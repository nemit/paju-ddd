package io.paju.templateservice.domain.salesorder

import io.paju.templateservice.domain.product.Product
import io.paju.templateservice.domain.customer.CustomerId
import io.paju.templateservice.domain.customer.Person
import io.paju.templateservice.domain.product.ReservedService
import io.paju.templateservice.domain.product.SellableProduct
import io.paju.templateservice.domain.services.PaymentService
import io.paju.templateservice.shared.AbstractAggregate
import java.util.*

/**
 * SalesOrder is Aggregate responsible for Sales Order lifecycle starting from Quote to Confirmed and the to Delivered.
 * PaymentStatus is tracked per product or service
 * TODO: Is quote part of this? Quote expiration?
 */

class SalesOrder internal constructor(var customer: CustomerId,
                                      internal val services: MutableList<ServiceAndStatus> = mutableListOf<ServiceAndStatus>(),
                                      internal val products: MutableList<ProductAndStatus> = mutableListOf<ProductAndStatus>(),
                                      internal val participants: MutableList<ParticipantAndRole> = mutableListOf<ParticipantAndRole>(),
                                      internal var confirmed: Boolean = false,
                                      internal var deleted: Boolean = false,
                                      internal var id: SalesOrderId? = null
                                      ): AbstractAggregate() {
    init {
        // This is to avoid having dedicated add method in repository - if id omitted in constructor, new ID generated
        // and aggregate root marked as new object
        if (id == null) {
            id = SalesOrderId(UUID.randomUUID())
            repositoryMediator.registerNew(this)
        }
    }
    // PUBLIC BUSINESS FUNCTIONS

    fun id(): SalesOrderId {
        return id!!
    }

    fun listParticipantsAndRoles(): List<ParticipantAndRole> {
        return participants.toList()
    }

    fun listProducts(): List<ProductAndStatus> {
        return products
    }

    fun listReservedServices(): List<ServiceAndStatus> {
        return services
    }

    fun addProduct(product: SellableProduct) {
        val added = ProductAndStatus(PaymentStatus.OPEN, PaymentMethod.UNDEFINED, product)
        products.add(added)
        repositoryMediator.registerNew(added)
    }

    fun removeProduct(product: SellableProduct) {
        val removed = ProductAndStatus(PaymentStatus.OPEN, PaymentMethod.UNDEFINED, product)
        products.remove(removed)
        repositoryMediator.registerRemoved(removed)
    }

    fun addReservedService(service: ReservedService) {
        val added = ServiceAndStatus(PaymentStatus.OPEN, PaymentMethod.UNDEFINED, service)
        services.add(added)
        repositoryMediator.registerNew(added)
    }

    fun removeReservedService(service: ReservedService) {
        val removed = ServiceAndStatus(PaymentStatus.OPEN, PaymentMethod.UNDEFINED, service)
        services.remove(removed)
        repositoryMediator.registerRemoved(removed)
    }

    fun deliverService(service: ReservedService) {
        val s = services.find { it.service.equals(service) }
        if (s != null) {
            val newStatus = ServiceAndStatus(s.paymentStatus, s.paymentMethod, s.service, DeliveryStatus.DELIVERED)
            services.add(newStatus)
            services.remove(s)
            repositoryMediator.registerNew(newStatus)
            repositoryMediator.registerRemoved(s)
        } else {
            //TODO should throw error?
        }
    }

    fun deliverProduct(product: Product) {
        val s = products.find { it.product.equals(product) }
        if (s != null) {
            val newStatus = ProductAndStatus(s.paymentStatus, s.paymentMethod, s.product, DeliveryStatus.DELIVERED)
            products.add(newStatus)
            products.remove(s)
            repositoryMediator.registerRemoved(s)
            repositoryMediator.registerNew(newStatus)
        } else {

            //TODO should throw error?
        }
    }

    fun invoiceDeliveredProductsAndServices(paymentService: PaymentService) {
        val allDeliveredProducts = mutableListOf<Product>()

        allDeliveredProducts.addAll(services.filter { it.deliveryStatus == DeliveryStatus.DELIVERED}.map({it.service as Product}))
        allDeliveredProducts.addAll(products.filter { it.deliveryStatus == DeliveryStatus.DELIVERED}.map({it.product as Product}))

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
        repositoryMediator.registerNew(added)
    }

    fun removeParticipant(person: Person) {
        val participantAndStatus = participants.find { it.participant.equals(person) }
        if (participantAndStatus != null) {
            participants.remove(participantAndStatus)
            repositoryMediator.registerRemoved(participantAndStatus)
        } else {
            //TODO
        }

    }

    fun deleteSalesOrder() {
        deleted = true
        repositoryMediator.registerDirty(this)
    }

    fun confirmSalesOrder() {
        confirmed = true
        repositoryMediator.registerDirty(this)
    }

    fun state(): Status {
        val productsDelivered = products.filter { it.deliveryStatus == DeliveryStatus.DELIVERED }.size
        val servicesDelivered = services.filter { it.deliveryStatus == DeliveryStatus.DELIVERED }.size
        val delivered = productsDelivered + servicesDelivered
        val total = products.size + services.size

        return when {
            deleted -> Status.DELETED
            delivered > 0 && total == delivered -> Status.DELIVERED
            delivered > 0 -> Status.PARTIALLY_DELIVERED
            confirmed -> Status.CONFIRMED
            else -> Status.QUOTE
        }
    }
}
