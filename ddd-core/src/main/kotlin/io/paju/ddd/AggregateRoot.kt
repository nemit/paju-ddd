package io.paju.ddd

import io.paju.ddd.exception.DddException
import io.paju.ddd.exception.InvalidStateException
import java.util.UUID

internal enum class ConstructionType{
    NEW, EVENT_RECONSTRUCTED, STATE_RECONSTRUCTED
}

abstract class AggregateRoot<S: State, E: StateChangeEvent>(
    val manager: StateManager<S, E>
){
    constructor(id: UUID): this(StateManager(id))

    init {
        manager.register { event -> this@AggregateRoot.mutate(event) }
    }

    val state: S
        get() = manager.state
    val id: UUID
        get() = manager.id

    internal var constructionType: ConstructionType = ConstructionType.NEW
        private set

    protected val eventMediator: StateManager<S,E>.EventMediator = manager.EventMediator()

    /**
     * Apply state change event.
     *
     * NOTE: Aggregate is initialized after first apply. Beware that when instance initialization
     * calls apply(initialEvent) for the first time the `state` is null.
     */
    protected abstract fun mutate(event: E): S

    protected fun applyChange(event: E) = manager.applyChange(event)

    abstract class Mutator<S: State, E: StateChangeEvent>(private val manager: StateManager<S, E>){
        init {
            manager.register { event -> mutate(event) }
        }

        val state
            get() = manager.state

        protected fun applyChange(event: E) = manager.applyChange(event)

        protected abstract fun mutate(event: E): S
    }


    class StateManager<S: State, E: StateChangeEvent>(
        val id: UUID
    ) {
        private val mutators: MutableList<(E) -> S> = mutableListOf()

        /**
         * Aggregate state. State is initialized after first apply.
         */
        lateinit var state: S
            //private set
        var version: Int = 0
            private set

        private val changes: MutableList<E> = mutableListOf()// all new uncommitted events

        fun isInitialized(): Boolean = this::state.isInitialized

        fun register(func: (E) -> S) {
            mutators.add(func)
        }

        internal fun rebuildState(events: Iterable<E>) {
            // apply mutators
            events.forEach{event ->
                mutators.forEach { mutator ->
                    state = mutator.invoke(event)
                    checkId(state)
                }
            }
        }

        // aggregate state modification events
        internal fun applyChange(event: E) {
            // apply mutators
            mutators.forEach {
                state = it.invoke(event)
                checkId(state)
            }
            // add event to changes
            changes.add(event)
        }

        inner class EventMediator {
            fun markChangesAsCommitted() {
                changes.clear()
            }
            fun uncommittedChanges() = changes
        }
    }

    fun isInitialized(): Boolean = manager.isInitialized()

    protected fun expectInitializedState() {
        if(!manager.isInitialized()) {
            throw InvalidStateException("Instance not in expected state. " +
                "Instance state was uninitialized, expected initialized", this)
        }
    }

    protected fun expectUninitializedState() {
        if(manager.isInitialized()) {
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
    internal constructor (private val constructor: (UUID) -> A)
    {
        fun newInstance(id: UUID): A =
            constructor(id).apply {
                constructionType = ConstructionType.NEW
            }

        fun newInstance(id: UUID, initialEvent: E): A =
            constructor(id).apply {
                constructionType = ConstructionType.NEW
                manager.applyChange(initialEvent)
            }

        fun fromEvents(id: UUID, events: Iterable<E>): A =
            constructor(id).apply {
                constructionType = ConstructionType.EVENT_RECONSTRUCTED
                manager.rebuildState(events)
            }

        fun fromState(state: S): A =
            constructor(state.id).apply {
                constructionType = ConstructionType.STATE_RECONSTRUCTED
                checkId(state)
                manager.state = state
            }

        fun fromState(id: UUID, stateFun: (UUID) ->  S): A =
            constructor(id).apply {
                constructionType = ConstructionType.STATE_RECONSTRUCTED
                checkId(state)
                manager.state = stateFun(id)
            }
    }
}

fun <C: Command>AggregateRoot<*,*>.checkId(command: C) {
    if(this.id != command.id){
        throw DddException("Invalid command id: ${this.id} != $command.id")
    }
}

fun <S: State>AggregateRoot<*,*>.checkId(state: S) {
    if(this.id != state.id){
        throw InvalidStateException("Aggregate id and state id must equal")
    }
}

fun <S: State>AggregateRoot.StateManager<*,*>.checkId(state: S) {
    if(this.id != state.id){
        throw InvalidStateException("Aggregate id and state id must equal")
    }
}