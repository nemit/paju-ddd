package io.paju.salesorder.command

import io.paju.ddd.CommandHandler
import io.paju.salesorder.domain.SalesOrder
import io.paju.salesorder.service.DummyPaymentService

class SalesOrderCommandHandler(
    private val aggregate: SalesOrder,
    private val paymentService: DummyPaymentService
) : CommandHandler<SalesOrderCommand> {

    override fun handle(command: SalesOrderCommand) {
        when (command) {
            is CreateSalesOrder ->
                Unit // TODO

            is DeliverProductsAndServices ->
                Unit // TODO

            is  DeleteSalesOrder ->
                Unit // TODO

            is ConfirmSalesOrder ->
                Unit // TODO

            is AddProductToSalesOrder ->
                aggregate.addProduct(command.product)

            is RemoveProductFromSalesOrder ->
                aggregate.removeProduct(command.product)

            is DeliverProduct ->
                aggregate.deliverProduct(command.product)

            is PayDeliveredProducts ->
                aggregate.payDeliveredProduct(paymentService, command.product, command.method)
        }.let {} // let is required for exhaustive when
    }
}