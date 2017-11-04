package io.paju.ddd

interface AggregateRootState {
    val id: AggregateRootId
    val version: Int
}