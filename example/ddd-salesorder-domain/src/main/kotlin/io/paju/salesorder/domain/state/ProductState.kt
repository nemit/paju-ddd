package io.paju.salesorder.domain.state

import io.paju.ddd.EntityId
import io.paju.ddd.state.EntityState
import io.paju.salesorder.domain.DeliveryStatus
import io.paju.salesorder.domain.PaymentMethod
import io.paju.salesorder.domain.PaymentStatus
import io.paju.salesorder.domain.Product

data class ProductState(
    val product: Product,
    var paymentStatus: PaymentStatus,
    var paymentMethod: PaymentMethod,
    val deliveryStatus: DeliveryStatus
) : EntityState {
    override val id: EntityId = product.id
}