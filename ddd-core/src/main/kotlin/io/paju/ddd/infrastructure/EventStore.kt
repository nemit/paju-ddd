package io.paju.ddd.infrastructure

import io.paju.ddd.AggregateRootId
import io.paju.ddd.Event

interface EventStoreWriter {
    fun saveEvents(topicName: String, events: Iterable<Event>, expectedVersion: Int)
}

interface EventStoreReader {
    fun getEventsForAggregate(topicName: String, id: AggregateRootId): Iterable<Event>
}