package io.paju.templateservice.model.sales

import com.nhaarman.mockito_kotlin.check
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import io.paju.templateservice.model.customer.Customer
import io.paju.templateservice.model.customer.CustomerId
import io.paju.templateservice.model.customer.Person
import io.paju.templateservice.model.customer.PersonSex
import io.paju.templateservice.model.product.Price
import io.paju.templateservice.model.product.Product
import io.paju.templateservice.model.product.SellableProduct
import io.paju.templateservice.model.product.Vat
import io.paju.templateservice.services.PaymentService
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.util.*

internal class SalesOrderTest {

    val customer = Customer(CustomerId(UUID.randomUUID()),"Test customer", Person(Date(), "Test", "Person", PersonSex.MALE))
    val product1 = SellableProduct(Price(10.0f, Vat.vat24), "Test product1", "Test product description")
    val product2 = SellableProduct(Price(12.0f, Vat.vat24), "Test product2", "Test product description")
    val person1 = Person(Date(), "Pekka", "Person", PersonSex.MALE)

    val paymentServiceMock = mock<PaymentService>()

    @Test
    fun addProduct() {
        val so = SalesOrder(customer)
        so.addProduct(product1)
        val products = so.listProducts()
        assertEquals(1, products.size)
        val productAndPaymentStatus = products.get(0)

        assertTrue(product1.equals(productAndPaymentStatus.product), "added product does not match product in the list")
        assertEquals(productAndPaymentStatus.paymentStatus, PaymentStatus.OPEN)
    }

    @Test
    fun removeProduct() {
        val so = SalesOrder(customer)
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
        val so = SalesOrder(customer)
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
        val so = SalesOrder(customer)
        so.addProduct(product1)
        so.addProduct(product2)
        so.addProduct(product1)
        so.deliverProduct(product1)

        so.invoiceDeliveredProductsAndServices(paymentServiceMock)
        verify(paymentServiceMock, times(1)).handleProductPayment(product1, customer, PaymentMethod.INVOICE)
    }

    @Test
    fun payDeliveredProduct() {
        val so = SalesOrder(customer)
        so.addProduct(product1)
        so.addProduct(product2)
        so.addProduct(product1)

        so.deliverProduct(product1)

        so.payDeliveredProduct(paymentServiceMock, product1, PaymentMethod.CASH)
        verify(paymentServiceMock, times(1)).handleProductPayment(product1, customer, PaymentMethod.CASH)
    }

    @Test
    fun payDeliveredServices() {
    }

    @Test
    fun addParticipant() {
        val expectedParticipantAndRole = ParticipantAndRole(customer.contactPerson, ParticipantRole.ORGANIZER)

        val so = SalesOrder(customer)
        so.addCustomerContactAsParticipant()
        so.addParticipant(person1)
        val participantsAndRoles = so.listParticipantsAndRoles()
        assertEquals(2, participantsAndRoles.size)
        assertNotNull(participantsAndRoles.find { it.equals(expectedParticipantAndRole) })
    }

    @Test
    fun removeParticipant() {
        val expectedParticipantAndRole = ParticipantAndRole(customer.contactPerson, ParticipantRole.ORGANIZER)

        val so = SalesOrder(customer)
        so.addCustomerContactAsParticipant()
        so.addParticipant(person1)
        so.removeParticipant(person1)

        val participantsAndRoles = so.listParticipantsAndRoles()
        assertEquals(1, participantsAndRoles.size)
        assertNotNull(participantsAndRoles.find { it.equals(expectedParticipantAndRole) })
    }

    @Test
    fun cancelSalesOrder() {
    }

    @Test
    fun status() {
        val so = SalesOrder(customer)
        assertEquals(SalesOrderState.QUOTE, so.status())

        so.addProduct(product1)
        so.addProduct(product2)
        so.confirmSalesOrder()
        assertEquals(SalesOrderState.CONFIRMED, so.status())

        so.deliverProduct(product1)
        assertEquals(SalesOrderState.PARTIALLY_DELIVERED, so.status())

        so.deliverProduct(product2)
        assertEquals(SalesOrderState.DELIVERED, so.status())
    }

}