package io.paju.salesorder.domain.state

import io.paju.salesorder.domain.DeliveryStatus
import io.paju.salesorder.domain.PaymentStatus
import io.paju.salesorder.domain.SalesOrder
import io.paju.salesorder.domain.event.SalesOrderEvent

class SalesOrderStateManager(private val salesOrder: SalesOrder)
{
    private fun state(): SalesOrderState {
        return salesOrder.state
    }

    private fun setState(state: SalesOrderState) {
        salesOrder.state = state
    }

    fun apply(event: SalesOrderEvent.CustomerSet) {
        setState(state().copy(customerId = event.customerId))
    }

    @Suppress("unused_parameter")
    fun apply(event: SalesOrderEvent.Deleted) {
        setState(state().copy(deleted = true))
    }

    @Suppress("unused_parameter")
    fun apply(event: SalesOrderEvent.Confirmed) {
        setState(state().copy(confirmed = true))
    }

    fun apply(event: SalesOrderEvent.ProductAdded) {
        val products = state().products.toMutableList()
        products.add(
            ProductState(event.product, event.paymentStatus, event.paymentMethod, event.deliveryStatus)
        )
        setState(state().copy(products = products))
    }

    fun apply(event: SalesOrderEvent.ProductRemoved) {
        val products = state().products.filterNot { it.product.id == event.product.id }
        setState(state().copy(products = products))
    }

    fun apply(event: SalesOrderEvent.ProductDelivered) {
        val products = state().products.map {
            if (it.product.id == event.product.id) {
                it.copy(deliveryStatus = DeliveryStatus.DELIVERED)
            } else {
                it
            }
        }
        setState(state().copy(products = products))
    }

    fun apply(event: SalesOrderEvent.ProductInvoiced) {
        val products = state().products.map {
            if (it.product.id == event.product.id) {
                it.copy(paymentStatus = PaymentStatus.INVOICED)
            } else {
                it
            }
        }
        setState(state().copy(products = products))
    }

    fun apply(event: SalesOrderEvent.ProductPaid) {
        val products = state().products.map {
            if (it.product.id == event.product.id) {
                it.copy(paymentStatus = PaymentStatus.PAID)
            } else {
                it
            }
        }
        setState(state().copy(products = products))
    }
}