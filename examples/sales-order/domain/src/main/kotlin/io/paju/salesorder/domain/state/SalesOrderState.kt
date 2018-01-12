package io.paju.salesorder.domain.state

import io.paju.ddd.EntityId
import io.paju.ddd.State

data class SalesOrderState (
    val version: Int,
    val customerId: EntityId?,
    val confirmed: Boolean,
    val deleted: Boolean,
    val products: List<ProductState>

) : State {
    override fun version(): Int = version

    companion object {
        val InitialState = SalesOrderState(
            1, null, false, false, mutableListOf()
        )
    }
}
