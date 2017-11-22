package io.paju.salesorder.infrastructure.ports.resources

import io.paju.salesorder.domain.PaymentMethod

data class ProductPayment(val productId: String, val paymentMethod: PaymentMethod)
data class ProductDelivery(val productId: String)