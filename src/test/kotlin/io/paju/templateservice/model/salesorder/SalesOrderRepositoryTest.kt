package io.paju.templateservice.model.salesorder

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class SalesOrderRepositoryTest {
    @Test
    fun addAndModify() {
        val salesOrder = SalesOrder.createNewSalesOrder(customer.customerId)
        salesOrder.addParticipant(person1)
        salesOrder.addProduct(product1)
        salesOrder.addProduct(product2)
        salesOrder.deliverProduct(product1)
        val repo = SalesOrderRepository()
        repo.add(salesOrder)

        val salesOrderFromDb = repo.salesOrderOfId(salesOrder.id())
        assertNotNull(salesOrderFromDb)
        assert(salesOrderFromDb!!.listProducts().filter {
            p -> p.deliveryStatus == DeliveryStatus.DELIVERED }.isNotEmpty())
        assert(salesOrderFromDb!!.listProducts().filter {
            p -> p.deliveryStatus == DeliveryStatus.NOT_DELIVERED }.isNotEmpty())

        salesOrderFromDb!!.removeParticipant(person1)
        salesOrderFromDb!!.deliverProduct(product2)
        salesOrderFromDb.confirmSalesOrder()

        repo.save(salesOrderFromDb)

        val salesOrderFromDb2 = repo.salesOrderOfId(salesOrder.id())
        assertNotNull(salesOrderFromDb2)
        assertTrue(salesOrderFromDb2!!.confirmed)
        assertTrue(salesOrderFromDb2.listParticipantsAndRoles().isEmpty())
        assert(salesOrderFromDb2.listProducts().filter {
            p -> p.deliveryStatus == DeliveryStatus.NOT_DELIVERED }.isEmpty())
    }

    @Test
    fun findAll() {
        val repo = SalesOrderRepository()
        val list = repo.findAll()
        assertTrue(list.isNotEmpty())
    }

}