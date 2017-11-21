package io.paju.salesorder.infrastructure.ports.controllers

import io.paju.ddd.AggregateRootId
import io.paju.salesorder.infrastructure.SalesOrderRepository
import io.paju.salesorder.infrastructure.ports.resources.SalesOrderResource
import io.paju.salesorder.infrastructure.ports.resources.SalesOrderResourceAssembler
import io.paju.salesorder.infrastructure.ports.SpringRestPort
import org.slf4j.LoggerFactory
import org.springframework.hateoas.ExposesResourceFor
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@ExposesResourceFor(SalesOrderResource::class)
@RequestMapping("/salesorders")
class SalesOrderController(val repository: SalesOrderRepository) {

    private val logger = LoggerFactory.getLogger(SpringRestPort::class.java)

    @GetMapping("/{id}")
    fun findById(@PathVariable("id") id: String): ResponseEntity<SalesOrderResource> {
        logger.debug("id is: $id")
        val salesOrder = repository.getById(AggregateRootId(id))
        logger.debug("salesorder is: $salesOrder")
        val assembler = SalesOrderResourceAssembler()
        return ResponseEntity<SalesOrderResource>(assembler.toResource(salesOrder.state()), HttpStatus.OK)
    }
}