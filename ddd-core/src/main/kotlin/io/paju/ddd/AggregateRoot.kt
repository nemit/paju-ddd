package io.paju.ddd

abstract class AggregateRoot<S: State, E : StateChangeEvent>
{
    val id: AggregateRootId
    var version: Int = 0
    private var state: S
    private val changes: MutableList<E> // all new uncommitted events
    protected val eventController: EventController

    constructor(id: AggregateRootId) {
        this.id = id
        this.state = initialState()
        this.changes = mutableListOf<E>()
        this.eventController = EventController()
    }

    protected abstract fun instanceCreated(): E // the first event in event stream / list
    protected abstract fun initialState(): S
    protected abstract fun apply(event: E, toState: S): S

    // get aggregate state
    protected fun getState(): S = state

    // aggregate state modification events
    protected fun applyChange(event: E) {
        applyChange(event, true)
    }

    private fun applyChange(event: E, isNew: Boolean) {
        state = apply(event, state)
        if (isNew) {
            changes.add(event)
        }
    }

    inner class EventController {

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
                this.state = state
            }
    }
}

