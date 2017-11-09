package io.paju.salesorder.domain.state

import io.paju.ddd.State
import io.paju.ddd.EntityId

data class SalesOrderState (
    val customerId: EntityId,
    val confirmed: Boolean,
    val deleted: Boolean,
    val products: List<ProductState>
) : State
