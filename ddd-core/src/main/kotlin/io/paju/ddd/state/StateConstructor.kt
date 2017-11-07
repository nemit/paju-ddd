package io.paju.ddd.state

import io.paju.ddd.AggregateRoot
import io.paju.ddd.Event

interface StateConstructor<out A: AggregateRoot, in S: AggregateRootState, F: Event, E: Event> {
    fun constructAggregate(state: S) : A
    fun constructAggregateFromEvents(creationEvent: F, events: List<E>): A
}