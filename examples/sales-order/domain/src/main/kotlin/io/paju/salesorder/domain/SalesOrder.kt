package io.paju.salesorder.domain

import io.paju.ddd.AggregateRoot
import io.paju.ddd.AggregateRootId
import io.paju.ddd.EntityId
import io.paju.ddd.StateExposed
import io.paju.ddd.exception.InvalidStateException
import io.paju.salesorder.domain.event.SalesOrderEvent
import io.paju.salesorder.domain.state.SalesOrderState
import io.paju.salesorder.domain.state.SalesOrderStateManager
import io.paju.salesorder.service.DummyPaymentService

/**
 * SalesOrder is Aggregate responsible for Sales Order lifecycle starting from Quote to Confirmed and the to Delivered.
 * PaymentStatus is tracked per product
 */
class SalesOrder constructor(id: AggregateRootId) :
    AggregateRoot<SalesOrderState, SalesOrderEvent>(id, SalesOrderEvent.Init),
    StateExposed<SalesOrderState>
{
    private val stateManager = SalesOrderStateManager({ state })

    internal fun getEventMediator() = eventMediator
    override fun state(): SalesOrderState = state

    override fun apply(event: SalesOrderEvent): SalesOrderState {
        return when (event) {
            is SalesOrderEvent.Init -> SalesOrderState(1, null, false, false, mutableListOf())
            is SalesOrderEvent.CustomerSet -> stateManager.apply(event)
            is SalesOrderEvent.Deleted -> stateManager.apply(event)
            is SalesOrderEvent.Confirmed -> stateManager.apply(event)
            is SalesOrderEvent.ProductAdded -> stateManager.apply(event)
            is SalesOrderEvent.ProductRemoved -> stateManager.apply(event)
            is SalesOrderEvent.ProductDelivered -> stateManager.apply(event)
            is SalesOrderEvent.ProductInvoiced -> stateManager.apply(event)
            is SalesOrderEvent.ProductPaid -> stateManager.apply(event)
        }
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
        deliverProduct(product.id)
    }

    fun deliverProduct(productId: EntityId) {
        val productState = state.products.find( { it.product.id.equals(productId) && it.deliveryStatus == DeliveryStatus.NOT_DELIVERED })
        productState ?: throw InvalidStateException(id, version, "Failed to delive product, product with id ${productId.id} not found")
        val event = SalesOrderEvent.ProductDelivered(productState.product)
        // update state
        applyChange(event)
    }

    fun invoiceDeliveredProducts(paymentService: DummyPaymentService) {
        val customerId = state.customerId
        if (customerId != null) {
            val delivered = state.products
                .filter { it.deliveryStatus == DeliveryStatus.DELIVERED }
                .map { it.product }

            for (product in delivered) {
                // call external service
                paymentService.handleProductPayment(product, customerId, PaymentMethod.INVOICE)

                // update state
                applyChange(SalesOrderEvent.ProductInvoiced(product))
            }
        } else {
            throw InvalidStateException(id, version, "Customer id is null. Sales Order cannot invoice without customer id")
        }
    }

    fun payDeliveredProduct(paymentService: DummyPaymentService, product: Product, method: PaymentMethod) {
        payDeliveredProduct(paymentService, product.id, method)
    }

    fun payDeliveredProduct(paymentService: DummyPaymentService, productId: EntityId, method: PaymentMethod) {
        val customerId = state.customerId
        if (customerId != null) {
            val p = state.products.find { it.product.id == productId }
            p ?: throw InvalidStateException(id, version, "Failed to pay product, product not found")

            paymentService.handleProductPayment(p.product, customerId, method)
            applyChange(SalesOrderEvent.ProductPaid(p.product))
        } else {
            throw InvalidStateException(id, version, "Customer id is null. Payment not possible without customer id")
        }
    }

    fun deleteSalesOrder() {
        applyChange(SalesOrderEvent.Deleted)
    }

    fun confirmSalesOrder() {
        applyChange(SalesOrderEvent.Confirmed)
    }

    fun isEveryProductPaid() = products(PaymentStatus.OPEN).isEmpty()

    fun isEveryProductDelivered() = products(DeliveryStatus.NOT_DELIVERED).isEmpty()

}
