package io.paju.ddd

abstract class AggregateRoot<S: State, E : StateChangeEvent>
{
    val id: AggregateRootId
    var version: Int = 0
    private var state: S
    private val changes: MutableList<E> // all new uncommitted events

    constructor(id: AggregateRootId){
        this.id = id
        this.state = initialState()
        this.changes = mutableListOf<E>()
    }

    protected abstract fun initialState(): S
    protected abstract fun apply(event: E, toState: S): S

    // get aggregate state
    protected fun getState(): S = state

    fun uncommittedChanges(): List<E> {
        return changes.toList()
    }

    fun markChangesAsCommitted() {
        changes.clear()
    }

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

    class Builder<out A: AggregateRoot<S, E>, S: State, E : StateChangeEvent>
    internal constructor (private val constructor: () -> A)
    {
        fun newInstance(): A {
            return constructor()
        }

        fun newInstanceWithCreateEvent(createEvent: E): A {
            return constructor().apply {
                applyChange(createEvent, true)
            }
        }

        fun fromEvents(events: Iterable<E>): A {
            return constructor().apply {
                events.forEach{ applyChange(it, false)}
            }
        }

        fun fromState(state: S): A {
            return constructor().apply {
                this.state = state
            }
        }
    }
}

object AggregateBuilder {
    fun <A: AggregateRoot<S, E>, S: State, E : StateChangeEvent>build(constructor: () -> A): AggregateRoot.Builder<A, S, E> {
        return AggregateRoot.Builder(constructor)
    }
}



