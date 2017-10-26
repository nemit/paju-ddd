package io.paju.templateservice.infrastructure.repository

import io.paju.templateservice.domain.customer.CustomerId
import io.paju.templateservice.domain.customer.Person
import io.paju.templateservice.domain.customer.sexEnumFromString
import io.paju.templateservice.domain.product.*
import io.paju.templateservice.domain.salesorder.*
import io.paju.templateservice.shared.AbstractRepository
import org.jdbi.v3.core.Jdbi


class SalesOrderJdbiRepository : AbstractRepository(), SalesOrderRepository {
    val jdbi: Jdbi
    init {
        jdbi = Jdbi.create("jdbc:postgresql://localhost:5432/pajulahti", "postgres", "password")
        jdbi.installPlugins()
    }

    override fun save(salesOrder: SalesOrder) {
        val personDao = PersonDao(jdbi)
        val productDao = jdbi.onDemand(ProductDao::class.java)
        val salesOrderDao = jdbi.onDemand(SalesOrderDao::class.java)

        val data = SalesOrderFactory.salesOrderData({ id: SalesOrderId,
                                                      confirmed: Boolean,
                                                      deleted: Boolean ->
            SalesOrderInternalDataWrapper(confirmed, deleted, id)
        }, salesOrder)

        val mediator = salesOrder.repositoryMediator

        // group by type to secure order in which objects are inserted
        // Order of objects to be inserted
        // 1. SalesOrder
        // 2. Value Objects
        // 3. Mappings
        //TODO sort by type
        for (obj in mediator.newObjects()) {
            when (obj) {
                is SalesOrder -> salesOrderDao.insert(data.id, salesOrder.customer, data.confirmed, data.deleted)
                is ParticipantAndRole -> insertParticipant(personDao, obj, salesOrder.id())
                is ProductAndStatus -> insertProductAndStatus(productDao, obj, salesOrder.id())
            }
        }

        for (obj in mediator.dirtyObjects()) {
            when (obj) {
                is SalesOrder -> salesOrderDao.update(data.id, obj.customer, data.confirmed, data.deleted)
            }
        }

        for (obj in mediator.removedObjects()) {
            when (obj) {
                is ParticipantAndRole -> personDao.delete(obj.participant.toDb())
                is ProductAndStatus -> productDao.delete(obj.product.toDb())
            }
        }
    }

    override fun findAll(): List<SalesOrder> {
        val personDao = PersonDao(jdbi)
        val productDao = jdbi.onDemand(ProductDao::class.java)
        val salesOrderDao = jdbi.onDemand(SalesOrderDao::class.java)
        val salesOrderlist = mutableListOf<SalesOrder>()
        val salesOrderDbs = salesOrderDao.findAllSalesOrders()
        for (item in salesOrderDbs) {
            salesOrderlist.add(fetchSalesOrder(personDao, productDao, item))
        }
        return salesOrderlist

    }

    override fun salesOrderOfId(id: SalesOrderId): SalesOrder? {
        val dao = jdbi.onDemand(SalesOrderDao::class.java)
        val productDao = jdbi.onDemand(ProductDao::class.java)
        val personDao = PersonDao(jdbi)
        val salesOrderData = dao.findSalesOrderById(id)
        return fetchSalesOrder(personDao, productDao, salesOrderData)
    }

    //
    // Helper functions to insert and fetch aggregate members in proper order
    //
    private fun insertParticipant(dao: PersonDao, participantAndRole: ParticipantAndRole, salesOrderId: SalesOrderId) {
        val p = dao.insert(participantAndRole.participant.toDb())
        dao.insert(PersonRoleInSalesOrderDb(salesOrderId.value, p.id, participantAndRole.role.toString()))
    }

    private fun insertProductAndStatus(dao: ProductDao, productAndStatus: ProductAndStatus, salesOrderId: SalesOrderId) {
        val p = dao.insert(productAndStatus.product.toDb())
        dao.insert(ProductsInSalesOrderDb(salesOrderId.value,
                p.id,
                productAndStatus.paymentStatus.toString(),
                productAndStatus.paymentMethod.toString(),
                productAndStatus.deliveryStatus.toString()))
    }

    private fun fetchSalesOrder(personDao: PersonDao, productDao: ProductDao, data: SalesOrderResult): SalesOrder {
        val id = SalesOrderId(data.id)

        val persons = personDao.findPersons(id).map { person ->
            val pers = Person(person.date_of_birth,
                    person.first_name,
                    person.last_name,
                    sexEnumFromString(person.sex))
            pers.setValueObjectLocalId(person.id)
            ParticipantAndRole(pers, participantRoleFromString(person.role))
        }

        val products = productDao.findProducts(id).map { product ->
            val prod = SellableProduct(Price(product.price, vatFromString(product.price_vat), Currencies.EURO),
                    product.name,
                    product.description)
            prod.setValueObjectLocalId(product.id) // TODO: Figure something smart with this ..
            ProductAndStatus(paymentStatusFromString(product.payment_status),
                    paymentMethodFromString(product.payment_method), prod,
                    deliveryStatusFromString(product.delivery_status))
        }

        val services = listOf<ServiceAndStatus>() // NOT IMPLEMENTED

        val internalData = object : SalesOrderInternalData {
            override val deleted: Boolean = data.deleted
            override val confirmed: Boolean = data.confirmed
        }

        return SalesOrderFactory.reconstitute(SalesOrderId(data.id), CustomerId(data.customer_id), products, services, persons, internalData)
    }
}



