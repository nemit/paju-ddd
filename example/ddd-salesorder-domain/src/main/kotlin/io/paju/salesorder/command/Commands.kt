package io.paju.salesorder.command

import io.paju.ddd.AggregateRootId
import io.paju.ddd.Command
import io.paju.ddd.EntityId
import io.paju.ddd.NotInitializedAggregateRootId
import io.paju.salesorder.domain.PaymentMethod
import io.paju.salesorder.domain.Product

sealed class SalesOrderCommand : Command

// Sales order Commands
data class CreateSalesOrder(val customerId: EntityId) : SalesOrderCommand() {
    override val id: AggregateRootId = NotInitializedAggregateRootId
    override val originalVersion: Int = -1
}
data class DeliverProductsAndServices(override val id: AggregateRootId, override val originalVersion: Int) : SalesOrderCommand()
data class DeleteSalesOrder(override val id: AggregateRootId, override val originalVersion: Int) : SalesOrderCommand()
data class ConfirmSalesOrder(override val id: AggregateRootId, override val originalVersion: Int) : SalesOrderCommand()


// Product Commands
data class AddProductToSalesOrder(override val id: AggregateRootId, override val originalVersion: Int, val product: Product) : SalesOrderCommand()
data class RemoveProductFromSalesOrder(override val id: AggregateRootId, override val originalVersion: Int, val product: Product) : SalesOrderCommand()
data class DeliverProduct(override val id: AggregateRootId, override val originalVersion: Int, val product: Product) : SalesOrderCommand()
data class PayDeliveredProducts(override val id: AggregateRootId, override val originalVersion: Int, val product: Product, val method: PaymentMethod) : SalesOrderCommand()
