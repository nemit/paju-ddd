package io.paju.ddd

interface AggregateStateReconstructable<in S: AggregateState> {
    fun reconstruct(state: S)
}
