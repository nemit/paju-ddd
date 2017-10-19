package io.paju.templateservice.domain.services

import io.paju.templateservice.domain.customer.CustomerId
import io.paju.templateservice.domain.product.Product
import io.paju.templateservice.domain.salesorder.PaymentMethod

object PaymentService {
    fun handleProductPayment(product: Product, customer: CustomerId, paymentMethod: PaymentMethod) {
        // do nothing
    }
}