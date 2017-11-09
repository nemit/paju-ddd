package io.paju.ddd.infrastructure

import io.paju.ddd.AggregateRootId
import io.paju.ddd.State

interface StateStoreWriter<in A: State> {
    fun saveState(state: A, expectedVersion: Int)
}

interface StateStoreReader<out A: State> {
    fun readState(id: AggregateRootId): A
}

