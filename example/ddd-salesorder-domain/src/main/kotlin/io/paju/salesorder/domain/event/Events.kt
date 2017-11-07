package io.paju.salesorder.domain.event

import io.paju.ddd.AggregateRootId
import io.paju.ddd.EntityId
import io.paju.ddd.Event
import io.paju.salesorder.domain.DeliveryStatus
import io.paju.salesorder.domain.PaymentMethod
import io.paju.salesorder.domain.PaymentStatus
import io.paju.salesorder.domain.Product
import io.paju.salesorder.domain.state.ProductState
import io.paju.salesorder.domain.state.SalesOrderState

sealed class SalesOrderEvent : Event() {
    abstract fun applyTo(currentState: SalesOrderState): SalesOrderState
}

// Sales order Events
data class SalesOrderCreated(
    override val id: AggregateRootId,
    val customer: EntityId
): Event()

data class SalesOrderDeleted(override val id: AggregateRootId) : SalesOrderEvent() {
    override fun applyTo(currentState: SalesOrderState): SalesOrderState {
        return currentState.copy(deleted = true)
    }
}
data class SalesOrderConfirmed(override val id: AggregateRootId) : SalesOrderEvent() {
    override fun applyTo(currentState: SalesOrderState): SalesOrderState {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

// Product Events
data class ProductAdded(
    override val id: AggregateRootId,
    val product: Product,
    val paymentStatus: PaymentStatus,
    val paymentMethod: PaymentMethod,
    val deliveryStatus: DeliveryStatus
) : SalesOrderEvent() {
    override fun applyTo(currentState: SalesOrderState): SalesOrderState {
        return currentState.copy(products = currentState.products.plus(ProductState(product, paymentStatus, paymentMethod, deliveryStatus)))
    }

}
data class ProductRemoved(override val id: AggregateRootId, val product: Product) : SalesOrderEvent() {
    override fun applyTo(currentState: SalesOrderState): SalesOrderState {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
data class ProductDelivered(override val id: AggregateRootId, val product: Product) : SalesOrderEvent() {
    override fun applyTo(currentState: SalesOrderState): SalesOrderState {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
data class ProductInvoiced(override val id: AggregateRootId, val product: Product) : SalesOrderEvent() {
    override fun applyTo(currentState: SalesOrderState): SalesOrderState {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
data class ProductPaid(override val id: AggregateRootId, val product: Product) : SalesOrderEvent() {
    override fun applyTo(currentState: SalesOrderState): SalesOrderState {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
