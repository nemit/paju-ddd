package io.paju.ddd

abstract class Event {
    abstract val id: AggregateRootId
    var version: Int = -1
}
