package io.paju.templateservice.model.salesorder

import io.paju.templateservice.model.customer.Person
import io.paju.templateservice.model.product.ReservedService
import io.paju.templateservice.model.product.SellableProduct
import java.util.*


data class SalesOrderId(val value: UUID)

data class ProductAndStatus(var paymentStatus: PaymentStatus,
                            var paymentMethod: PaymentMethod,
                            val product: SellableProduct,
                            val deliveryStatus: DeliveryStatus = DeliveryStatus.NOT_DELIVERED)

data class ServiceAndStatus(var paymentStatus: PaymentStatus,
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
    return when (status) {
        "INVOICED" -> PaymentStatus.INVOICED
        "PAID" -> PaymentStatus.PAID
        else -> PaymentStatus.OPEN
    }
}

fun paymentMethodFromString(method: String): PaymentMethod {
    return when (method) {
        "INVOICE" -> PaymentMethod.INVOICE
        "CASH" -> PaymentMethod.CASH
        else -> PaymentMethod.UNDEFINED
    }
}

fun deliveryStatusFromString(status: String): DeliveryStatus {
    return when (status) {
        "DELIVERED" -> DeliveryStatus.DELIVERED
        else -> DeliveryStatus.NOT_DELIVERED
    }
}

fun participantRoleFromString(role: String): ParticipantRole {
    return when (role) {
        "OGRANIZER" -> ParticipantRole.ORGANIZER
        else -> ParticipantRole.OTHER
    }
}