package io.paju.ddd

interface Command {
    val id: AggregateRootId
    val originalVersion: Int
}