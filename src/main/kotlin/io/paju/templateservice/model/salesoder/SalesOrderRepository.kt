package io.paju.templateservice.model.salesoder

import io.paju.templateservice.model.customer.Person
import io.paju.templateservice.model.product.ReservedService
import io.paju.templateservice.model.product.SellableProduct
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.sqlobject.kotlin.onDemand
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys
import org.jdbi.v3.sqlobject.statement.MapTo
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import java.util.*

class SalesOrderRepository {
    val jdbi: Jdbi
    init {
        jdbi = Jdbi.create("jdbc:postgresql://localhost:5432/pajulahti", "postgres", "password")
        jdbi.installPlugins()
    }

    fun add(salesOrder: SalesOrder) {
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
                /*
                val product = jdbi.withHandle<ProductDb, Exception> { handle ->
                    handle.createUpdate("INSERT INTO product (name, description, price, price_currency, price_vat) " +
                            "VALUES (?, ?, ?, ?, ?)")
                            .bind(0, data.name)
                            .bind(1, data.description)
                            .bind(2, data.price)
                            .bind(3, data.price_currency)
                            .bind(4, data.price_vat)
                            .executeAndReturnGeneratedKeys()
                            .mapTo(ProductDb::class.java)
                            .findOnly()
                }
                */

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
    }

    fun salesOrderOfId(id: SalesOrderId): SalesOrder? {
        val dao = jdbi.onDemand(SalesOrderDao::class.java)
        val salesOrderDb = dao.findSalesOrderById(id)
        //val personRoleDb = dao.findPersonsBySalesOrder()

        return null
    }
}

interface SalesOrderDao {
    @SqlUpdate("INSERT INTO sales_order (id, customer_id, confirmed, deleted) " +
            "VALUES (:data.id, :data.customer_id, :data.confirmed, :data.deleted)")
    fun insert(data: SalesOrderDb)

    @SqlUpdate("INSERT INTO person (first_name, last_name) " +
            "VALUES (:data.first_name, :data.last_name)"  )
    @GetGeneratedKeys()
    fun insert(data: PersonDb): PersonDb

    @SqlUpdate("INSERT INTO reserved_service (id, name, description, price, price_currency, price_vat, service_id, " +
            "date_start, date_end) VALUES (:data.id, :data.name, :data.description, :data.price, :data.price_currency," +
            "data:price_vat, data: service_id" )
    fun insert(data: ReservedServiceDb)

    @SqlUpdate("INSERT INTO reserved_services_in_sales_order (reserved_service_id, sales_order_id, payment_status," +
            "payment_method, delivery_status) VALUES (:data.reserved_service_id, :data.sales_order_id, :data.payment_status," +
            ":data.payment_method, :data.delivery_status)")
    fun insert(data: ReservedServicesInSalesOrder)

    @SqlUpdate("INSERT INTO person_role_in_sales_order (sales_order_id, person_id, role) " +
            "VALUES (:data.sales_order_id, :data.person_id, :data.role)")
    @GetGeneratedKeys
    fun insert(data: PersonRoleInSalesOrderDb): PersonRoleInSalesOrderDb

    @SqlUpdate("INSERT INTO product (name, description," +
            "price, price_currency, price_vat) " +
            "VALUES (:data.name, :data.description, :data.price, :data.price_currency, :data.price_vat)")
    @GetGeneratedKeys
    fun insert(data: ProductDb): ProductDb

    @SqlUpdate("INSERT INTO products_in_sales_order (sales_order_id, product_id, payment_status, payment_method, delivery_status) " +
            "VALUES (:data.sales_order_id, :data.product_id, :data.payment_status, :data.payment_method, :data.delivery_status)")
    fun insert(data: ProductsInSalesOrderDb)

    @SqlQuery("SELECT * FROM sales_order WHERE id = :data.id.value")
    fun findSalesOrderById(data: SalesOrderId): SalesOrderDb

    @SqlQuery("SELECT p.*, r.role FROM person p, person_role_in_sales_order r WHERE m.sales_order = :id.value")
    fun findPersonsBySalesOrder(id: SalesOrderId): List<Pair<PersonDb, PersonRoleInSalesOrderDb>>



/*
    fun update(data: SalesOrderDb)
    fun update(data: PersonDb)
    fun update(data: ReservedServiceDb)
    fun update(data: PersonRoleInSalesOrderDb)*/


}

// DTOs matching database columns and extension functions to convert domain models to db models
data class SalesOrderDb(val id: UUID, val customer_id: UUID, val confirmed: Boolean, val deleted: Boolean)

data class PersonDb(val id: Long, val first_name: String, val last_name: String)
data class PersonRoleInSalesOrderDb(val sales_order_id: UUID, val person_id: Long, val role: String)


data class ReservedServiceDb(val id: UUID, val name: String,
                             val description: String,
                             val price: Float,
                             val price_currency: String,
                             val price_vat: String,
                             val start_date: Date,
                             val end_date: Date)

data class ReservedServicesInSalesOrder(val reserved_service_id: UUID,
                                        val sales_order_id: UUID,
                                        val payment_status: String,
                                        val payment_method: String,
                                        val delivery_status: String)

data class ReservedServiceAndDeliveryStatusDb(val id: UUID, val name: String,
                                              val description: String,
                                              val price: Float,
                                              val price_currency: String,
                                              val price_vat: String,
                                              val start_date: Date,
                                              val end_date: Date,
                                              val payment_status: String,
                                              val payment_method: String,
                                              val delivery_status: String)

data class ProductDb(val id: Long, val name: String,
                     val description: String,
                     val price: Float,
                     val price_currency: String,
                     val price_vat: String)
data class ProductsInSalesOrderDb(val sales_order_id: UUID,
                                  val product_id: Long,
                                  val payment_status: String,
                                  val payment_method: String,
                                  val delivery_status: String)
data class ProductAndDeliveryStatus(val id: Long, val name: String,
                                    val description: String,
                                    val price: Float,
                                    val price_currency: String,
                                    val price_vat: String,
                                    val payment_status: String,
                                    val payment_method: String,
                                    val delivery_status: String)

// SalesOrder.toDb() implemented in SalesOrder because it requires internal state
// TODO WHERE TO PLACE THE CONVERSION FUNCTIONS?
fun Person.toDb(): PersonDb {
    return PersonDb(this.valueObjectLocalId().id, this.firstName, this.lastName)
}

fun ReservedService.toDb(): ReservedServiceDb {
    return ReservedServiceDb(this.reservedServiceId.value,
            this.name,
            this.description,
            this.price.price,
            this.price.currency.toString(),
            this.price.vat.toString(),
            this.reservationPeriod.start,
            this.reservationPeriod.end)
}

fun SellableProduct.toDb(): ProductDb {
    return ProductDb(this.valueObjectLocalId().id,
            this.name,
            this.description,
            this.price.price,
            this.price.currency.toString(),
            this.price.vat.toString())
}

