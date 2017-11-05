package io.paju.ddd.infrastructure.eventstore

import io.paju.ddd.AggregateRootId
import io.paju.ddd.Event
import io.paju.ddd.exception.DDDRuntimeException
import io.paju.ddd.infrastructure.EventStoreReader
import io.paju.ddd.infrastructure.EventStoreWriter
import org.slf4j.LoggerFactory
import java.util.ConcurrentModificationException

class LocalEventStore : EventStoreReader, EventStoreWriter {

    private val logger = LoggerFactory.getLogger(this::class.java)
    private val storage: MutableMap<String, MutableList<Event>> = mutableMapOf()

    override fun saveEvents(topicName: String, events: Iterable<Event>, expectedVersion: Int) {
        // check events
        val aggregateIds = events.map { it.id }.distinct()
        if (aggregateIds.size != 1) {
            throw DDDRuntimeException("One and only one aggregate id expected while saving events [${aggregateIds.joinToString(",")}]")
        }
        val aggregateId = aggregateIds.first()

        logger.debug("Saving events for [$topicName] with Id [$aggregateId]")
        val storedEvents = storage.getOrElse(aggregateId.toString(), { mutableListOf() })
        val actualVersion = storedEvents.lastOrNull()?.version ?: 0
        if (actualVersion != expectedVersion && expectedVersion != -1) {
            throw ConcurrentModificationException("The actual version is [$actualVersion] and the expected version is [$expectedVersion]")
        }

        // set event versions
        var version = expectedVersion
        for (event in events) {
            version++
            event.version = version
            storedEvents.add(event)
        }
        storage.put(aggregateId.toString(), storedEvents)
    }

    override fun getEventsForAggregate(topicName: String, id: AggregateRootId): Iterable<Event> {
        return storage.getOrElse(id.toString(), { listOf() })
    }
}