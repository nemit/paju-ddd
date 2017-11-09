package io.paju.ddd.infrastructure

import io.paju.ddd.AggregateRoot
import io.paju.ddd.AggregateRootId
import io.paju.ddd.State
import io.paju.ddd.StateChangeEvent

interface Repository<out S: State, E: StateChangeEvent, T : AggregateRoot<S, E>> {
    fun save(aggregate: T, version: Int)
    fun getById(id: AggregateRootId): T
}