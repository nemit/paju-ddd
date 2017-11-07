package io.paju.ddd

interface AggregateStateConstructor<S: AggregateState, E : Event, out A: AggregateRoot<S, E>> {
    fun constructAggregate(state: S) : A
}
