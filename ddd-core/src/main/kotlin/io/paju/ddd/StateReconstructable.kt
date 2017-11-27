package io.paju.ddd

interface StateReconstructable<in S: State> {
    fun reconstruct(state: S)
}
