package io.paju.salesorder.domain.event

import io.paju.ddd.AggregateRootId
import io.paju.ddd.EntityId
import io.paju.ddd.StateChangeEvent
import io.paju.salesorder.domain.DeliveryStatus
import io.paju.salesorder.domain.PaymentMethod
import io.paju.salesorder.domain.PaymentStatus
import io.paju.salesorder.domain.Product

sealed class SalesOrderEvent : StateChangeEvent() {

    data class CustomerSet(override val id: AggregateRootId, val customer: EntityId) : SalesOrderEvent()
    data class Deleted(override val id: AggregateRootId) : SalesOrderEvent()
    data class Confirmed(override val id: AggregateRootId) : SalesOrderEvent()

    // Product Events
    data class ProductAdded(
        override val id: AggregateRootId,
        val product: Product,
        val paymentStatus: PaymentStatus,
        val paymentMethod: PaymentMethod,
        val deliveryStatus: DeliveryStatus
    ) : SalesOrderEvent()

    data class ProductRemoved(override val id: AggregateRootId, val product: Product) : SalesOrderEvent()
    data class ProductDelivered(override val id: AggregateRootId, val product: Product) : SalesOrderEvent()
    data class ProductInvoiced(override val id: AggregateRootId, val product: Product) : SalesOrderEvent()
    data class ProductPaid(override val id: AggregateRootId, val product: Product) : SalesOrderEvent()
}