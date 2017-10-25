package io.paju.templateservice.infrastructure

import io.paju.templateservice.domain.TestData.customer
import io.paju.templateservice.domain.TestData.person1
import io.paju.templateservice.domain.TestData.product1
import io.paju.templateservice.domain.TestData.product2
import io.paju.templateservice.domain.salesorder.*
import io.paju.templateservice.infrastructure.repository.SalesOrderJdbiRepository
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class SalesOrderJdbiRepositoryTest {

    @Test
    fun addNewSalesOrder() {
        val salesOrder = SalesOrderFactory.createNew(customer.customerId)
        salesOrder.addParticipant(person1)
        salesOrder.addProduct(product1)
        salesOrder.addProduct(product2)
        salesOrder.deliverProduct(product1)
        val repo = SalesOrderJdbiRepository()
        repo.save(salesOrder)

        val salesOrderFromDb = repo.salesOrderOfId(salesOrder.id())
        assertNotNull(salesOrderFromDb)
        assert(salesOrderFromDb!!.listProducts().filter {
            p -> p.deliveryStatus == DeliveryStatus.DELIVERED
        }.isNotEmpty())
        assert(salesOrderFromDb.listProducts().filter {
            p -> p.deliveryStatus == DeliveryStatus.NOT_DELIVERED
        }.isNotEmpty())
    }

    @Test
    fun addAndModify() {
        val salesOrder = SalesOrderFactory.createNew(customer.customerId)
        salesOrder.addParticipant(person1)
        salesOrder.addProduct(product1)
        salesOrder.addProduct(product2)
        salesOrder.deliverProduct(product1)
        val repo = SalesOrderJdbiRepository()
        repo.save(salesOrder)

        val salesOrderFromDb = repo.salesOrderOfId(salesOrder.id())
        assertNotNull(salesOrderFromDb)
        assert(salesOrderFromDb!!.listProducts().filter {
            p -> p.deliveryStatus == DeliveryStatus.DELIVERED
        }.isNotEmpty())
        assert(salesOrderFromDb.listProducts().filter {
            p -> p.deliveryStatus == DeliveryStatus.NOT_DELIVERED
        }.isNotEmpty())

        salesOrderFromDb.removeParticipant(person1)
        salesOrderFromDb.deliverProduct(product2)
        salesOrderFromDb.confirmSalesOrder()

        repo.save(salesOrderFromDb)

        val salesOrderFromDb2 = repo.salesOrderOfId(salesOrder.id())
        assertNotNull(salesOrderFromDb2)
        //assertTrue(salesOrderFromDb2!!.confirmed)
        assertTrue(salesOrderFromDb2!!.listParticipantsAndRoles().isEmpty())
        assert(salesOrderFromDb2.listProducts().filter {
            p -> p.deliveryStatus == DeliveryStatus.NOT_DELIVERED
        }.size == 1)
    }

    @Test
    fun findAll() {
        val repo = SalesOrderJdbiRepository()
        val list = repo.findAll()
        assertTrue(list.isNotEmpty())
    }
}