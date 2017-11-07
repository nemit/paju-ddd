package io.paju.ddd

interface AggregateEventConstructor<out S: AggregateState, E : Event, out A: AggregateRoot<S, E>> {
    fun constructAggregate(id: AggregateRootId, events: Iterable<E>) : A
}