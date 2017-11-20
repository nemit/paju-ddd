package io.paju.salesorder

import io.paju.ddd.AggregateRootId
import io.paju.ddd.StateChangeEvent
import io.paju.ddd.StateChangeEventPublisher
import io.paju.salesorder.command.CreateSalesOrder
import io.paju.salesorder.command.SalesOrderCommandHandler
import io.paju.salesorder.domain.event.SalesOrderEvent
import io.paju.salesorder.infrastructure.SalesOrderRepository
import mu.KotlinLogging
import org.eclipse.jetty.websocket.api.Session
import spark.Spark.*
import java.io.IOException
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect
import java.util.concurrent.ConcurrentLinkedQueue
import org.eclipse.jetty.websocket.api.annotations.WebSocket
import java.util.UUID

private val logger = KotlinLogging.logger {}

class SalesOrderRestApi(
    repository: SalesOrderRepository,
    webSocket: SalesOrderWebSocket,
    commandHandler: SalesOrderCommandHandler
) {
    init {

        webSocket("/sales-order-ws", webSocket)

        get("/hello") { _, _ -> "Hello from sales order" }

        // Style 1: Handle commands in separate path
        post("/sales-order/command/create-sales-order", "application/json") {req, _ ->
            val json = req.body()
            try {
                val command = Serializer.objectMapper.readValue(json, CreateSalesOrder::class.java)
                logger.info { "Sales order command ${command::class.simpleName} ${command.id}" }
                commandHandler.handle(command)
            }catch (t: Throwable){
                logger.error(t) { "Sales order command failed"}
            }
            "OK"
        }

        // Style 2: Handle commands polymorphism in json parser
        post("/sales-order/command", "application/json") {req, _ ->
            val json = req.body()
            try {
                val command = Serializer.jsonToCommand(json)
                logger.info { "Sales order command ${command::class.simpleName} ${command.id}" }
                commandHandler.handle(command)
            }catch (t: Throwable){
                logger.error(t) { "Sales order command failed"}
            }
            "OK"
        }

        get("/sales-order/:id") {req, _ ->
            val id = req.params(":id")
            logger.info { "Get sales order $id" }
            val aggregate = repository.getById(AggregateRootId.fromObject(UUID.fromString(id)))
            Serializer.stateToJson(aggregate.state)
        }

        awaitInitialization()
    }

}

@WebSocket
class SalesOrderWebSocket : StateChangeEventPublisher {

    private val sessions = ConcurrentLinkedQueue<Session>()
    var commandHandler: SalesOrderCommandHandler? = null

    @OnWebSocketConnect
    fun connected(session: Session) {
        logger.info { "Websocket connected ${session.remoteAddress.hostName}" }
        sessions.add(session)
    }

    @OnWebSocketClose
    @Suppress("unused_parameter")
    fun closed(session: Session, statusCode: Int, reason: String) {
        logger.info { "Websocket closed ${session.remoteAddress.hostName}" }
        sessions.remove(session)
    }

    @OnWebSocketMessage
    @Throws(IOException::class)
    fun message(session: Session, message: String) {
        logger.info {"Received ${session.remoteAddress.hostName} message: $message " }
        try{
            commandHandler?.handle(
                Serializer.jsonToCommand(message)
            )

        }catch (t: Throwable){
            logger.error(t) { "Websocket command handling failed"}
        }
        session.getRemote().sendString(message)
    }

    override fun publish(topicName: String, event: StateChangeEvent) {
        logger.info { "Publishing event ${event::class.simpleName} to websocket" }
        when(event) {
            is SalesOrderEvent -> sendToAll(event)
        }
    }

    private fun sendToAll(event: SalesOrderEvent) {
        val jsonEvent = Serializer.eventToJson(event)
        sessions.forEach { it.remote.sendString(jsonEvent) }
    }
}
