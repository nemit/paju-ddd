package io.paju.salesorder.infrastructure.ports

import io.paju.ddd.AggregateRootId
import io.paju.salesorder.infrastructure.SalesOrderRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class SalesOrderController(val repository: SalesOrderRepository) {

    @GetMapping("/salesorders/{id}")
    fun findById(@PathVariable("id") id: String) = repository.getById(AggregateRootId(id))
}