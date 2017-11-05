package io.paju.salesorder.command

import io.paju.ddd.Command
import io.paju.ddd.CommandHandler
import io.paju.salesorder.domain.SalesOrder
import io.paju.salesorder.service.DummyPaymentService

class SalesOrderCommandHandler(
    private val aggregate: SalesOrder,
    private val paymentService: DummyPaymentService
) : CommandHandler {
    override fun handle(command: Command) {
        when (command) {
            //is CreateSalesOrder -> aggregate.

            is AddProductToSalesOrder ->
                aggregate.addProduct(command.product)

            is RemoveProductFromSalesOrder ->
                aggregate.removeProduct(command.product)

            is DeliverProduct ->
                aggregate.deliverProduct(command.product)

            is PayDeliveredProducts ->
                aggregate.payDeliveredProduct(paymentService, command.product, command.method)
        }
    }
}