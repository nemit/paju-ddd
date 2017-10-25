package io.paju.templateservice.domain.salesorder

import io.paju.templateservice.domain.product.Product
import io.paju.templateservice.domain.customer.CustomerId
import io.paju.templateservice.domain.customer.Person
import io.paju.templateservice.domain.product.ReservedService
import io.paju.templateservice.domain.product.SellableProduct
import io.paju.templateservice.domain.services.PaymentService
import io.paju.templateservice.shared.AbstractAggregate
import io.paju.templateservice.shared.UnitOfWork
import java.util.*

/**
 * SalesOrder is Aggregate responsible for Sales Order lifecycle starting from Quote to Confirmed and the to Delivered.
 * PaymentStatus is tracked per product or service
 * TODO: Is quote part of this? Quote expiration?
 */

class SalesOrder constructor(val customer: CustomerId,
                             internal val startedServices: MutableList<ServiceAndStatus> = mutableListOf<ServiceAndStatus>(),
                             internal val deliveredServices: MutableList<ServiceAndStatus> = mutableListOf<ServiceAndStatus>(),
                             internal val orderedServices: MutableList<ServiceAndStatus> = mutableListOf<ServiceAndStatus>(),
                             internal val orderedProducts: MutableList<ProductAndStatus> = mutableListOf<ProductAndStatus>(),
                             internal val deliveredProducts: MutableList<ProductAndStatus> = mutableListOf<ProductAndStatus>(),
                             internal val participants: MutableList<ParticipantAndRole> = mutableListOf<ParticipantAndRole>(),
                             var confirmed: Boolean = false,
                             var deleted: Boolean = false,
                             val id: SalesOrderId = SalesOrderId(UUID.randomUUID()),
                             val unitOfWork: UnitOfWork = UnitOfWork()) {

    // FACTORY METHOD
    companion object {
        fun createNewSalesOrder(customer: CustomerId): SalesOrder {
            val salesOrder = SalesOrder(customer)
            return salesOrder
        }
    }

    // PUBLIC BUSINESS FUNCTIONS

    fun id(): SalesOrderId {
        return id
    }

    fun listParticipantsAndRoles(): List<ParticipantAndRole> {
        return participants.toList()
    }

    fun listProducts(): List<ProductAndStatus> {
        return listOf<ProductAndStatus>(*orderedProducts.toTypedArray(), *deliveredProducts.toTypedArray())
    }

    fun listReservedServices(): List<ServiceAndStatus> {
        return listOf<ServiceAndStatus>(*orderedServices.toTypedArray(), *deliveredServices.toTypedArray())
    }

    fun addProduct(product: SellableProduct) {
        val added = ProductAndStatus(PaymentStatus.OPEN, PaymentMethod.UNDEFINED, product)
        orderedProducts.add(added)
        unitOfWork.registerNew(added)
    }

    fun removeProduct(product: SellableProduct) {
        val removed = ProductAndStatus(PaymentStatus.OPEN, PaymentMethod.UNDEFINED, product)
        orderedProducts.remove(removed)
        unitOfWork.registerRemoved(removed)
    }

    fun addReservedService(service: ReservedService) {
        val added = ServiceAndStatus(PaymentStatus.OPEN, PaymentMethod.UNDEFINED, service)
        orderedServices.add(added)
        unitOfWork.registerNew(added)
    }

    fun removeReservedService(service: ReservedService) {
        val removed = ServiceAndStatus(PaymentStatus.OPEN, PaymentMethod.UNDEFINED, service)
        orderedServices.remove(removed)
        unitOfWork.registerRemoved(removed)
    }

    fun deliverService(service: ReservedService) {
        val s = orderedServices.find { it.service.equals(service) }
        if (s != null) {
            val newStatus = ServiceAndStatus(s.paymentStatus, s.paymentMethod, s.service, DeliveryStatus.DELIVERED)
            deliveredServices.add(newStatus)
            orderedServices.remove(s)
            unitOfWork.registerNew(newStatus)
            unitOfWork.registerRemoved(s)
        } else {
            //TODO should throw error?
        }
    }

    fun deliverProduct(product: Product) {
        val s = orderedProducts.find { it.product.equals(product) }
        if (s != null) {
            val newStatus = ProductAndStatus(s.paymentStatus, s.paymentMethod, s.product, DeliveryStatus.DELIVERED)
            deliveredProducts.add(newStatus)
            orderedProducts.remove(s)
            unitOfWork.registerRemoved(s)
            unitOfWork.registerNew(newStatus)
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
            paymentService.handleProductPayment(product, customer, PaymentMethod.INVOICE)
        }
    }

    fun payDeliveredProduct(paymentService: PaymentService, product: Product, method: PaymentMethod) {
        // TODO update payment status
        paymentService.handleProductPayment(product, customer, method)
    }

    fun payDeliveredServices(paymentService: PaymentService, service: ReservedService, method: PaymentMethod) {
        // TODO update payment status
        paymentService.handleProductPayment(service, customer, method)
    }

    fun addParticipant(person: Person) {
        // TODO: Here we could have business logic to verify sufficient amount of products for every participant
        val added = ParticipantAndRole(person, ParticipantRole.OTHER)
        participants.add(added)
        unitOfWork.registerNew(added)
    }

    fun removeParticipant(person: Person) {
        val participantAndStatus = participants.find { it.participant.equals(person) }
        if (participantAndStatus != null) {
            participants.remove(participantAndStatus)
            unitOfWork.registerRemoved(participantAndStatus)
        } else {
            //TODO
        }

    }

    fun deleteSalesOrder() {
        deleted = true
        unitOfWork.registerDirty(this)
    }

    fun confirmSalesOrder() {
        confirmed = true
        unitOfWork.registerDirty(this)
    }

    fun state(): SalesOrderState {
        return when {
            deleted -> SalesOrderState.DELETED
            orderedProducts.isEmpty() && orderedServices.isEmpty() && (deliveredProducts.isNotEmpty() || deliveredServices.isNotEmpty()) -> SalesOrderState.DELIVERED
            (orderedProducts.isNotEmpty() || orderedServices.isNotEmpty()) && (deliveredServices.isNotEmpty() || deliveredProducts.isNotEmpty()) -> SalesOrderState.PARTIALLY_DELIVERED
            confirmed -> SalesOrderState.CONFIRMED
            else -> SalesOrderState.QUOTE
        }
    }
}
