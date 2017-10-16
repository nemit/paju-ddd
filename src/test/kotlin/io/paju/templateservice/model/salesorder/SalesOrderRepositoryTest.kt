package io.paju.templateservice.model.salesorder

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class SalesOrderRepositoryTest {
    @Test
    fun add() {
        val salesOrder = SalesOrder.createNewSalesOrder(customer.customerId)
        salesOrder.addParticipant(person1)
        salesOrder.addProduct(product1)
        salesOrder.addProduct(product2)
        salesOrder.deliverProduct(product1)
        val repo = SalesOrderRepository()
        repo.add(salesOrder)

        val salesOrderFromDb = repo.salesOrderOfId(salesOrder.id())
        assertNotNull(salesOrderFromDb)
        assert(salesOrder.listProducts().filter {
            p -> p.deliveryStatus == DeliveryStatus.DELIVERED }.isNotEmpty())
        assert(salesOrder.listProducts().filter {
            p -> p.deliveryStatus == DeliveryStatus.NOT_DELIVERED }.isNotEmpty())

    }

}