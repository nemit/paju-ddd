package io.paju.salesorder.domain

import io.paju.ddd.AggregateRoot
import io.paju.ddd.AggregateRootId
import io.paju.ddd.StateReconstructable
import io.paju.ddd.NotInitializedEntityId
import io.paju.ddd.exception.InvalidStateException
import io.paju.salesorder.domain.event.ProductAdded
import io.paju.salesorder.domain.event.ProductDelivered
import io.paju.salesorder.domain.event.ProductInvoiced
import io.paju.salesorder.domain.event.ProductPaid
import io.paju.salesorder.domain.event.ProductRemoved
import io.paju.salesorder.domain.event.SalesOrderEvent
import io.paju.salesorder.domain.state.ProductState
import io.paju.salesorder.domain.state.SalesOrderState
import io.paju.salesorder.service.DummyPaymentService

/**
 * SalesOrder is Aggregate responsible for Sales Order lifecycle starting from Quote to Confirmed and the to Delivered.
 * PaymentStatus is tracked per product or service
 */
class SalesOrder internal constructor(
    id: AggregateRootId
) : AggregateRoot<SalesOrderState, SalesOrderEvent>(id) {

    override var state: SalesOrderState = SalesOrderState(
        NotInitializedEntityId, false, false, mutableListOf<ProductState>()
    )

    // PUBLIC BUSINESS FUNCTIONS

    fun id(): AggregateRootId {
        return id
    }

    fun addProduct(product: Product) {
        val event = ProductAdded(
            id, product, PaymentStatus.OPEN, PaymentMethod.UNDEFINED, DeliveryStatus.NOT_DELIVERED
        )

        // update state
        applyChange(event)
    }


    fun removeProduct(product: Product) {
        val event = ProductRemoved(id, product)

        // update state
        applyChange(event)
    }

    fun deliverProduct(product: Product) {
        val event = ProductDelivered(id, product)

        // update state
        apply(event)
    }

    fun invoiceDeliveredProductsAndServices(paymentService: DummyPaymentService) {
        val delivered = products
            .filter { it.deliveryStatus == DeliveryStatus.DELIVERED }
            .map { it.product }

        for (product in delivered) {
            // call external service
            paymentService.handleProductPayment(product, customerId, PaymentMethod.INVOICE)

            // update state
            applyChange(ProductInvoiced(id, product))
        }
    }

    fun payDeliveredProduct(paymentService: DummyPaymentService, product: Product, method: PaymentMethod) {
        val p = products.find { it.product == product }
        p ?: throw InvalidStateException(id, version, "Failed to pay product, product not found")

        paymentService.handleProductPayment(product, customerId, method)
        apply(ProductPaid(id, product))
    }

    fun products(): List<Product> =
        products
            .map { it.product }

    fun products(deliveryStatus: DeliveryStatus): List<Product> =
        products
            .filter { it.deliveryStatus == deliveryStatus }
            .map { it.product }

    fun products(paymentStatus: PaymentStatus): List<Product> =
        products
            .filter { it.paymentStatus == paymentStatus }
            .map { it.product }


    private fun apply(event: ProductAdded) {
        products.add(
            ProductState(event.product, event.paymentStatus, event.paymentMethod, event.deliveryStatus)
        )
    }

    private fun apply(event: ProductRemoved) {
        val p = products.find { it.product == event.product }

        if (p != null) {
            products.remove(p)
        } else {
            throw InvalidStateException(id, version, "Failed to remove product, product not found")
        }
    }


    private fun apply(event: ProductDelivered) {
        val p = products.find { it.product == event.product }
        if (p != null) {
            products.remove(p)
            products.add(p.copy(deliveryStatus = DeliveryStatus.DELIVERED))
        } else {
            throw InvalidStateException(id, version, "Failed to deliver product, product not found")
        }
    }

    private fun apply(event: ProductInvoiced) {
        val p = products.find { it.product == event.product }

        if (p != null) {
            products.remove(p)
            products.add(p.copy(paymentStatus = PaymentStatus.INVOICED))
        } else {
            throw InvalidStateException(id, version, "Failed to deliver product, product not found")
        }
    }


    private fun apply(event: ProductPaid) {
        val p = products.find { it.product == event.product }
        if (p != null) {
            products.remove(p)
            products.add(p.copy(paymentStatus = PaymentStatus.PAID))
        } else {
            throw InvalidStateException(id, version, "Failed to pay product, product not found")
        }
    }

    fun deleteSalesOrder() {
        // TODO
    }

    fun confirmSalesOrder() {
        // TODO
    }

    fun state(): Status {
        val delivered = products.filter { it.deliveryStatus == DeliveryStatus.DELIVERED }.size
        val total = products.size

        return when {
            deleted -> Status.DELETED
            delivered > 0 && total == delivered -> Status.DELIVERED
            delivered > 0 -> Status.PARTIALLY_DELIVERED
            confirmed -> Status.CONFIRMED
            else -> Status.QUOTE
        }
    }

    override fun apply(event: SalesOrderEvent) {

        var products = state.products.toMutableList()

        fun addToProds(event: SalesOrderEvent.ProductAdded) =
            products.add(ProductState(event.product, event.paymentStatus, event.paymentMethod, event.deliveryStatus))

        fun removeFromProds(event: SalesOrderEvent.ProductRemoved) =
            products.removeIf { it.product != event.product }

        fun setProdDelivered(event: SalesOrderEvent.ProductRemoved) {
            products = products.map {
                if (it.product != event.product) it.copy(deliveryStatus = DeliveryStatus.DELIVERED)
                else it
            }.toMutableList()
        }

        when(event) {
            is SalesOrderEvent.CustomerSet -> state = state.copy(customerId = event.customer)
            is SalesOrderEvent.Deleted -> state = state.copy(deleted = true)
            is SalesOrderEvent.Confirmed -> state = state.copy(confirmed = true)
            is SalesOrderEvent.ProductAdded -> state = state.copy(products = addToProds(event) )
            is SalesOrderEvent.ProductRemoved -> state = state.copy(products = removeFromProds(event) )
            is SalesOrderEvent.ProductDelivered -> state = state.copy(products = setProdDelivered(event) )
            is SalesOrderEvent.ProductInvoiced ->
            is SalesOrderEvent.ProductPaid ->
        } let {}
    }



    companion object :
        StateReconstructable<SalesOrder, SalesOrderState>,
        StateExtractor<SalesOrder, SalesOrderState>
    {
        override fun constructAggregate(state: SalesOrderState): SalesOrder {
            val salesOrder = SalesOrder(state.id, state.customerId)
            salesOrder.confirmed = state.confirmed
            salesOrder.deleted = state.deleted
            salesOrder.products.addAll(state.products)
            return salesOrder
        }

        override fun extractAggregateState(aggregate: SalesOrder): SalesOrderState {
            return SalesOrderState(
                aggregate.id,
                aggregate.customerId,
                aggregate.confirmed,
                aggregate.deleted,
                aggregate.products
            )
        }
    }

}
