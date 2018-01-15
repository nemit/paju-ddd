package io.paju.ddd

import io.paju.ddd.exception.DddRuntimeException

abstract class AggregateRoot<S: State, E : StateChangeEvent>(val id: AggregateRootId)
{
    var version: Int = 0
    abstract protected var aggregateState: S
    private val changes: MutableList<E> = mutableListOf()// all new uncommitted events
    protected val eventMediator: EventMediator = EventMediator()

    protected abstract fun instanceCreated(): E // the first event in event stream / list
    protected abstract fun apply(event: E, toState: S): S

    // get aggregate state
    protected fun getState(): S = aggregateState

    // aggregate state modification events
    protected fun applyChange(event: E) {
        applyChange(event, true)
    }

    private fun applyChange(event: E, isNew: Boolean) {
        aggregateState = apply(event, aggregateState)
        if (isNew) {
            changes.add(event)
        }
    }

    inner class EventMediator {

        fun aggregateRootId() = id

        fun uncommittedChanges(): List<E> {
            return changes.toList()
        }

        fun markChangesAsCommitted() {
            changes.clear()
        }
    }

    class Builder<out A: AggregateRoot<S, E>, S: State, E : StateChangeEvent>
    internal constructor (constructor: () -> A)
    {
        private val aggregate = constructor()

        fun newInstance(): A =
            aggregate.apply {
                applyChange(aggregate.instanceCreated(), true)
            }

        fun fromEvents(events: Iterable<E>): A =
            aggregate.apply {
                events.forEach { applyChange(it, false) }
            }

        fun fromState(state: S): A =
            aggregate.apply {
                this.aggregateState = state
            }
    }
}

fun <C: Command>AggregateRoot<*,*>.checkId(command: C) {
    if(this.id != command.id){
        throw DddRuntimeException("Invalid command id: ${this.id} != $id")
    }
}