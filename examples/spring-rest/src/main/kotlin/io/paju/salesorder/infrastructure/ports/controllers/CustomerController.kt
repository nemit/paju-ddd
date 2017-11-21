package io.paju.salesorder.infrastructure.ports.controllers

import io.paju.salesorder.infrastructure.ports.resources.Customer
import io.paju.salesorder.infrastructure.ports.resources.CustomerResource
import io.paju.salesorder.infrastructure.ports.resources.CustomerResourceAssember
import org.springframework.hateoas.ExposesResourceFor
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/*
 This is here just to mock Customer service so that links to /customer work
 */

@RestController
@RequestMapping("/customers")
@ExposesResourceFor(CustomerResource::class)
class CustomerController {

    @GetMapping("/{id}")
    fun findById(@PathVariable id: String): ResponseEntity<CustomerResource> {
        val assembler = CustomerResourceAssember()
        val r = assembler.toResource(Customer("Kimmo", "Eklund", id))
        return ResponseEntity.ok(r)
    }
}
