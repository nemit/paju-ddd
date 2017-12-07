package io.paju.salesorder.domain.state

import io.paju.salesorder.domain.DeliveryStatus
import io.paju.salesorder.domain.PaymentStatus
import io.paju.salesorder.domain.event.SalesOrderEvent

class SalesOrderStateManager(private val currentState: () -> SalesOrderState)
{
    fun apply(event: SalesOrderEvent.CustomerSet) =
        currentState().copy(customerId = event.customerId)

    @Suppress("unused_parameter")
    fun apply(event: SalesOrderEvent.Deleted) =
        currentState().copy(deleted = true)

    @Suppress("unused_parameter")
    fun apply(event: SalesOrderEvent.Confirmed) =
        currentState().copy(confirmed = true)

    fun apply(event: SalesOrderEvent.ProductAdded): SalesOrderState {
        val products = currentState().products.toMutableList()
        products.add(
            ProductState(event.product, event.paymentStatus, event.paymentMethod, event.deliveryStatus)
        )
        return currentState().copy(products = products)
    }

    fun apply(event: SalesOrderEvent.ProductRemoved): SalesOrderState {
        val products = currentState().products.filterNot { it.product.id == event.product.id }
        return currentState().copy(products = products)
    }

    fun apply(event: SalesOrderEvent.ProductDelivered): SalesOrderState {
        val products = currentState().products.map {
            if (it.product.id == event.product.id) {
                it.copy(deliveryStatus = DeliveryStatus.DELIVERED)
            } else {
                it
            }
        }
        return currentState().copy(products = products)
    }

    fun apply(event: SalesOrderEvent.ProductInvoiced): SalesOrderState {
        val products = currentState().products.map {
            if (it.product.id == event.product.id) {
                it.copy(paymentStatus = PaymentStatus.INVOICED)
            } else {
                it
            }
        }
        return currentState().copy(products = products)
    }

    fun apply(event: SalesOrderEvent.ProductPaid): SalesOrderState {
        val products = currentState().products.map {
            if (it.product.id == event.product.id) {
                it.copy(paymentStatus = PaymentStatus.PAID)
            } else {
                it
            }
        }
        return currentState().copy(products = products)
    }
}