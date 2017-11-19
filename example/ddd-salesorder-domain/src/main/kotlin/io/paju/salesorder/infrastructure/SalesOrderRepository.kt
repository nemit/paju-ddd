package io.paju.salesorder.infrastructure

import io.paju.ddd.AggregateRootId
import io.paju.ddd.EntityId
import io.paju.ddd.infrastructure.EventStoreWriter
import io.paju.ddd.infrastructure.Repository
import io.paju.ddd.infrastructure.StateStoreTypedEventWriter
import io.paju.ddd.infrastructure.StateStoreTypedReader
import io.paju.ddd.infrastructure.StateStoreTypedStateWriter
import io.paju.salesorder.domain.DeliveryStatus
import io.paju.salesorder.domain.PaymentStatus
import io.paju.salesorder.domain.Product
import io.paju.salesorder.domain.SalesOrder
import io.paju.salesorder.domain.event.SalesOrderEvent
import io.paju.salesorder.domain.state.ProductState
import io.paju.salesorder.domain.state.SalesOrderState

class SalesOrderRepository(
    private val eventWriter: EventStoreWriter,
    private val stateWriter: StateStoreTypedEventWriter<SalesOrderEvent>,
    private val stateSnapshotWriter: StateStoreTypedStateWriter<SalesOrderState>,
    private val stateReader: StateStoreTypedReader<SalesOrderState>
) : Repository<SalesOrderEvent, SalesOrder> {

    override fun save(aggregate: SalesOrder, version: Int) {
        val uncommitted = aggregate.uncommittedChanges()

        // save state from events
        stateWriter.saveState(aggregate.id(), uncommitted, version)

        // save state snapshot
        stateSnapshotWriter.saveState(aggregate.id(), aggregate.state(), version)

        // save events
        eventWriter.saveEvents("salesorder", aggregate.id(), uncommitted, version)

        // mark changes saved
        aggregate.markChangesAsCommitted()
    }

    override fun getById(id: AggregateRootId): SalesOrder {
        return SalesOrder(id, false).apply {
            reconstruct(stateReader.readStateOrFail(id))
        }
    }

}

abstract class SalesOrderStore :
    StateStoreTypedEventWriter<SalesOrderEvent>,
    StateStoreTypedStateWriter<SalesOrderState>,
    StateStoreTypedReader<SalesOrderState>
{

    abstract fun getSalesOrderWithoutRelations(id: AggregateRootId): SalesOrderState
    abstract fun getProducts(id: AggregateRootId): List<ProductState>
    abstract fun getProduct(id: AggregateRootId, product: Product): ProductState
    abstract fun add(id: AggregateRootId, product: ProductState)
    abstract fun remove(id: AggregateRootId, productId: EntityId)
    abstract fun update(id: AggregateRootId, product: ProductState)
    abstract fun create(id: AggregateRootId)
    abstract fun update(id: AggregateRootId, salesOrder: SalesOrderState)
    abstract fun saveSnapshot(id: AggregateRootId, salesOrder: SalesOrderState)

    override fun saveState(id: AggregateRootId, events: Iterable<SalesOrderEvent>, expectedVersion: Int) {
        events.forEach { it -> saveState(id, it) }
    }

    private fun saveState(id: AggregateRootId, e: SalesOrderEvent) {
        when (e) {
            is SalesOrderEvent.Created ->
                create(id)

            is SalesOrderEvent.CustomerSet ->
                update(id, getSalesOrderWithoutRelations(id).copy(customerId = e.customerId) )

            is SalesOrderEvent.Deleted ->
                update(id, getSalesOrderWithoutRelations(id).copy(deleted = true) )

            is SalesOrderEvent.Confirmed ->
                update(id, getSalesOrderWithoutRelations(id).copy(confirmed = true) )

            is SalesOrderEvent.ProductAdded ->
                add(id, ProductState(e.product, e.paymentStatus, e.paymentMethod, e.deliveryStatus))

            is SalesOrderEvent.ProductRemoved ->
                remove(id, e.product.id)

            is SalesOrderEvent.ProductDelivered ->
                update(id, getProduct(id, e.product).copy(deliveryStatus = DeliveryStatus.DELIVERED))

            is SalesOrderEvent.ProductInvoiced ->
                update(id, getProduct(id, e.product).copy(paymentStatus = PaymentStatus.INVOICED))

            is SalesOrderEvent.ProductPaid ->
                update(id, getProduct(id, e.product).copy(paymentStatus = PaymentStatus.PAID))
        }.let { }
    }

    override fun readState(id: AggregateRootId): SalesOrderState {
        val salesOrder = getSalesOrderWithoutRelations(id)
        val products = getProducts(id)
        return salesOrder.copy(products = products)
    }

    override fun saveState(id: AggregateRootId, state: SalesOrderState, expectedVersion: Int) {
        saveSnapshot(id, state)
    }
}
