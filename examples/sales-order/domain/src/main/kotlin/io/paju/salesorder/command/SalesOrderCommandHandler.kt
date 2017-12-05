package io.paju.salesorder.command

import io.paju.ddd.AggregateRootBuilder
import io.paju.ddd.AggregateRootId
import io.paju.ddd.CommandHandler
import io.paju.ddd.infrastructure.Repository
import io.paju.salesorder.domain.DeliveryStatus
import io.paju.salesorder.domain.SalesOrder
import io.paju.salesorder.domain.event.SalesOrderEvent
import io.paju.salesorder.service.DummyPaymentService
import mu.KotlinLogging
import java.util.UUID

private val logger = KotlinLogging.logger {}

class SalesOrderCommandHandler(
    private val repository: Repository<SalesOrderEvent, SalesOrder>,
    private val paymentService: DummyPaymentService
) : CommandHandler<SalesOrderCommand> {

    override fun handle(command: SalesOrderCommand) {
        logger.info { "Handle command ${command::class.simpleName} ${command.id}" }

        fun aggregate() = repository.getById(command.id)

        val aggregate: SalesOrder = when (command) {
            is CreateSalesOrder -> {
                val aggregateId: AggregateRootId =
                    if (command.id == AggregateRootId.NotInitialized) {
                        AggregateRootId.fromObject(UUID.randomUUID())
                    } else {
                        command.id
                    }
                AggregateRootBuilder
                    .build { SalesOrder(aggregateId) }
                    .newInstance()
            }

            is DeliverProducts ->
                aggregate().apply { deliverProducts() }

            is DeleteSalesOrder ->
                aggregate().apply { deleteSalesOrder() }

            is ConfirmSalesOrder ->
                aggregate().apply { confirmSalesOrder() }

            is AddProductToSalesOrder ->
                aggregate().apply { addProduct(command.product) }

            is RemoveProductFromSalesOrder ->
                aggregate().apply { removeProduct(command.product) }

            is DeliverProduct ->
                aggregate().apply { deliverProduct(command.productId) }

            is PayDeliveredProduct ->
                aggregate().apply { payDeliveredProduct(paymentService, command.productId, command.method) }

            is PayAllDeliveredProducts ->
                aggregate().apply {
                    products(DeliveryStatus.DELIVERED).forEach { }
                }

            is InvoiceDeliveredProducts ->
                aggregate().apply { invoiceDeliveredProducts(paymentService) }

        }

        repository.save(aggregate, command.originalVersion)
    }
}