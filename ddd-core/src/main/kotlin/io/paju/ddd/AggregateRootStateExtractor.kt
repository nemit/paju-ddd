package io.paju.ddd

interface AggregateRootStateExtractor<in A: AggregateRoot, out S: AggregateRootState> {
    fun extractState(aggregate: A) : S
}