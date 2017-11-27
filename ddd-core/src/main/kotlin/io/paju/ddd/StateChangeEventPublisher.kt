package io.paju.ddd

interface StateChangeEventPublisher {
    fun publish(topicName: String, event: StateChangeEvent)
}