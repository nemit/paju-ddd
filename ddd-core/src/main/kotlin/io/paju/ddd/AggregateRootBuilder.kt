package io.paju.ddd

import java.util.UUID

object AggregateRootBuilder {
    fun <A: AggregateRoot<S, E>, S: State, E : StateChangeEvent>build(constructor: (UUID) -> A): AggregateRoot.Builder<A, S, E> {
        return AggregateRoot.Builder(constructor)
    }
}