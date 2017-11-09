package io.paju.ddd.infrastructure

import io.paju.ddd.AggregateRootId
import io.paju.ddd.StateChangeEvent

interface EventStoreWriter {
    fun saveEvents(topicName: String, events: Iterable<StateChangeEvent>, expectedVersion: Int)
}

interface EventStoreReader {

    fun getEventsForAggregate(topicName: String, id: AggregateRootId): Iterable<StateChangeEvent>

    fun <E: StateChangeEvent>getEventsForAggregate(topicName: String, id: AggregateRootId, clazz: Class<E>): Iterable<E> {
        return getEventsForAggregate(topicName, id).map { clazz.cast(it) }
    }

}