package io.paju.salesorder.infrastructure.ports.resources

import io.paju.ddd.EntityId
import io.paju.salesorder.domain.PaymentMethod

data class ProductPayment(val productId: EntityId, val paymentMethod: PaymentMethod)
data class ProductDelivery(val productId: EntityId)