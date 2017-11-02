package io.paju.templateservice.infrastructure.repository.salesorder

import io.paju.templateservice.domain.product.SellableProduct
import java.util.*

//
// Data classes matching database tables
//

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

fun SellableProduct.toDb(): ProductDb {
    return ProductDb(this.valueObjectLocalId(),
            this.name,
            this.description,
            this.price.price,
            this.price.currency.toString(),
            this.price.vat.toString())
}


