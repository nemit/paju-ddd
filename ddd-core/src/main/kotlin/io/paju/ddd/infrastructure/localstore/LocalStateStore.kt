package io.paju.ddd.infrastructure.localstore

import io.paju.ddd.AggregateRootId
import io.paju.ddd.State
import io.paju.ddd.infrastructure.StateStoreReader
import io.paju.ddd.infrastructure.StateStoreStateWriter
import org.slf4j.LoggerFactory
import java.util.ConcurrentModificationException
import java.util.concurrent.locks.ReentrantLock

class LocalStateStore : StateStoreStateWriter, StateStoreReader {

    private val logger = LoggerFactory.getLogger(this::class.java)
    private val storage: MutableMap<String, State> = mutableMapOf()
    private val lock = ReentrantLock()

    private fun <S: State>getKey(id: AggregateRootId, clazz: Class<S>): String =
        "${clazz.simpleName}:$id"

    override fun <S: State>saveState(
        id: AggregateRootId,
        state: S,
        expectedVersion: Int)
    {
        logger.debug("Saving state for Id [$id]")

        val key = getKey(id, state.javaClass)
        lock.lock()
        try {
            val storedState = storage[key]
            val actualVersion = storedState?.version() ?: 1
            if (actualVersion != expectedVersion && expectedVersion != -1) {
                throw ConcurrentModificationException("The actual version is [$actualVersion] and the expected version is [$expectedVersion]")
            }
            storage.put(key, state)
        } finally {
            lock.unlock()
        }
    }

    override fun <S: State>readState(id: AggregateRootId, clazz: Class<S>): S? {
        val state = storage[getKey(id, clazz)]
        return if (state != null) clazz.cast(state) else null
    }
}