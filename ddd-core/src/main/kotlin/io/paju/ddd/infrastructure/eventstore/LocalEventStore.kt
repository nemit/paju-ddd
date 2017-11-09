package io.paju.ddd.infrastructure.eventstore

import io.paju.ddd.AggregateRootId
import io.paju.ddd.StateChangeEvent
import io.paju.ddd.exception.DddRuntimeException
import io.paju.ddd.infrastructure.EventStoreReader
import io.paju.ddd.infrastructure.EventStoreWriter
import org.slf4j.LoggerFactory
import java.util.ConcurrentModificationException
import java.util.concurrent.locks.ReentrantLock


class LocalEventStore : EventStoreReader, EventStoreWriter {

    private val logger = LoggerFactory.getLogger(this::class.java)
    private val storage: MutableMap<String, MutableList<StateChangeEvent>> = mutableMapOf()
    private val lock = ReentrantLock()

    override fun saveEvents(topicName: String, events: Iterable<StateChangeEvent>, expectedVersion: Int) {
        // check events
        val aggregateIds = events.map { it.id }.distinct()
        if (aggregateIds.size != 1) {
            throw DddRuntimeException("One and only one aggregate id is expected while saving events [${aggregateIds.joinToString(",")}]")
        }
        val aggregateId = aggregateIds.first()

        logger.debug("Saving events for [$topicName] with Id [$aggregateId]")

        lock.lock()
        try {
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
        } finally {
            lock.unlock()
        }
    }

    override fun getEventsForAggregate(topicName: String, id: AggregateRootId): Iterable<StateChangeEvent> {
        return storage.getOrElse(id.toString(), { listOf() })
    }
}