package io.paju.templateservice.model.salesoder

import io.paju.templateservice.model.product.Product
import io.paju.templateservice.model.customer.Customer
import io.paju.templateservice.model.customer.CustomerId
import io.paju.templateservice.model.customer.Person
import io.paju.templateservice.model.product.ReservedService
import io.paju.templateservice.model.product.SellableProduct
import io.paju.templateservice.services.PaymentService
import java.util.*

/**
 * SalesOrder is Aggregate responsible for Sales Order lifecycle starting from Quote to Confirmed and the to Delivered.
 * PaymentStatus is tracked per product or service
 * TODO: Is quote part of this? Quote expiration?
 */

class SalesOrder internal constructor (val customer: CustomerId,
                                       private val startedServices: MutableList<ServiceAndPaymentStatus> = mutableListOf<ServiceAndPaymentStatus>(),
                                       private val deliveredServices: MutableList<ServiceAndPaymentStatus> = mutableListOf<ServiceAndPaymentStatus>(),
                                       private val orderedServices: MutableList<ServiceAndPaymentStatus> = mutableListOf<ServiceAndPaymentStatus>(),
                                       private val orderedProducts: MutableList<ProductAndStatus> = mutableListOf<ProductAndStatus>(),
                                       private val deliveredProducts: MutableList<ProductAndStatus> = mutableListOf<ProductAndStatus>(),
                                       private val participants: MutableList<ParticipantAndRole> = mutableListOf<ParticipantAndRole>(),
                                       private var confirmed: Boolean = false,
                                       private var deleted: Boolean = false,
                                       private val id: SalesOrderId = SalesOrderId(UUID.randomUUID())) {

    // FACTORY METHOD
    companion object {
        fun createNewSalesOrder(customer: CustomerId): SalesOrder {
            return SalesOrder(customer)
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

    fun listReservedServices(): List<ServiceAndPaymentStatus> {
        return listOf<ServiceAndPaymentStatus>(*orderedServices.toTypedArray(), *deliveredServices.toTypedArray())
    }

    fun addProduct(product: SellableProduct) {
        orderedProducts.add(ProductAndStatus(PaymentStatus.OPEN, PaymentMethod.UNDEFINED, product))
    }

    fun removeProduct(product: SellableProduct) {
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

    fun removeParticipant(person: Person) {
        val participantAndStatus = participants.find { it.participant.equals(person) }
        participants.remove(participantAndStatus)
    }

    fun deleteSalesOrder() {
        deleted = true
    }

    fun confirmSalesOrder() {
        confirmed = true
    }

    fun state(): SalesOrderState {
        return when {
            deleted -> SalesOrderState.DELETED
            orderedProducts.isEmpty() && orderedServices.isEmpty() && (deliveredProducts.isNotEmpty() || deliveredServices.isNotEmpty()) -> SalesOrderState.DELIVERED
            (orderedProducts.isNotEmpty() || orderedServices.isNotEmpty()) && (deliveredServices.isNotEmpty() ||deliveredProducts.isNotEmpty()) -> SalesOrderState.PARTIALLY_DELIVERED
            confirmed -> SalesOrderState.CONFIRMED
            else -> SalesOrderState.QUOTE
        }
    }

    // CONVERSION TO DB DTOs
    fun toDb(): SalesOrderDb {
        return SalesOrderDb(id.value, this.customer.value, confirmed, deleted)
    }

    fun participantsToDb(): List<PersonRoleInSalesOrderDb> {
        val list = mutableListOf<PersonRoleInSalesOrderDb>()
        for (personAndRole in this.listParticipantsAndRoles()) {
            list.add(PersonRoleInSalesOrderDb(this.id.value, personAndRole.participant.valueObjectLocalId().id, personAndRole.role.toString()))
        }
        return list
    }

    fun servicesToDb(): List<ReservedServicesInSalesOrder> {
        val list = mutableListOf<ReservedServicesInSalesOrder>()

        // TODO conversion to extension functions..
        for (service in this.orderedServices) {
            list.add(ReservedServicesInSalesOrder(service.service.reservedServiceId.value,
                    this.id.value,
                    service.paymentStatus.toString(),
                    service.paymentMethod.toString(),
                    service.deliveryStatus.toString())
            )
        }

        for (service in this.startedServices) {
            list.add(ReservedServicesInSalesOrder(service.service.reservedServiceId.value,
                    this.id.value,
                    service.paymentStatus.toString(),
                    service.paymentMethod.toString(),
                    service.deliveryStatus.toString())
            )
        }

        for (service in this.deliveredServices) {
            list.add(ReservedServicesInSalesOrder(service.service.reservedServiceId.value,
                    this.id.value,
                    service.paymentStatus.toString(),
                    service.paymentMethod.toString(),
                    service.deliveryStatus.toString())
            )
        }

        return list
    }

    fun productsToDb(): List<ProductsInSalesOrderDb> {
        val list = mutableListOf<ProductsInSalesOrderDb>()
        for (product in orderedProducts) {
            list.add(ProductsInSalesOrderDb(this.id.value, product.product.valueObjectLocalId().id,
                    product.paymentStatus.toString(),
                    product.paymentMethod.toString(),
                    product.deliveryStatus.toString()))
        }

        for (product in deliveredProducts) {
            list.add(ProductsInSalesOrderDb(this.id.value, product.product.valueObjectLocalId().id,
                    product.paymentStatus.toString(),
                    product.paymentMethod.toString(),
                    product.deliveryStatus.toString()))
        }

        return list
    }
}

data class SalesOrderId(val value: UUID)

data class ProductAndStatus(var paymentStatus: PaymentStatus,
                            var paymentMethod: PaymentMethod,
                            val product: SellableProduct,
                            val deliveryStatus: DeliveryStatus = DeliveryStatus.NOT_DELIVERED)
data class ServiceAndPaymentStatus(var paymentStatus: PaymentStatus,
                                   var paymentMethod: PaymentMethod,
                                   val service: ReservedService,
                                   val deliveryStatus: DeliveryStatus = DeliveryStatus.NOT_DELIVERED)
data class ParticipantAndRole(val participant: Person, val role: ParticipantRole)

enum class ParticipantRole {
    ORGANIZER, OTHER
}

enum class DeliveryStatus {
    NOT_DELIVERED, DELIVERED
}

enum class SalesOrderState {
    QUOTE, CONFIRMED, PARTIALLY_DELIVERED, DELIVERED, DELETED
}

enum class PaymentStatus {
    OPEN, INVOICED, PAID
}

enum class PaymentMethod {
    INVOICE, CASH, UNDEFINED
}

fun paymentStatusFromString(status: String): PaymentStatus {
    return when(status) {
        "INVOICED" -> PaymentStatus.INVOICED
        "PAID" -> PaymentStatus.PAID
        else -> PaymentStatus.OPEN
    }
}

fun paymentMethodFromString(method: String): PaymentMethod {
    return when(method) {
        "INVOICE" -> PaymentMethod.INVOICE
        "CASH" -> PaymentMethod.CASH
        else -> PaymentMethod.UNDEFINED
    }
}

fun deliveryStatusFromString(status: String): DeliveryStatus {
    return when(status) {
        "DELIVERED" -> DeliveryStatus.DELIVERED
        else -> DeliveryStatus.NOT_DELIVERED
    }
}

fun participantRoleFromString(role: String): ParticipantRole {
    return when(role) {
        "OGRANIZER" -> ParticipantRole.ORGANIZER
        else -> ParticipantRole.OTHER
    }
}