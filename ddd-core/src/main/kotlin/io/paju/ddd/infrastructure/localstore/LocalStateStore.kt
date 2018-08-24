package io.paju.ddd.infrastructure.localstore

import io.paju.ddd.State
import io.paju.logger
import java.util.ConcurrentModificationException
import java.util.UUID
import java.util.concurrent.locks.ReentrantLock

class LocalStateStore<S: State> {
    private val logger = logger()
    private val storage: MutableMap<UUID, S> = mutableMapOf()
    private val lock = ReentrantLock()

    fun saveState(
        id: UUID,
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

    fun readState(id: UUID): S? = storage[id]
}