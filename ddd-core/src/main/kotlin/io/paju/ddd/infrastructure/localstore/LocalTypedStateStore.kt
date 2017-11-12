package io.paju.ddd.infrastructure.localstore

import io.paju.ddd.AggregateRootId
import io.paju.ddd.State
import io.paju.ddd.infrastructure.StateStoreTypedReader
import io.paju.ddd.infrastructure.StateStoreTypedStateWriter
import org.slf4j.LoggerFactory
import java.util.ConcurrentModificationException
import java.util.concurrent.locks.ReentrantLock

class LocalTypedStateStore<S: State> : StateStoreTypedStateWriter<S>, StateStoreTypedReader<S> {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private val storage: MutableMap<AggregateRootId, S> = mutableMapOf()
    private val lock = ReentrantLock()

    override fun saveState(
        id: AggregateRootId,
        state: S,
        expectedVersion: Int)
    {
        logger.debug("Saving state for Id [$id]")

        lock.lock()
        try {
            val storedState = storage[id]
            val actualVersion = storedState?.version() ?: 1
            if (actualVersion != expectedVersion && expectedVersion != -1) {
                throw ConcurrentModificationException("The actual version is [$actualVersion] and the expected version is [$expectedVersion]")
            }
            storage.put(id, state)
        } finally {
            lock.unlock()
        }
    }

    override fun readState(id: AggregateRootId): S?  = storage[id]
}