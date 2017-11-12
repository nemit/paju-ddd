package io.paju.salesorder.domain

import io.paju.ddd.AggregateRoot
import io.paju.ddd.AggregateRootId
import io.paju.ddd.EntityId
import io.paju.ddd.NotInitializedEntityId
import io.paju.ddd.StateExposed
import io.paju.ddd.StateReconstructable
import io.paju.ddd.exception.InvalidStateException
import io.paju.salesorder.domain.event.SalesOrderEvent
import io.paju.salesorder.domain.state.ProductState
import io.paju.salesorder.domain.state.SalesOrderState
import io.paju.salesorder.domain.state.SalesOrderStateManager
import io.paju.salesorder.service.DummyPaymentService

/**
 * SalesOrder is Aggregate responsible for Sales Order lifecycle starting from Quote to Confirmed and the to Delivered.
 * PaymentStatus is tracked per product
 */
class SalesOrder internal constructor(id: AggregateRootId) :
    AggregateRoot<SalesOrderEvent>(id),
    StateReconstructable<SalesOrderState>,
    StateExposed<SalesOrderState>
{

    var state: SalesOrderState = SalesOrderState(1,
        NotInitializedEntityId, false, false, mutableListOf()
    )
    private val stateManager = SalesOrderStateManager(this)

    override fun state(): SalesOrderState {
        return state
    }

    override fun reconstruct(state: SalesOrderState) {
        this.state = state
    }

    // event handling
    override fun apply(event: SalesOrderEvent) {
        when(event) {
            is SalesOrderEvent.CustomerSet -> stateManager.apply(event)
            is SalesOrderEvent.Deleted -> stateManager.apply(event)
            is SalesOrderEvent.Confirmed -> stateManager.apply(event)
            is SalesOrderEvent.ProductAdded -> stateManager.apply(event)
            is SalesOrderEvent.ProductRemoved -> stateManager.apply(event)
            is SalesOrderEvent.ProductDelivered -> stateManager.apply(event)
            is SalesOrderEvent.ProductInvoiced -> stateManager.apply(event)
            is SalesOrderEvent.ProductPaid -> stateManager.apply(event)
        }.let { }
    }

    // PUBLIC QUERY API

    fun id(): AggregateRootId {
        return id
    }

    fun products(): List<Product> =
        state.products
            .map { it.product }

    fun products(deliveryStatus: DeliveryStatus): List<Product> =
        state.products
            .filter { it.deliveryStatus == deliveryStatus }
            .map { it.product }

    fun products(paymentStatus: PaymentStatus): List<Product> =
        state.products
            .filter { it.paymentStatus == paymentStatus }
            .map { it.product }

    fun salesOrderStatus(): Status {
        val delivered = state.products.filter { it.deliveryStatus == DeliveryStatus.DELIVERED }.size
        val total = state.products.size

        return when {
            state.deleted -> Status.DELETED
            delivered > 0 && total == delivered -> Status.DELIVERED
            delivered > 0 -> Status.PARTIALLY_DELIVERED
            state.confirmed -> Status.CONFIRMED
            else -> Status.QUOTE
        }
    }

    // PUBLIC BUSINESS FUNCTIONS

    fun setCustomer(customerId: EntityId) {
        applyChange(SalesOrderEvent.CustomerSet(customerId))
    }

    fun addProduct(product: Product) {
        val event = SalesOrderEvent.ProductAdded(
            product, PaymentStatus.OPEN, PaymentMethod.UNDEFINED, DeliveryStatus.NOT_DELIVERED
        )

        // update state
        applyChange(event)
    }

    fun removeProduct(product: Product) {
        val event = SalesOrderEvent.ProductRemoved(product)

        // update state
        applyChange(event)
    }

    fun deliverProducts() {
        products().forEach { deliverProduct(it) }
    }

    fun deliverProduct(product: Product) {
        val event = SalesOrderEvent.ProductDelivered(product)

        // update state
        applyChange(event)
    }

    fun invoiceDeliveredProducts(paymentService: DummyPaymentService) {
        val delivered = state.products
            .filter { it.deliveryStatus == DeliveryStatus.DELIVERED }
            .map { it.product }

        for (product in delivered) {
            // call external service
            paymentService.handleProductPayment(product, state.customerId, PaymentMethod.INVOICE)

            // update state
            applyChange(SalesOrderEvent.ProductInvoiced(product))
        }
    }

    fun payDeliveredProduct(paymentService: DummyPaymentService, product: Product, method: PaymentMethod) {
        val p = state.products.find { it.product.id == product.id }
        p ?: throw InvalidStateException(id, version, "Failed to pay product, product not found")

        paymentService.handleProductPayment(product, state.customerId, method)
        applyChange(SalesOrderEvent.ProductPaid(product))
    }

    fun deleteSalesOrder() {
        applyChange(SalesOrderEvent.Deleted)
    }

    fun confirmSalesOrder() {
        applyChange(SalesOrderEvent.Confirmed)
    }

}
