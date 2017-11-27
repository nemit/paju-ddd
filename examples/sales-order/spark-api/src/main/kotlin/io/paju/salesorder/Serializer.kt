package io.paju.salesorder

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.paju.salesorder.command.SalesOrderCommand
import io.paju.salesorder.domain.event.SalesOrderEvent
import io.paju.salesorder.domain.state.SalesOrderState

object Serializer {
    val objectMapper = ObjectMapper().apply {
        enableDefaultTyping()
        enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL)
        registerModule(KotlinModule())
    }

    fun jsonToCommand(json: String): SalesOrderCommand {
        return objectMapper.readValue(json, SalesOrderCommand::class.java)
    }

    fun <C: SalesOrderCommand>commandToJson(command: C): String {
        return objectMapper.writeValueAsString(command)
    }

    fun <E: SalesOrderEvent>eventToJson(event: E): String {
        return objectMapper.writeValueAsString(event)
    }

    fun stateToJson(state: SalesOrderState): String {
        return objectMapper.writeValueAsString(state)
    }

}