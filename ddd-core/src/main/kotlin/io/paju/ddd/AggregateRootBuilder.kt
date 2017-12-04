package io.paju.ddd

object AggregateRootBuilder {
    fun <A: AggregateRoot<S, E>, S: State, E : StateChangeEvent>build(constructor: () -> A): AggregateRoot.Builder<A, S, E> {
        return AggregateRoot.Builder(constructor)
    }
}