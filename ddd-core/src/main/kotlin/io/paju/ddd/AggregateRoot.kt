package io.paju.ddd

import io.paju.ddd.exception.DddException
import io.paju.ddd.exception.InvalidStateException
import java.util.UUID

internal enum class ConstructionType{
    NEW, EVENT_RECONSTRUCTED, STATE_RECONSTRUCTED
}

abstract class AggregateRoot<S: State, E: StateChangeEvent>
(val id: UUID)
{
    /**
     * Aggregate state. State is initialized after first apply.
     */
    lateinit var state: S
        private set

    private val changes: MutableList<E> = mutableListOf()// all new uncommitted events
    protected val eventMediator: EventMediator = EventMediator()
    var version: Int = 0
        private set
    internal  var constructionType: ConstructionType = ConstructionType.NEW
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
        checkId(state)
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

    protected fun expectInitializedState() {
        if(!isInitialized()) {
            throw InvalidStateException("Instance not in expected state. " +
                "Instance state was uninitialized, expected initialized", this)
        }
    }

    protected fun expectUninitializedState() {
        if(isInitialized()) {
            throw InvalidStateException("Instance not in expected state. " +
                "Instance state was initialized, expected uninitialized", this)
        }
    }

    protected inline fun <reified T: State>expectState(): T {
        val instance = state
        if(instance is T){
            return instance
        }else{
            throw InvalidStateException("Instance not in expected state. " +
                "Instance state was ${instance::class}, expected ${T::class}", this)
        }
    }

    protected inline fun <reified T: State>expectState(check: (T) -> Boolean): T {
        val instance = state
        if(instance is T && check(instance)){
            return instance
        }else{
            throw InvalidStateException("Instance not in expected state. Instance state was invalid by check")
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
                checkId(state)
                this.state = state
            }

    }

}

fun <C: Command>AggregateRoot<*,*>.checkId(command: C) {
    if(this.id != command.id){
        throw DddException("Invalid command id: ${this.id} != $id")
    }
}

fun <S: State>AggregateRoot<*,*>.checkId(state: S) {
    if(this.id != state.id){
        throw InvalidStateException("Aggregate id and state id must equal")
    }
}