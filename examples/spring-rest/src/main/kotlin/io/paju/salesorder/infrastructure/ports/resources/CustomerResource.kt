package io.paju.salesorder.infrastructure.ports.resources

import org.springframework.hateoas.ResourceSupport
import org.springframework.hateoas.core.Relation

/*
This is here just to mock Customer service so that links to /customer work
*/

// This represents non-existing Customer Aggregate
data class Customer(val firstName: String, val lastName: String, val id: String)

@Relation(value = "customer", collectionRelation = "customers")
data class CustomerResource(val firstName: String, val lastName: String, val id: String) : ResourceSupport()
