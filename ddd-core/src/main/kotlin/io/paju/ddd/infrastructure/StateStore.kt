package io.paju.ddd.infrastructure

import io.paju.ddd.AggregateRootId
import io.paju.ddd.State
import io.paju.ddd.StateChangeEvent
import io.paju.ddd.exception.DddRuntimeException

interface StateStoreEventWriter {
    fun <E: StateChangeEvent>saveState(id: AggregateRootId, events: Iterable<E>, expectedVersion: Int)
}

interface StateStoreStateWriter {
    fun <S: State>saveState(id: AggregateRootId, state: S, expectedVersion: Int)
}

interface StateStoreReader {
    fun <S: State>readState(id: AggregateRootId, clazz: Class<S>): S?

    @Throws(DddRuntimeException::class)
    fun <S: State>readStateOrFail(id: AggregateRootId, clazz: Class<S>): S {
        return readState(id, clazz) ?: throw DddRuntimeException("Aggregate [$id] state not found")
    }
}

interface StateStoreTypedEventWriter<in E: StateChangeEvent> {
    fun saveState(id: AggregateRootId, events: Iterable<E>, expectedVersion: Int)
}

interface StateStoreTypedStateWriter<in S: State> {
    fun saveState(id: AggregateRootId, state: S, expectedVersion: Int)
}

interface StateStoreTypedReader<out S: State> {
    fun readState(id: AggregateRootId): S?

    @Throws(DddRuntimeException::class)
    fun readStateOrFail(id: AggregateRootId): S {
        return readState(id) ?: throw DddRuntimeException("Aggregate [$id] state not found")
    }
}
