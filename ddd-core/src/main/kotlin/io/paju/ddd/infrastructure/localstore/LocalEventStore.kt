package io.paju.ddd.infrastructure.localstore

import io.paju.ddd.AggregateRootId
import io.paju.ddd.StateChangeEvent
import io.paju.ddd.StateChangeEventPublisher
import io.paju.ddd.infrastructure.EventStoreReader
import io.paju.ddd.infrastructure.EventStoreWriter
import org.slf4j.LoggerFactory
import java.util.ConcurrentModificationException
import java.util.concurrent.locks.ReentrantLock


class LocalEventStore : EventStoreReader, EventStoreWriter {

    private val logger = LoggerFactory.getLogger(this::class.java)
    private val storage: MutableMap<String, MutableList<StateChangeEvent>> = mutableMapOf()
    private val lock = ReentrantLock()
    private val publishers = mutableSetOf<StateChangeEventPublisher>()

    override fun saveEvents(
        topicName: String,
        id: AggregateRootId,
        events: Iterable<StateChangeEvent>,
        expectedVersion: Int)
    {
        logger.debug("Saving events for [$topicName] with Id [$id]")

        lock.lock()
        try {
            val storedEvents = storage.getOrElse(id.toString(), { mutableListOf() })

            //val actualVersion = storedEvents.lastOrNull()?.version ?: 0
            //if (actualVersion != expectedVersion && expectedVersion != -1) {
            //    throw ConcurrentModificationException("The actual version is [$actualVersion] and the expected version is [$expectedVersion]")
            //}

            // set event versions
            var version = expectedVersion
            for (event in events) {
                version++
                event.version = version
                storedEvents.add(event)
                publishers.forEach { it.publish(topicName, event) }
            }
            storage.put(id.toString(), storedEvents)
        } finally {
            lock.unlock()
        }
    }

    override fun getEventsForAggregate(topicName: String, id: AggregateRootId): Iterable<StateChangeEvent> {
        return storage.getOrElse(id.toString(), { listOf() })
    }

    fun addPublisher(publisher: StateChangeEventPublisher){
        publishers.add(publisher)
    }

    fun removePublisher(publisher: StateChangeEventPublisher){
        publishers.remove(publisher)
    }
}