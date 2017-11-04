package io.paju.ddd

interface AggregateRootStateConstructor<out A: AggregateRoot, in S: AggregateRootState> {
    fun constructAggregate(state: S) : A
}