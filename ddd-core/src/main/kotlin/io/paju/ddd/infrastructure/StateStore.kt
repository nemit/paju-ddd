package io.paju.ddd.infrastructure

import io.paju.ddd.AggregateRootId
import io.paju.ddd.state.AggregateRootState
import io.paju.ddd.Event

interface StateStoreWriter<in A: AggregateRootState> {
    fun saveState(events: Iterable<Event>, expectedVersion: Int)
    fun saveState(state: A, expectedVersion: Int)
}

interface StateStoreReader<out A: AggregateRootState> {
    fun readState(id: AggregateRootId): A
}

