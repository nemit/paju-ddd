package io.paju.salesorder.domain.event

import com.fasterxml.jackson.annotation.JsonTypeInfo
import io.paju.ddd.EntityId
import io.paju.ddd.StateChangeEvent
import io.paju.salesorder.domain.DeliveryStatus
import io.paju.salesorder.domain.PaymentMethod
import io.paju.salesorder.domain.PaymentStatus
import io.paju.salesorder.domain.Product

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type")
sealed class SalesOrderEvent : StateChangeEvent() {

    data class CustomerSet(val customerId: EntityId) : SalesOrderEvent()
    object Deleted : SalesOrderEvent()
    object Confirmed : SalesOrderEvent()

    data class ProductAdded(
        val product: Product,
        val paymentStatus: PaymentStatus,
        val paymentMethod: PaymentMethod,
        val deliveryStatus: DeliveryStatus
    ) : SalesOrderEvent()
    data class ProductRemoved(val product: Product) : SalesOrderEvent()
    data class ProductDelivered(val product: Product) : SalesOrderEvent()
    data class ProductInvoiced(val product: Product) : SalesOrderEvent()
    data class ProductPaid(val product: Product) : SalesOrderEvent()
}