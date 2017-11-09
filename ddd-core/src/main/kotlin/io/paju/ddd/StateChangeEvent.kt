package io.paju.ddd

abstract class StateChangeEvent {
    abstract val id: AggregateRootId
    var version: Int = -1
}
