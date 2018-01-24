package io.paju.ddd.exception

import io.paju.ddd.AggregateRoot

class InvalidStateException(message: String, aggregate: AggregateRoot<*, *>? = null)
    : DddRuntimeException(buildMessage(message, aggregate))

private fun buildMessage(message: String, aggregate: AggregateRoot<*, *>?): String {
    return if(aggregate != null){
        "${aggregate.javaClass.simpleName}(${aggregate.id.toUUID()}): $message"
    }else{
        message
    }
}