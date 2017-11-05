package io.paju.salesorder.domain.internal

import io.paju.ddd.AggregateRootId
import io.paju.ddd.AggregateRootState
import io.paju.ddd.EntityId

data class SalesOrderState (
    override val id: AggregateRootId,
    override val version: Int,
    val customerId: EntityId,
    val confirmed: Boolean,
    val deleted: Boolean,
    val products: List<ProductState>
) : AggregateRootState
