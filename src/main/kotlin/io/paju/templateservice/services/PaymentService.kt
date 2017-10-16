package io.paju.templateservice.services

import io.paju.templateservice.model.customer.Customer
import io.paju.templateservice.model.customer.CustomerId
import io.paju.templateservice.model.product.Product
import io.paju.templateservice.model.salesoder.PaymentMethod

object PaymentService {
    fun handleProductPayment(product: Product, customer: CustomerId, paymentMethod: PaymentMethod) {
        // do nothing
    }
}