package io.paju.templateservice.infrastructure.repository

import io.paju.templateservice.domain.customer.Person
import io.paju.templateservice.domain.product.ReservedService
import io.paju.templateservice.domain.product.SellableProduct
import io.paju.templateservice.domain.salesorder.SalesOrder
import java.time.LocalDate
import java.util.*

//
// Data classes matching database tables
//

// sales_order table
data class SalesOrderDb(val id: UUID, val customer_id: UUID, val confirmed: Boolean, val deleted: Boolean)

// person table
data class PersonDb(val id: Long,
                    val first_name: String,
                    val last_name: String,
                    val sex: String,
                    val date_of_birth: LocalDate)

// persons_in_ssales_order table
data class PersonRoleInSalesOrderDb(val sales_order_id: UUID, val person_id: Long, val role: String)

// query domain from select join
// TODO: Best way to handle query models? a) data class like here b) custom row mapper c) map models?
data class PersonRoleDb(val id: Long,
                        val first_name: String,
                        val last_name: String,
                        val sex: String,
                        val date_of_birth: LocalDate,
                        val role: String)

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
// TODO: Best way to handle query models? a) data class like here b) custom row mapper c) map models?
data class ProductAndDeliveryStatusDb(val id: Long, val name: String,
                                      val description: String,
                                      val price: Float,
                                      val price_currency: String,
                                      val price_vat: String,
                                      val payment_status: String,
                                      val payment_method: String,
                                      val delivery_status: String)

//
// Extension functions to convert domain objects to DB objects
//

fun Person.toDb(): PersonDb {
    return PersonDb(this.valueObjectLocalId(), this.firstName, this.lastName, this.sex.toString(), this.dateOfBirth)
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
    return ProductDb(this.valueObjectLocalId(),
            this.name,
            this.description,
            this.price.price,
            this.price.currency.toString(),
            this.price.vat.toString())
}

fun SalesOrder.toDb(): SalesOrderDb {
    return SalesOrderDb(this.id().value, this.customer.value, this.confirmed, this.deleted)
}


