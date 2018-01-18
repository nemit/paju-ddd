package io.paju.ddd

import io.paju.ddd.exception.DddRuntimeException

enum class ConstructionType{
    NEW, EVENT_RECONSTRUCTED, STATE_RECONSTRUCTED
}

abstract class AggregateRoot<S: State, E: StateChangeEvent>
(val id: AggregateRootId)
{
    /**
     * Aggregate state. State is initialized after first apply.
     */
    protected lateinit var state: S
        private set
    private val changes: MutableList<E> = mutableListOf()// all new uncommitted events
    protected val eventMediator: EventMediator = EventMediator()
    var version: Int = 0
        private set
    protected var constructionType: ConstructionType = ConstructionType.NEW
        private set
    fun isInitialized(): Boolean = this::state.isInitialized

    /**
     * Apply state change event.
     *
     * NOTE: Aggregate is initialized after first apply. Beware that when instance initialization
     * calls apply(initialEvent) for the first time the `state` is null.
     */
    protected abstract fun apply(event: E): S

    // aggregate state modification events
    protected fun applyChange(event: E) {
        state = apply(event)
        changes.add(event)
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
                constructionType = ConstructionType.NEW
            }

        fun newInstance(initialEvent: E): A =
            aggregate.apply {
                constructionType = ConstructionType.NEW
                applyChange(initialEvent)
            }

        fun fromEvents(events: Iterable<E>): A =
            aggregate.apply {
                constructionType = ConstructionType.EVENT_RECONSTRUCTED
                events.forEach {
                    state = apply(it)
                }
            }

        fun fromState(state: S): A =
            aggregate.apply {
                constructionType = ConstructionType.STATE_RECONSTRUCTED
                this.state = state
            }

    }
}

fun <C: Command>AggregateRoot<*,*>.checkId(command: C) {
    if(this.id != command.id){
        throw DddRuntimeException("Invalid command id: ${this.id} != $id")
    }
}