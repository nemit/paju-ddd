package io.paju.templateservice.model.salesoder

import io.paju.templateservice.model.customer.Person
import io.paju.templateservice.model.product.Product
import io.paju.templateservice.model.product.ReservedService
import io.paju.templateservice.model.product.SellableProduct
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import java.util.*

class SalesOrderRepository {
    init {
        val jdbi = Jdbi.create("jdbc:postgresql://localhost:5432/pajulahti", "postgres", "password")
        jdbi.installPlugins()
    }

    fun add(salesOrder: SalesOrder) {

    }

    fun save(salesOrder: SalesOrder) {
    }

    fun salesOrderOfId(id: SalesOrderId): SalesOrder? {
        return null
    }
}

private interface SalesOrderDao {
    @SqlUpdate("INSERT INTO sales_order (id, customer_id, confirmed, deleted) " +
            "VALUES (:so.id, :so.customer_id, :so.confirmed, :so.deleted)")
    fun insertSalesOrder(so: SalesOrderDb)
}

// DTOs matching database columns and extension functions to convert domain models to db models
data class PersonDb(val id: Long, val first_name: String, val last_name: String)
data class SalesOrderDb(val id: UUID, val customer_id: UUID, val confirmed: Boolean, val deleted: Boolean)
data class ReservedServiceDb(val id: UUID, val name: String,
                             val description: String,
                             val price: Float,
                             val price_currency: String,
                             val price_vat: String,
                             val start_date: Date,
                             val end_date: Date)
data class PersonRoleInSalesOrderDb(val sales_order_id: UUID, val person_id: Long, val role: String)
data class ReservedServicesInSalesOrder(val reserved_service_id: UUID,
                                        val sales_order_id: UUID,
                                        val payment_status: String,
                                        val payment_method: String,
                                        val delivery_status: String)
data class ProductDb(val id: Long, val name: String,
                     val description: String,
                     val price: Float,
                     val price_currency: String,
                     val price_vat: String)
data class ProductsInSalesOderDb(val sales_order_id: UUID,
                                 val product_id: Long,
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

