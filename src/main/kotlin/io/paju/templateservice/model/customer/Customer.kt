package io.paju.templateservice.model.customer

import java.util.*

/**
 * Customer is ENTITY representing customer account
 */
data class Customer(val customerId: CustomerId, val name: String, val contactPerson: Person)

data class CustomerId(val value: UUID)