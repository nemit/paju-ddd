package io.paju.templateservice.model.sales

import io.paju.templateservice.model.product.Product
import io.paju.templateservice.model.customer.Customer
import io.paju.templateservice.model.customer.Person
import io.paju.templateservice.model.product.ReservedService
import io.paju.templateservice.services.PaymentService

/**
 * SalesOrder is Aggregate responsible for Sales Order lifecycle starting from Quote to Confirmed and the to Delivered.
 * PaymentStatus is tracked per product or service
 * TODO: Is quote part of this? Quote expiration?
 */
class SalesOrder(val customer: Customer) {

    private val startedServices: MutableList<ServiceAndPaymentStatus>
    private val deliveredServices: MutableList<ServiceAndPaymentStatus>
    private val orderedServices: MutableList<ServiceAndPaymentStatus>
    private val orderedProducts: MutableList<ProductAndPaymentStatus>
    private val deliveredProducts: MutableList<ProductAndPaymentStatus>
    private val currentState: SalesOrderState
    private val additionalParticipants: MutableList<Person>
    private val orderConfirmed: Boolean = false

    init {
        startedServices = mutableListOf<ServiceAndPaymentStatus>()
        deliveredServices = mutableListOf<ServiceAndPaymentStatus>()
        orderedProducts = mutableListOf<ProductAndPaymentStatus>()
        deliveredProducts = mutableListOf<ProductAndPaymentStatus>()
        orderedServices = mutableListOf<ServiceAndPaymentStatus>()
        additionalParticipants = mutableListOf<Person>()
        currentState = SalesOrderState.QUOTE
    }

    // PUBLIC BUSINESS FUNCTIONS
    fun addProduct(product: Product) {
        orderedProducts.add(ProductAndPaymentStatus(PaymentStatus.OPEN, PaymentMethod.UNDEFINED, product))
    }

    fun removeProduct(product: Product) {
        orderedProducts.remove(ProductAndPaymentStatus(PaymentStatus.OPEN, PaymentMethod.UNDEFINED, product))
    }

    fun addReservedService(service: ReservedService) {
        orderedServices.add(ServiceAndPaymentStatus(PaymentStatus.OPEN, PaymentMethod.UNDEFINED, service))
    }

    fun removeReservedService(service: ReservedService) {
        orderedServices.remove(ServiceAndPaymentStatus(PaymentStatus.OPEN, PaymentMethod.UNDEFINED, service))
    }

    fun deliverService(service: ReservedService) {
        val serviceAndPaymentStatus = orderedServices.find { serviceAndPaymentStatus -> serviceAndPaymentStatus.service.equals(service) }
        if (serviceAndPaymentStatus != null) {
            deliveredServices.add(serviceAndPaymentStatus)
            orderedServices.remove(serviceAndPaymentStatus)
        } else {
            //TODO should throw error?
        }
    }

    fun deliverProduct(product: Product) {
        val productAndPaymentStatus = orderedProducts.find { productAndPaymentStatus -> productAndPaymentStatus.product.equals(product) }
        if (productAndPaymentStatus != null) {
            deliveredProducts.add(productAndPaymentStatus)
            orderedProducts.remove(productAndPaymentStatus)
        } else {
            //TODO should throw error?
        }
    }

    fun invoiceDeliveredProductsAndServices() {
        val allDeliveredProducts = mutableListOf<Product>()
        allDeliveredProducts.addAll(deliveredServices.map { serviceAndPaymentStatus ->
            serviceAndPaymentStatus.service as Product
        })

        allDeliveredProducts.addAll(deliveredProducts.map { productAndPaymentStatus ->
            productAndPaymentStatus.product as Product
        })

        for (product in allDeliveredProducts) {
            // TODO Should PaymentService be given as parameter? Now using as Singleton
            PaymentService.handleProductPayment(product, customer, PaymentMethod.INVOICE)
        }
    }

    fun payDeliveredProduct(product: Product, method: PaymentMethod) {
        PaymentService.handleProductPayment(product, customer, method)
    }

    fun payDeliveredServices(service: ReservedService, method: PaymentMethod) {
        PaymentService.handleProductPayment(service, customer, method)
    }

    fun addParticipant(person: Person) {
        // TODO: Here we could have business logic to verify sufficient amount of products for every participant
        additionalParticipants.add(person)
    }

    fun removeParticipant(person: Person) {
        additionalParticipants.remove(person)
    }

    fun cancelSalesOrder() {
        // TODO
    }

    fun status() {
        when {
            orderedProducts.isEmpty() && orderedServices.isEmpty() && (deliveredProducts.isNotEmpty() || deliveredServices.isNotEmpty()) -> SalesOrderState.DELIVERED
            (orderedProducts.isNotEmpty() || orderedServices.isNotEmpty()) && (deliveredServices.isNotEmpty() ||deliveredProducts.isNotEmpty()) -> SalesOrderState.PARTIALLY_DELIVERED
            orderConfirmed -> SalesOrderState.CONFIRMED
            else -> SalesOrderState.QUOTE
        }
    }
}


data class ProductAndPaymentStatus(var status: PaymentStatus, var method: PaymentMethod, val product: Product)
data class ServiceAndPaymentStatus(var status: PaymentStatus, var method: PaymentMethod, val service: ReservedService)

enum class SalesOrderState {
    QUOTE, CONFIRMED, PARTIALLY_DELIVERED, DELIVERED
}

enum class PaymentStatus {
    OPEN, INVOICED, PAID
}

enum class PaymentMethod {
    INVOICE, CASH, UNDEFINED
}