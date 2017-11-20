package io.paju.salesorder

import io.paju.ddd.EntityId
import io.paju.ddd.infrastructure.localstore.LocalEventStore
import io.paju.salesorder.command.SalesOrderCommandHandler
import io.paju.salesorder.domain.PaymentMethod
import io.paju.salesorder.domain.Product
import io.paju.salesorder.infrastructure.SalesOrderRepository
import io.paju.salesorder.infrastructure.SalesOrderStoreJdbc
import io.paju.salesorder.service.DummyPaymentService
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

@Suppress("unused_parameter")
fun main(args : Array<String>){

    // 1) WS ws://localhost:4567/sales-order-ws
    // 2) GET http://localhost:4567/hello
    // 3) POST http://localhost:4567/sales-order/command OR ws://localhost:4567/sales-order-ws
        //- {"type":"CreateSalesOrder","id":{"id":"e94ce3ee-1631-47f7-a1e1-3a3f4f3fa4a5"},"customerId":{"id":"0"},"originalVersion":-1}
        //- {"type":"DeleteSalesOrder","id":{"id":"e94ce3ee-1631-47f7-a1e1-3a3f4f3fa4a5"},"originalVersion":1}
        //- {"type":"AddProductToSalesOrder","id":{"id":"e94ce3ee-1631-47f7-a1e1-3a3f4f3fa4a5"},"originalVersion":1,"product":{"id":{"id":"0"},"price":{"price":["java.math.BigDecimal",1],"vat":"vat0","currency":"EURO"},"name":"qwerty","description":"qwerty"}}
    // 4) GET http://localhost:4567/sales-order/e94ce3ee-1631-47f7-a1e1-3a3f4f3fa4a5

    // init postgres
    val embeddedPostgres = EmbeddedPostgresServer.instance
    logger.info { "Postgres started to ${embeddedPostgres.url}" }

    // init aggregate and spark api
    val webSocket = SalesOrderWebSocket()
    val store = SalesOrderStoreJdbc(embeddedPostgres.url)
    val eventWriter = LocalEventStore().apply { addPublisher(webSocket) }
    val repository = SalesOrderRepository(eventWriter, store, store, store)
    val commandHandler = SalesOrderCommandHandler(repository, DummyPaymentServiceImpl)
    webSocket.commandHandler = commandHandler
    SalesOrderRestApi(repository, webSocket, commandHandler)
}

object DummyPaymentServiceImpl : DummyPaymentService {
    @Suppress("unused_parameter")
    override fun handleProductPayment(product: Product, customerId: EntityId, paymentMethod: PaymentMethod) {
    }
}