package io.paju.ddd.state

import io.paju.ddd.AggregateRoot

interface StateConstructor<out A: AggregateRoot, in S: AggregateRootState> {
    fun constructAggregate(state: S) : A
}