package io.paju.ddd

interface AggregateEventReconstructable<in E : Event> {
    fun reconstruct(events: Iterable<E>)
}

