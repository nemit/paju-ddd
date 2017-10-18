package io.paju.templateservice.model.salesorder

import io.paju.templateservice.model.customer.CustomerId
import io.paju.templateservice.model.customer.Person
import io.paju.templateservice.model.customer.sexEnumFromString
import io.paju.templateservice.model.product.*
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import java.time.LocalDate
import java.util.*

class SalesOrderRepository {
    val jdbi: Jdbi
    init {
        jdbi = Jdbi.create("jdbc:postgresql://localhost:5432/pajulahti", "postgres", "password")
        jdbi.installPlugins()
    }

    fun add(salesOrder: SalesOrder) {
        // TODO how to refactor to be based on UnitOfWork?

        jdbi.useExtension<SalesOrderDao, Exception>(SalesOrderDao::class.java) { dao: SalesOrderDao ->
            // Insert sales order
            dao.insert(salesOrder.toDb())

            // insert persons
            for (personAndRole in salesOrder.listParticipantsAndRoles()) {
                val person = dao.insert(personAndRole.participant.toDb())
                println("created person with id: " + person.id)
                val salesOrderMapping = PersonRoleInSalesOrderDb(salesOrder.id().value, person.id, personAndRole.role.toString())
                dao.insert(salesOrderMapping)
            }

            // products
            for (productAndStatus in salesOrder.listProducts()) {
                val product = dao.insert(productAndStatus.product.toDb())
                val salesOrderMapping = ProductsInSalesOrderDb(salesOrder.id().value, product.id,
                        productAndStatus.paymentStatus.toString(),
                        productAndStatus.paymentMethod.toString(),
                        productAndStatus.deliveryStatus.toString())
                dao.insert(salesOrderMapping)
            }

/*
            // services
            for (serviceAndStatus in salesOrder.listReservedServices()) {
                dao.insert(serviceAndStatus.service.toDb())
            }

            // services to sales order mapping
            for (servicesInSalesOrder in salesOrder.servicesToDb()) {
                dao.insert(servicesInSalesOrder)
            }
        }*/
        }
    }

    fun save(salesOrder: SalesOrder) {
        jdbi.useExtension<SalesOrderDao, Exception>(SalesOrderDao::class.java) { dao: SalesOrderDao ->
            // process UnitOfWork
            val unitOfWork = salesOrder.unitOfWork

            // group by type to secure order in which objects are inserted
            // Order of objects to be inserted
            // 1. SalesOrder
            // 2. Value Objects
            // 3. Mappings
            for (obj in unitOfWork.newObjects()) {
                when(obj) {
                    is SalesOrder -> dao.insert(obj.toDb())
                    is ParticipantAndRole -> insertParticipant(obj, salesOrder.id(), dao)
                    is ProductAndStatus -> insertProductAndStatus(obj, salesOrder.id(), dao)
                }
            }

            for (obj in unitOfWork.dirtyObjects()) {
                when(obj) {
                    is SalesOrder -> dao.update(obj.toDb())
                }
            }

            for(obj in unitOfWork.removedObjects()) {
                when(obj) {
                    is ParticipantAndRole -> dao.delete(obj.participant.toDb())
                    is ProductAndStatus -> dao.delete(obj.product.toDb())
                }
            }
        }
    }

    fun findAll(): List<SalesOrder> {
        return jdbi.withExtension<List<SalesOrder>, SalesOrderDao, Exception>(SalesOrderDao::class.java) { dao: SalesOrderDao ->
            val salesOrderlist = mutableListOf<SalesOrder>()
            val salesOrderDbs = dao.findAllSalesOrders()
            for (item in salesOrderDbs) {
                salesOrderlist.add(fetchSalesOrder(dao, item))
            }
            salesOrderlist
        }
    }

    fun salesOrderOfId(id: SalesOrderId): SalesOrder? {
        val dao = jdbi.onDemand(SalesOrderDao::class.java)
        val salesOrderDb = dao.findSalesOrderById(id)
        return fetchSalesOrder(dao, salesOrderDb)
    }

    //
    // Helper functions to return generated ID for the value objects
    //
    private fun insertParticipant(participantAndRole: ParticipantAndRole, salesOrderId: SalesOrderId, dao: SalesOrderDao) {
        val p = dao.insert(participantAndRole.participant.toDb())
        dao.insert(PersonRoleInSalesOrderDb(salesOrderId.value, p.id, participantAndRole.role.toString()))
    }

    private fun insertProductAndStatus(productAndStatus: ProductAndStatus, salesOrderId: SalesOrderId, dao: SalesOrderDao) {
        val p = dao.insert(productAndStatus.product.toDb())
        dao.insert(ProductsInSalesOrderDb(salesOrderId.value,
                p.id,
                productAndStatus.paymentStatus.toString(),
                productAndStatus.paymentMethod.toString(),
                productAndStatus.deliveryStatus.toString()))
    }

    private fun fetchSalesOrder(dao: SalesOrderDao, salesOrderDb: SalesOrderDb): SalesOrder {
        val id = SalesOrderId(salesOrderDb.id)
        val personRoleDbList = dao.findPersons(id)
        val productsDb = dao.findProducts(id)

        val personList = mutableListOf<ParticipantAndRole>()
        val orderedProducts = mutableListOf<ProductAndStatus>()
        val deliveredProducts = mutableListOf<ProductAndStatus>()

        for (db in productsDb) {
            val deliveryStatus = deliveryStatusFromString(db.delivery_status)
            val paymentMethod = paymentMethodFromString(db.payment_method)
            val paymentStatus = paymentStatusFromString(db.payment_status)
            val product = SellableProduct(Price(db.price, vatFromString(db.price_vat), Currencies.EURO), db.name, db.description)
            product.setValueObjectLocalId(db.id)
            if (deliveryStatus == DeliveryStatus.DELIVERED) {
                deliveredProducts.add(ProductAndStatus(paymentStatus, paymentMethod, product, DeliveryStatus.DELIVERED))
            } else {
                orderedProducts.add(ProductAndStatus(paymentStatus, paymentMethod, product, DeliveryStatus.NOT_DELIVERED))
            }
        }

        for (db in personRoleDbList) {
            val person = Person(db.date_of_birth, db.first_name, db.last_name, sexEnumFromString(db.sex))
            person.setValueObjectLocalId(db.id)
            personList.add(ParticipantAndRole(person, participantRoleFromString(db.role)))
        }

        return SalesOrder(customer = CustomerId(salesOrderDb.customer_id),
                participants = personList,
                id = SalesOrderId(salesOrderDb.id),
                confirmed = salesOrderDb.confirmed,
                deleted = salesOrderDb.deleted,
                orderedProducts = orderedProducts,
                deliveredProducts = deliveredProducts
        )
    }


}



