package io.paju.salesorder.infrastructure.ports.controllers

import io.paju.ddd.AggregateRootId
import io.paju.ddd.EntityId
import io.paju.salesorder.command.DeliverProduct
import io.paju.salesorder.command.PayDeliveredProduct
import io.paju.salesorder.command.SalesOrderCommandHandler
import io.paju.salesorder.infrastructure.SalesOrderRepository
import io.paju.salesorder.infrastructure.ports.SpringRestPort
import io.paju.salesorder.infrastructure.ports.resources.ProductDelivery
import io.paju.salesorder.infrastructure.ports.resources.ProductPayment
import io.paju.salesorder.infrastructure.ports.resources.SalesOrderResource
import io.paju.salesorder.infrastructure.ports.resources.SalesOrderResourceAssembler
import org.slf4j.LoggerFactory
import org.springframework.hateoas.EntityLinks
import org.springframework.hateoas.ExposesResourceFor
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@ExposesResourceFor(SalesOrderResource::class)
@RequestMapping("/salesorders")
class SalesOrderController(val repository: SalesOrderRepository, val commandHandler: SalesOrderCommandHandler, val entityLinks: EntityLinks) {

    private val logger = LoggerFactory.getLogger(SpringRestPort::class.java)

    @GetMapping("/{id}")
    fun findById(@PathVariable("id") id: String): ResponseEntity<SalesOrderResource> {
        logger.debug("id is: $id")
        val salesOrder = repository.getById(AggregateRootId(id))
        logger.debug("salesorder is: $salesOrder")
        val assembler = SalesOrderResourceAssembler(entityLinks)
        return ResponseEntity<SalesOrderResource>(assembler.toResource(salesOrder), HttpStatus.OK)
    }

    @PutMapping("/{id}/payment")
    fun payAllDeliveredProducts(@PathVariable("id") id: String, @RequestBody payment: ProductPayment): ResponseEntity<Void> {
        val cmd = PayDeliveredProduct(AggregateRootId(id), 1, EntityId(payment.productId), payment.paymentMethod)
        commandHandler.handle(cmd)
        return ResponseEntity<Void>(HttpStatus.OK)
    }

    @PutMapping("/{id}/delivery")
    fun deliverProduct(@PathVariable("id") id: String, @RequestBody delivery: ProductDelivery): ResponseEntity<Void> {
        val cmd = DeliverProduct(AggregateRootId(id), 1, EntityId(delivery.productId))
        commandHandler.handle(cmd)
        return ResponseEntity<Void>(HttpStatus.OK)
    }

}