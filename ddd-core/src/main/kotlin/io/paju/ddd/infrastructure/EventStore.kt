package io.paju.ddd.infrastructure

import io.paju.ddd.AggregateRootId
import io.paju.ddd.Event

interface EventStoreWriter {
    fun saveEvents(events: Iterable<Event>, expectedVersion: Int)
}

interface EventStoreReader {
    fun getEventsForAggregate(id: AggregateRootId): Iterable<Event>
}