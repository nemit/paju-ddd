package io.paju.salesorder.domain


enum class DeliveryStatus {
    NOT_DELIVERED, DELIVERED
}

enum class Status {
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