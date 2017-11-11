package io.paju.salesorder.service

import io.paju.ddd.EntityId
import io.paju.salesorder.domain.PaymentMethod
import io.paju.salesorder.domain.Product

object DummyPaymentService {

    @Suppress("unused_parameter")
    fun handleProductPayment(product: Product, customerId: EntityId, paymentMethod: PaymentMethod) {
        // do nothing
    }
}