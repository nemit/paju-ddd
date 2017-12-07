package io.paju.salesorder.command

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonSubTypes.Type
import com.fasterxml.jackson.annotation.JsonTypeInfo
import io.paju.ddd.AggregateRootId
import io.paju.ddd.Command
import io.paju.ddd.EntityId
import io.paju.salesorder.domain.PaymentMethod
import io.paju.salesorder.domain.Product
import java.util.UUID

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type")
@JsonSubTypes(
    Type(name = "CreateSalesOrder", value = CreateSalesOrder::class),
    Type(name = "DeliverProducts", value = DeliverProducts::class),
    Type(name = "DeleteSalesOrder", value = DeleteSalesOrder::class),
    Type(name = "ConfirmSalesOrder", value = ConfirmSalesOrder::class),
    Type(name = "AddProductToSalesOrder", value = AddProductToSalesOrder::class),
    Type(name = "RemoveProductFromSalesOrder", value = RemoveProductFromSalesOrder::class),
    Type(name = "DeliverProduct", value = DeliverProduct::class),
    Type(name = "InvoiceDeliveredProducts", value = InvoiceDeliveredProducts::class),
    Type(name = "PayDeliveredProduct", value = PayDeliveredProduct::class)
)
sealed class SalesOrderCommand : Command

// Sales order Commands
data class CreateSalesOrder(
    override val id: AggregateRootId = AggregateRootId(UUID.randomUUID()),
    val customerId: EntityId) : SalesOrderCommand()
{
    override val originalVersion: Int = -1
}
data class DeliverProducts(override val id: AggregateRootId, override val originalVersion: Int) : SalesOrderCommand()
data class DeleteSalesOrder(override val id: AggregateRootId, override val originalVersion: Int) : SalesOrderCommand()
data class ConfirmSalesOrder(override val id: AggregateRootId, override val originalVersion: Int) : SalesOrderCommand()
data class AddProductToSalesOrder(override val id: AggregateRootId, override val originalVersion: Int, val product: Product) : SalesOrderCommand()
data class RemoveProductFromSalesOrder(override val id: AggregateRootId, override val originalVersion: Int, val product: Product) : SalesOrderCommand()
data class DeliverProduct(override val id: AggregateRootId, override val originalVersion: Int, val productId: EntityId) : SalesOrderCommand()
data class InvoiceDeliveredProducts(override val id: AggregateRootId, override val originalVersion: Int, val product: Product, val method: PaymentMethod) : SalesOrderCommand()
data class PayDeliveredProduct(override val id: AggregateRootId, override val originalVersion: Int, val productId: EntityId, val method: PaymentMethod) : SalesOrderCommand()
data class PayAllDeliveredProducts(override val id: AggregateRootId, override val originalVersion: Int, val method: PaymentMethod) : SalesOrderCommand()

