package io.paju.ddd.infrastructure

import io.paju.ddd.AggregateRootId
import io.paju.ddd.State
import io.paju.ddd.StateChangeEvent

interface StateStoreEventWriter<in E: StateChangeEvent> {
    fun saveState(id: AggregateRootId, events: Iterable<E>, expectedVersion: Int)
}

interface StateStoreStateWriter<in A: State> {
    fun saveState(id: AggregateRootId, state: A, expectedVersion: Int)
}

interface StateStoreReader<out A: State> {
    fun readState(id: AggregateRootId): A
}

