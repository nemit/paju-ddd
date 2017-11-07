package io.paju.ddd.infrastructure

import io.paju.ddd.AggregateRoot
import io.paju.ddd.AggregateRootId
import io.paju.ddd.AggregateState
import io.paju.ddd.Event

interface Repository<out S: AggregateState, E: Event, T : AggregateRoot<S, E>> {
    fun save(aggregate: T, version: Int)
    fun getById(id: AggregateRootId): T
}