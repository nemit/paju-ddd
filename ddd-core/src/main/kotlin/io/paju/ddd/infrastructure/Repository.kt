package io.paju.ddd.infrastructure

import io.paju.ddd.AggregateRoot
import io.paju.ddd.AggregateRootId
import io.paju.ddd.StateChangeEvent
import io.paju.ddd.exception.DddRuntimeException
import java.util.UUID

interface Repository<E: StateChangeEvent, T : AggregateRoot<*, E>> {
    fun save(aggregate: T, version: Int)
    fun getById(id: AggregateRootId): T
    fun getById(id: UUID): T? {
        return try {
            getById(AggregateRootId(id))
        } catch (e: DddRuntimeException) {
            null
        }
    }
}