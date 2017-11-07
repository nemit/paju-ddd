package io.paju.ddd

interface AggregateStateExposer<out S: AggregateState> {
    fun state() : S
}