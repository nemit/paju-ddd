package io.paju.salesorder.command

import io.paju.ddd.AggregateRootId
import io.paju.ddd.Command
import io.paju.ddd.EntityId
import io.paju.ddd.NotInitializedAggregateRootId
import io.paju.salesorder.domain.PaymentMethod
import io.paju.salesorder.domain.Product

// Sales order Commands
data class CreateSalesOrder(val customerId: EntityId) : Command {
    override val id: AggregateRootId = NotInitializedAggregateRootId
    override val originalVersion: Int = -1
}
data class DeliverProductsAndServices(override val id: AggregateRootId, override val originalVersion: Int) : Command
data class DeleteSalesOrder(override val id: AggregateRootId, override val originalVersion: Int) : Command
data class ConfirmSalesOrder(override val id: AggregateRootId, override val originalVersion: Int) : Command


// Product Commands
data class AddProductToSalesOrder(override val id: AggregateRootId, override val originalVersion: Int, val product: Product) : Command
data class RemoveProductFromSalesOrder(override val id: AggregateRootId, override val originalVersion: Int, val product: Product) : Command
data class DeliverProduct(override val id: AggregateRootId, override val originalVersion: Int, val product: Product) : Command
data class PayDeliveredProducts(override val id: AggregateRootId, override val originalVersion: Int, val product: Product, val method: PaymentMethod) : Command


// Service Commands
//data class AddReservedServiceToSalesOrder(val salersOrderId: AggregateRootId, val service: Service)
//data class RemoveReservedServiceFromSalesOrder(val salersOrderId: AggregateRootId, val service: Service)
//data class DeliverService(val salersOrderId: AggregateRootId, val service: Service)
//data class PayDeliveredServices(val salersOrderId: AggregateRootId)


// Participant Commands
//data class AddParticipantToSalesOrder(val salersOrderId: AggregateRootId, val participant: Person, val role: ParticipantRole)
//data class RemoveParticipantFromSalesOrder(val salersOrderId: AggregateRootId, val participant: Person)
