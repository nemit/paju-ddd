package io.paju.salesorder.infrastructure.ports.resources

import org.springframework.hateoas.ResourceSupport

/*
This is here just to mock Customer service so that links to /customer work
*/

// This represents non-existing Customer Aggregate
data class Customer(val firstName: String, val lastName: String, val id: String)

data class CustomerResource(val firstName: String, val lastName: String, val id: String) : ResourceSupport()
