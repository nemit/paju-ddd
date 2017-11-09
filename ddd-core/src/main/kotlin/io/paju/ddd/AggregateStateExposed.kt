package io.paju.ddd

interface AggregateStateExposed<out S: AggregateState> {
    fun state() : S
}