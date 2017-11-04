package io.paju.ddd

interface Event {
    val id: AggregateRootId
    val version: Int
}
