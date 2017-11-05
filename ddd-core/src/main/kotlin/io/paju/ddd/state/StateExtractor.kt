package io.paju.ddd.state

import io.paju.ddd.AggregateRoot

interface StateExtractor<in A: AggregateRoot, out S: AggregateRootState> {
    fun extractAggregateState(aggregate: A) : S
}