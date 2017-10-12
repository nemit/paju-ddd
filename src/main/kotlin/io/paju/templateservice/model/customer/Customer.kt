package io.paju.templateservice.model.customer

import java.util.*

/**
 * Customer is ENTITY representing customer account
 */
class Customer(customerId: CustomerId, name: String, contactPerson: Person) {
}

data class CustomerId(val value: UUID)