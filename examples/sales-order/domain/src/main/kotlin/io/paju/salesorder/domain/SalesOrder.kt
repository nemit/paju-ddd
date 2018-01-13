package io.paju.salesorder.domain

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.and
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
    AggregateRoot<SalesOrderState, SalesOrderEvent>(id),
    StateExposed<SalesOrderState>
{
    private val stateManager = SalesOrderStateManager({ getState() })
    override var aggregateState =  SalesOrderState(
        1, null, false, false, mutableListOf()
    )

    internal fun getEventMediator() = eventMediator
    override fun state(): SalesOrderState = getState()
    override fun instanceCreated(): SalesOrderEvent = SalesOrderEvent.Created

    override fun apply(event: SalesOrderEvent, toState: SalesOrderState): SalesOrderState {
        return when (event) {
            is SalesOrderEvent.Created -> aggregateState
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
        getState().products
            .map { it.product }

    fun products(deliveryStatus: DeliveryStatus): List<Product> =
        getState().products
            .filter { it.deliveryStatus == deliveryStatus }
            .map { it.product }

    fun products(paymentStatus: PaymentStatus): List<Product> =
        getState().products
            .filter { it.paymentStatus == paymentStatus }
            .map { it.product }

    fun salesOrderStatus(): Status {
        val delivered = getState().products.filter { it.deliveryStatus == DeliveryStatus.DELIVERED }.size
        val total = getState().products.size

        return when {
            getState().deleted -> Status.DELETED
            delivered > 0 && total == delivered -> Status.DELIVERED
            delivered > 0 -> Status.PARTIALLY_DELIVERED
            getState().confirmed -> Status.CONFIRMED
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

    fun deliverProducts(): Result<SalesOrderState, Throwable> {
        val results = products().map { deliverProduct(it) }
        return results.fold(results.first(), {prev, current -> prev.and(current)} )
    }

    fun deliverProduct(product: Product): Result<SalesOrderState, Throwable> {
        return deliverProduct(product.id)
    }

    fun deliverProduct(productId: EntityId): Result<SalesOrderState, Throwable> = Result.of({
        val productState = getState().products.find({ it.product.id.equals(productId) && it.deliveryStatus == DeliveryStatus.NOT_DELIVERED })
        productState ?: throw InvalidStateException(id, version, "Failed to delive product, product with id ${productId.id} not found")
        val event = SalesOrderEvent.ProductDelivered(productState.product)
        // update state
        applyChange(event)
        getState()
    })

    fun invoiceDeliveredProducts(paymentService: DummyPaymentService): Result<SalesOrderState, Throwable> {
        val customerId = getState().customerId
        return Result.of({
            when (customerId) {
                null -> throw InvalidStateException(id, version,
                    "Customer id is null. Sales Order cannot invoice without customer id")
                else -> {
                    val delivered = getState().products
                        .filter { it.deliveryStatus == DeliveryStatus.DELIVERED }
                        .map { it.product }

                    for (product in delivered) {
                        // call external service
                        paymentService.handleProductPayment(product, customerId, PaymentMethod.INVOICE)

                        // update state
                        applyChange(SalesOrderEvent.ProductInvoiced(product))
                    }
                    getState()
                }
            }
        })
    }

    fun payDeliveredProduct(paymentService: DummyPaymentService, product: Product, method: PaymentMethod) {
        payDeliveredProduct(paymentService, product.id, method)
    }

    fun payDeliveredProduct(paymentService: DummyPaymentService,
        productId: EntityId,
        method: PaymentMethod): Result<SalesOrderState, Throwable> {
        val customerId = getState().customerId
        return Result.of({
            when(customerId) {
                null -> throw InvalidStateException(id, version,
                    "Customer id is null. Payment not possible without customer id")
                else -> {
                    val p = getState().products.find { it.product.id == productId }
                    p ?: throw InvalidStateException(id, version, "Failed to pay product, product not found")
                    paymentService.handleProductPayment(p.product, customerId, method)
                    applyChange(SalesOrderEvent.ProductPaid(p.product))
                    getState()
                }
            }
        })
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
