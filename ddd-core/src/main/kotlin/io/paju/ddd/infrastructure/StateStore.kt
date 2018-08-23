package io.paju.ddd.infrastructure

import io.paju.ddd.State
import io.paju.ddd.StateChangeEvent
import io.paju.ddd.exception.DddException
import java.util.UUID

interface StateStoreEventWriter {
    fun <E: StateChangeEvent>saveState(id: UUID, events: Iterable<E>, expectedVersion: Int)
}

interface StateStoreStateWriter {
    fun <S: State>saveState(id: UUID, state: S, expectedVersion: Int)
}

interface StateStoreReader {
    fun <S: State>readState(id: UUID, clazz: Class<S>): S?

    @Throws(DddException::class)
    fun <S: State>readStateOrFail(id: UUID, clazz: Class<S>): S {
        return readState(id, clazz) ?: throw DddException("Aggregate [$id] state not found")
    }
}

interface StateStoreTypedEventWriter<in E: StateChangeEvent> {
    fun saveState(id: UUID, events: Iterable<E>, expectedVersion: Int)
}

interface StateStoreTypedStateWriter<in S: State> {
    fun saveState(id: UUID, state: S, expectedVersion: Int)
}

interface StateStoreTypedReader<out S: State> {
    fun readState(id: UUID): S?

    @Throws(DddException::class)
    fun readStateOrFail(id: UUID): S {
        return readState(id) ?: throw DddException("Aggregate [$id] state not found")
    }
}
