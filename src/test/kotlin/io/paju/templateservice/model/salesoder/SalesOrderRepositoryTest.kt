package io.paju.templateservice.model.salesoder

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class SalesOrderRepositoryTest {
    @Test
    fun add() {
        val salesOrder = SalesOrder.createNewSalesOrder(customer)
        salesOrder.addParticipant(person1)
        salesOrder.addCustomerContactAsParticipant()
        salesOrder.addProduct(product1)
        salesOrder.addProduct(product2)
        val repo = SalesOrderRepository()
        repo.add(salesOrder)
    }
}