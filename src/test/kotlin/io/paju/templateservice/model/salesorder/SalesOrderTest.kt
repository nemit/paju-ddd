package io.paju.templateservice.model.salesorder

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import io.paju.templateservice.services.PaymentService
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class SalesOrderTest {



    val paymentServiceMock = mock<PaymentService>()

    @Test
    fun addProduct() {
        val so = SalesOrder.createNewSalesOrder(customer.customerId)
        so.addProduct(product1)
        val products = so.listProducts()
        assertEquals(1, products.size)
        val productAndPaymentStatus = products.get(0)

        assertTrue(product1.equals(productAndPaymentStatus.product), "added product does not match product in the list")
        assertEquals(productAndPaymentStatus.paymentStatus, PaymentStatus.OPEN)
    }

    @Test
    fun removeProduct() {
        val so = SalesOrder.createNewSalesOrder(customer.customerId)
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
        val so = SalesOrder.createNewSalesOrder(customer.customerId)
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
        val so = SalesOrder.createNewSalesOrder(customer.customerId)
        so.addProduct(product1)
        so.addProduct(product2)
        so.addProduct(product1)
        so.deliverProduct(product1)

        so.invoiceDeliveredProductsAndServices(paymentServiceMock)
        verify(paymentServiceMock, times(1)).handleProductPayment(product1, customer.customerId, PaymentMethod.INVOICE)
    }

    @Test
    fun payDeliveredProduct() {
        val so = SalesOrder.createNewSalesOrder(customer.customerId)
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

        val so = SalesOrder.createNewSalesOrder(customer.customerId)
    //    so.addCustomerContactAsParticipant()
        so.addParticipant(person1)
        val participantsAndRoles = so.listParticipantsAndRoles()
        assertEquals(1, participantsAndRoles.size)
        assertNotNull(participantsAndRoles.find { it.equals(expectedParticipantAndRole) })
    }

    @Test
    fun removeParticipant() {
        val expectedParticipantAndRole = ParticipantAndRole(customer.contactPerson, ParticipantRole.ORGANIZER)

        val so = SalesOrder.createNewSalesOrder(customer.customerId)
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
        val so = SalesOrder.createNewSalesOrder(customer.customerId)
        assertEquals(SalesOrderState.QUOTE, so.state())

        so.addProduct(product1)
        so.addProduct(product2)
        so.confirmSalesOrder()
        assertEquals(SalesOrderState.CONFIRMED, so.state())

        so.deliverProduct(product1)
        assertEquals(SalesOrderState.PARTIALLY_DELIVERED, so.state())

        so.deliverProduct(product2)
        assertEquals(SalesOrderState.DELIVERED, so.state())
    }
}