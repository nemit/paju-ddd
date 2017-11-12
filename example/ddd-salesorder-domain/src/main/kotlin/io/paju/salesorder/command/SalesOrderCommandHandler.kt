package io.paju.salesorder.command

import io.paju.ddd.AggregateRootId
import io.paju.ddd.CommandHandler
import io.paju.ddd.infrastructure.Repository
import io.paju.salesorder.domain.SalesOrder
import io.paju.salesorder.domain.event.SalesOrderEvent
import io.paju.salesorder.service.DummyPaymentService

class SalesOrderCommandHandler(
    private val repository: Repository<SalesOrderEvent, SalesOrder>,
    private val paymentService: DummyPaymentService
) : CommandHandler<SalesOrderCommand> {

    override fun handle(command: SalesOrderCommand) {

        fun aggregate() = repository.getById(command.id)
        fun createNewAggregate(id: AggregateRootId) = SalesOrder(id)

        val aggregate: SalesOrder = when (command) {
            is CreateSalesOrder ->
                createNewAggregate(command.id).apply { setCustomer(command.customerId) }

            is DeliverProducts ->
                aggregate().apply { deliverProducts() }

            is  DeleteSalesOrder ->
                aggregate().apply { deleteSalesOrder() }

            is ConfirmSalesOrder ->
                aggregate().apply { confirmSalesOrder() }

            is AddProductToSalesOrder ->
                aggregate().apply { addProduct(command.product) }

            is RemoveProductFromSalesOrder ->
                aggregate().apply { removeProduct(command.product) }

            is DeliverProduct ->
                aggregate().apply { deliverProduct(command.product) }

            is PayDeliveredProducts ->
                aggregate().apply { payDeliveredProduct(paymentService, command.product, command.method) }

            is InvoiceDeliveredProducts ->
                aggregate().apply { invoiceDeliveredProducts(paymentService) }

        }

        repository.save(aggregate, command.originalVersion)
    }
}