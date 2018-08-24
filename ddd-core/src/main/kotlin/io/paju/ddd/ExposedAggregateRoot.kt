package io.paju.ddd

import java.util.UUID

abstract class ExposedAggregateRoot<S: State, E: StateChangeEvent>(id: UUID):  AggregateRoot<S, E>(id) {
    fun id(): UUID = id
    fun state(): S = state
    fun exposedState() : ExposedState<S> = ExposedState(id(), state())
}

data class ExposedState<out S: State>(val id: UUID, val state: S)