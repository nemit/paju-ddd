package io.paju.salesorder.domain.event

import io.paju.ddd.AggregateRootId
import io.paju.ddd.EntityId
import io.paju.ddd.Event
import io.paju.salesorder.domain.DeliveryStatus
import io.paju.salesorder.domain.PaymentMethod
import io.paju.salesorder.domain.PaymentStatus
import io.paju.salesorder.domain.Product

// Sales order Events
data class SalesOrderCreated(
    val id: AggregateRootId,
    val customer: EntityId
)
data class SalesOrderDeleted(override val id: AggregateRootId) : Event()
data class SalesOrderConfirmed(override val id: AggregateRootId) : Event()

// Product Events
data class ProductAdded(
    override val id: AggregateRootId,
    val product: Product,
    val paymentStatus: PaymentStatus,
    val paymentMethod: PaymentMethod,
    val deliveryStatus: DeliveryStatus
) : Event()
data class ProductRemoved(override val id: AggregateRootId, val product: Product) : Event()
data class ProductDelivered(override val id: AggregateRootId, val product: Product) : Event()
data class ProductInvoiced(override val id: AggregateRootId, val product: Product) : Event()
data class ProductPayed(override val id: AggregateRootId, val product: Product) : Event()
