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
                //aggregate.setCustomer(command.customerId)
                TODO()

            is DeliverProducts ->
                aggregate.deliverProducts()

            is  DeleteSalesOrder ->
                aggregate.deleteSalesOrder()

            is ConfirmSalesOrder ->
                aggregate.confirmSalesOrder()

            is AddProductToSalesOrder ->
                aggregate.addProduct(command.product)

            is RemoveProductFromSalesOrder ->
                aggregate.removeProduct(command.product)

            is DeliverProduct ->
                aggregate.deliverProduct(command.product)

            is PayDeliveredProducts ->
                aggregate.payDeliveredProduct(paymentService, command.product, command.method)

            is InvoiceDeliveredProducts ->
                aggregate.invoiceDeliveredProducts(paymentService)

        }.let {} // let is required for exhaustive when
    }
}