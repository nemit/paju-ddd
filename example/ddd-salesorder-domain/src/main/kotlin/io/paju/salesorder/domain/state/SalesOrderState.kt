package io.paju.salesorder.domain.state

import io.paju.ddd.AggregateRootId
import io.paju.ddd.EntityId
import io.paju.ddd.state.AggregateRootState

data class SalesOrderState (
    override val id: AggregateRootId,
    val customerId: EntityId,
    val confirmed: Boolean,
    val deleted: Boolean,
    val products: List<ProductState>
) : AggregateRootState
