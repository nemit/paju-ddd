package io.paju.ddd.infrastructure

import io.paju.ddd.StateChangeEvent
import java.util.UUID

interface EventStoreWriter {
    fun saveEvents(topicName: String, id: UUID, events: Iterable<StateChangeEvent>, expectedVersion: Int)
}

interface EventStoreReader {

    fun getEventsForAggregate(topicName: String, id: UUID): Iterable<StateChangeEvent>

    fun <E: StateChangeEvent>getEventsForAggregate(topicName: String, id: UUID, clazz: Class<E>): Iterable<E> {
        return getEventsForAggregate(topicName, id).map { clazz.cast(it) }
    }

}