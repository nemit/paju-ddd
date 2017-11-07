package io.paju.ddd.infrastructure

import io.paju.ddd.AggregateRootId
import io.paju.ddd.AggregateState

interface StateStoreWriter<in A: AggregateState> {
    fun saveState(state: A, expectedVersion: Int)
}

interface StateStoreReader<out A: AggregateState> {
    fun readState(id: AggregateRootId): A
}

