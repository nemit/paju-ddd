package io.paju.salesorder.infrastructure

import io.paju.ddd.AggregateRootId
import io.paju.ddd.Event
import io.paju.ddd.infrastructure.EventStoreWriter
import io.paju.ddd.infrastructure.Repository
import io.paju.ddd.infrastructure.StateStoreReader
import io.paju.ddd.infrastructure.StateStoreWriter
import io.paju.salesorder.domain.DeliveryStatus
import io.paju.salesorder.domain.PaymentStatus
import io.paju.salesorder.domain.Product
import io.paju.salesorder.domain.SalesOrder
import io.paju.salesorder.domain.event.ProductAdded
import io.paju.salesorder.domain.event.ProductDelivered
import io.paju.salesorder.domain.event.ProductInvoiced
import io.paju.salesorder.domain.event.ProductPaid
import io.paju.salesorder.domain.event.SalesOrderConfirmed
import io.paju.salesorder.domain.event.SalesOrderDeleted
import io.paju.salesorder.domain.state.ProductState
import io.paju.salesorder.domain.state.SalesOrderState

abstract class SalesOrderRepository(
    private val eventWriter: EventStoreWriter,
    private val stateWriter: StateStoreWriter<SalesOrderState>,
    private val stateReader: StateStoreReader<SalesOrderState>
) : Repository<SalesOrder> {

    override fun save(aggregate: SalesOrder, version: Int) {
        val uncommitted = aggregate.uncommittedChanges()

        // save state from events
        stateWriter.saveState(uncommitted, version)

        // save state snapshot
        stateWriter.saveState(SalesOrder.extractAggregateState(aggregate), version)

        // save events
        eventWriter.saveEvents("salesorder", uncommitted, version)

        // mark changes saved
        aggregate.markChangesAsCommitted()
    }

    override fun getById(id: AggregateRootId): SalesOrder {
        val salesOrderState = stateReader.readState(id)
        return SalesOrder.constructAggregate(salesOrderState)
    }

}

abstract class SalesOrderStore : StateStoreWriter<SalesOrderState>, StateStoreReader<SalesOrderState> {

    abstract fun getSalesOrderWithoutRelations(id: AggregateRootId): SalesOrderState
    abstract fun getProducts(id: AggregateRootId): List<ProductState>
    abstract fun getProduct(id: AggregateRootId, product: Product): ProductState
    abstract fun add(id: AggregateRootId, product: ProductState)
    abstract fun update(id: AggregateRootId, product: ProductState)
    abstract fun add(salesOrder: SalesOrderState)
    abstract fun update(salesOrder: SalesOrderState)
    abstract fun saveSnapshot(salesOrder: SalesOrderState)

    override fun saveState(events: Iterable<Event>, expectedVersion: Int) {
        events.forEach { it -> saveState(it) }
    }

    private fun saveState(e: Event) {
        when (e) {
            is SalesOrderDeleted ->
                update(
                    getSalesOrderWithoutRelations(e.id).copy(deleted = true)
                )

            is SalesOrderConfirmed ->
                update(
                    getSalesOrderWithoutRelations(e.id).copy(confirmed = true)
                )

            is ProductAdded ->
                add(
                    e.id, ProductState(e.product, e.paymentStatus, e.paymentMethod, e.deliveryStatus)
                )

            is ProductDelivered ->
                update(
                    e.id, getProduct(e.id, e.product).copy(deliveryStatus = DeliveryStatus.DELIVERED)
                )

            is ProductInvoiced ->
                update(
                    e.id, getProduct(e.id, e.product).copy(paymentStatus = PaymentStatus.INVOICED)
                )

            is ProductPaid ->
                update(
                    e.id, getProduct(e.id, e.product).copy(paymentStatus = PaymentStatus.PAID)
                )
        }
    }

    override fun readState(id: AggregateRootId): SalesOrderState {
        val salesOrder = getSalesOrderWithoutRelations(id)
        val products = getProducts(id)
        return salesOrder.copy(products = products)
    }

    override fun saveState(state: SalesOrderState, expectedVersion: Int) {
        saveSnapshot(state)
    }

}
