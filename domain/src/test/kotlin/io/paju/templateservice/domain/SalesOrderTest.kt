package io.paju.templateservice

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import io.paju.templateservice.domain.salesorder.*
import io.paju.templateservice.domain.TestData.customer
import io.paju.templateservice.domain.TestData.person1
import io.paju.templateservice.domain.TestData.product1
import io.paju.templateservice.domain.TestData.product2

import io.paju.templateservice.domain.services.PaymentService
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class SalesOrderTest {

    val paymentServiceMock = mock<PaymentService>()
    @Test
    fun addProduct() {
        val so = SalesOrderFactory.createNew(customer.customerId)
        so.addProduct(product1)
        val products = so.listProducts()
        assertEquals(1, products.size)
        val productAndPaymentStatus = products.get(0)

        assertTrue(product1.equals(productAndPaymentStatus.product), "added product does not match product in the list")
        assertEquals(productAndPaymentStatus.paymentStatus, PaymentStatus.OPEN)
    }

    @Test
    fun removeProduct() {
        val so = SalesOrderFactory.createNew(customer.customerId)
        so.addProduct(product1)
        so.addProduct(product2)
        so.addProduct(product1)
        so.removeProduct(product2)

        val products = so.listProducts()
        assertEquals(2, products.size)
        for (productAndStatus in products) {
            assertTrue(product1.equals(productAndStatus.product), "expecting product")
            assertEquals(productAndStatus.paymentStatus, PaymentStatus.OPEN)
        }
    }

    @Test
    fun addReservedService() {
    }

    @Test
    fun removeReservedService() {
    }

    @Test
    fun deliverService() {
    }

    @Test
    fun deliverProduct() {
        val so = SalesOrderFactory.createNew(customer.customerId)
        so.addProduct(product1)
        so.addProduct(product2)
        so.addProduct(product1)
        so.deliverProduct(product1)

        val products = so.listProducts()
        assertEquals(3, products.size)
        assertNotNull(products.find { productAndStatus: ProductAndStatus -> productAndStatus.product.equals(product1) && productAndStatus.deliveryStatus == DeliveryStatus.DELIVERED })
    }

    @Test
    fun invoiceDeliveredProductsAndServices() {
        val so = SalesOrderFactory.createNew(customer.customerId)
        so.addProduct(product1)
        so.addProduct(product2)
        so.addProduct(product1)
        so.deliverProduct(product1)

        so.invoiceDeliveredProductsAndServices(paymentServiceMock)
        verify(paymentServiceMock, times(1)).handleProductPayment(product1, customer.customerId, PaymentMethod.INVOICE)
    }

    @Test
    fun payDeliveredProduct() {
        val so = SalesOrderFactory.createNew(customer.customerId)
        so.addProduct(product1)
        so.addProduct(product2)
        so.addProduct(product1)

        so.deliverProduct(product1)

        so.payDeliveredProduct(paymentServiceMock, product1, PaymentMethod.CASH)
        verify(paymentServiceMock, times(1)).handleProductPayment(product1, customer.customerId, PaymentMethod.CASH)
    }

    @Test
    fun payDeliveredServices() {
    }

    @Test
    fun addParticipant() {
        val expectedParticipantAndRole = ParticipantAndRole(person1, ParticipantRole.OTHER)

        val so = SalesOrderFactory.createNew(customer.customerId)
    //    so.addCustomerContactAsParticipant()
        so.addParticipant(person1)
        val participantsAndRoles = so.listParticipantsAndRoles()
        assertEquals(1, participantsAndRoles.size)
        assertNotNull(participantsAndRoles.find { it.equals(expectedParticipantAndRole) })
    }

    @Test
    fun removeParticipant() {
        val expectedParticipantAndRole = ParticipantAndRole(customer.contactPerson, ParticipantRole.ORGANIZER)

        val so = SalesOrderFactory.createNew(customer.customerId)
      //  so.addCustomerContactAsParticipant()
        so.addParticipant(person1)
        so.removeParticipant(person1)

        val participantsAndRoles = so.listParticipantsAndRoles()
        assertEquals(0, participantsAndRoles.size)
  //      assertNotNull(participantsAndRoles.find { it.equals(expectedParticipantAndRole) })
    }

    @Test
    fun cancelSalesOrder() {
    }

    @Test
    fun status() {
        val so = SalesOrderFactory.createNew(customer.customerId)
        assertEquals(Status.QUOTE, so.state())

        so.addProduct(product1)
        so.addProduct(product2)
        so.confirmSalesOrder()
        assertEquals(Status.CONFIRMED, so.state())

        so.deliverProduct(product1)
        assertEquals(Status.PARTIALLY_DELIVERED, so.state())

        so.deliverProduct(product2)
        assertEquals(Status.DELIVERED, so.state())
    }
}