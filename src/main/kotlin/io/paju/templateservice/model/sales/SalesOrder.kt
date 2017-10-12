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
    private val orderedProducts: MutableList<ProductAndStatus>
    private val deliveredProducts: MutableList<ProductAndStatus>
    private val currentState: SalesOrderState
    private val participants: MutableList<ParticipantAndRole>
    private var orderConfirmed: Boolean = false

    init {
        startedServices = mutableListOf<ServiceAndPaymentStatus>()
        deliveredServices = mutableListOf<ServiceAndPaymentStatus>()
        orderedProducts = mutableListOf<ProductAndStatus>()
        deliveredProducts = mutableListOf<ProductAndStatus>()
        orderedServices = mutableListOf<ServiceAndPaymentStatus>()
        participants = mutableListOf<ParticipantAndRole>()
        currentState = SalesOrderState.QUOTE
    }


    // PUBLIC BUSINESS FUNCTIONS

    fun listParticipantsAndRoles(): List<ParticipantAndRole> {
        return participants.toList()
    }

    fun listProducts(): List<ProductAndStatus> {
        return listOf<ProductAndStatus>(*orderedProducts.toTypedArray(), *deliveredProducts.toTypedArray())
    }

    fun listReservedServices(): List<ServiceAndPaymentStatus> {
        return listOf<ServiceAndPaymentStatus>(*orderedServices.toTypedArray(), *deliveredServices.toTypedArray())
    }

    fun addProduct(product: Product) {
        orderedProducts.add(ProductAndStatus(PaymentStatus.OPEN, PaymentMethod.UNDEFINED, product))
    }

    fun removeProduct(product: Product) {
        orderedProducts.remove(ProductAndStatus(PaymentStatus.OPEN, PaymentMethod.UNDEFINED, product))
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
        val s = orderedProducts.find { productAndPaymentStatus -> productAndPaymentStatus.product.equals(product) }
        if (s != null) {
            val newStatus = ProductAndStatus(s.paymentStatus, s.paymentMethod, s.product, DeliveryStatus.DELIVERED)
            deliveredProducts.add(newStatus)
            orderedProducts.remove(s)
        } else {
            //TODO should throw error?
        }
    }

    fun invoiceDeliveredProductsAndServices(paymentService: PaymentService) {
        val allDeliveredProducts = mutableListOf<Product>()
        allDeliveredProducts.addAll(deliveredServices.map { serviceAndPaymentStatus ->
            serviceAndPaymentStatus.service as Product
        })

        allDeliveredProducts.addAll(deliveredProducts.map { productAndPaymentStatus ->
            productAndPaymentStatus.product as Product
        })

        for (product in allDeliveredProducts) {
            // TODO Should PaymentService be given as parameter? Now using as Singleton
            paymentService.handleProductPayment(product, customer, PaymentMethod.INVOICE)
        }
    }

    fun payDeliveredProduct(paymentService: PaymentService, product: Product, method: PaymentMethod) {
        paymentService.handleProductPayment(product, customer, method)
    }

    fun payDeliveredServices(paymentService: PaymentService, service: ReservedService, method: PaymentMethod) {
        paymentService.handleProductPayment(service, customer, method)
    }

    fun addParticipant(person: Person) {
        // TODO: Here we could have business logic to verify sufficient amount of products for every participant
        participants.add(ParticipantAndRole(person, ParticipantRole.OTHER))
    }

    fun addCustomerContactAsParticipant() {
        participants.add(ParticipantAndRole(this.customer.contactPerson, ParticipantRole.ORGANIZER))
    }
    
    fun removeParticipant(person: Person) {
        val participantAndStatus = participants.find { it.participant.equals(person) }
        participants.remove(participantAndStatus)
    }

    fun cancelSalesOrder() {
        // TODO
    }

    fun confirmSalesOrder() {
        orderConfirmed = true
    }

    fun status(): SalesOrderState {
        return when {
            orderedProducts.isEmpty() && orderedServices.isEmpty() && (deliveredProducts.isNotEmpty() || deliveredServices.isNotEmpty()) -> SalesOrderState.DELIVERED
            (orderedProducts.isNotEmpty() || orderedServices.isNotEmpty()) && (deliveredServices.isNotEmpty() ||deliveredProducts.isNotEmpty()) -> SalesOrderState.PARTIALLY_DELIVERED
            orderConfirmed -> SalesOrderState.CONFIRMED
            else -> SalesOrderState.QUOTE
        }
    }
}


data class ProductAndStatus(var paymentStatus: PaymentStatus, var paymentMethod: PaymentMethod, val product: Product, val deliveryStatus: DeliveryStatus = DeliveryStatus.NOT_DELIVERED)
data class ServiceAndPaymentStatus(var status: PaymentStatus, var method: PaymentMethod, val service: ReservedService)
data class ParticipantAndRole(val participant: Person, val role: ParticipantRole)

enum class ParticipantRole {
    ORGANIZER, OTHER
}

enum class DeliveryStatus {
    NOT_DELIVERED, DELIVERED
}

enum class SalesOrderState {
    QUOTE, CONFIRMED, PARTIALLY_DELIVERED, DELIVERED
}

enum class PaymentStatus {
    OPEN, INVOICED, PAID
}

enum class PaymentMethod {
    INVOICE, CASH, UNDEFINED
}