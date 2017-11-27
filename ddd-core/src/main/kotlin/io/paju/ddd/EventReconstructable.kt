package io.paju.ddd

interface EventReconstructable<in E : StateChangeEvent> {
    fun reconstruct(events: Iterable<E>)
}

