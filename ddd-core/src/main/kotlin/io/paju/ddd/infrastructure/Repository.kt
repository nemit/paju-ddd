package io.paju.ddd.infrastructure

import io.paju.ddd.AggregateRoot
import io.paju.ddd.StateChangeEvent
import java.util.UUID

interface Repository<E: StateChangeEvent, T : AggregateRoot<*, E>> {
    fun save(aggregate: T, version: Int)
    fun getAggregate(id: UUID): T
}