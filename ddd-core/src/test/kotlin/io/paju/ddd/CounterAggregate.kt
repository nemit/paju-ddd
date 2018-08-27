package io.paju.ddd

import java.util.UUID

class CounterAggregate(id: UUID) :
    AggregateRoot<CounterState, CounterEvent>(id)
{
    constructor(id: UUID, initialValue: Int): this(id) {
        applyChange(CounterEvent.Init(initialValue))
    }

    // public api
    fun add() {
        applyChange(CounterEvent.Added)
    }

    fun subtract() {
        applyChange(CounterEvent.Subtracted)
    }

    internal fun getEventMediator() = eventMediator

    override fun apply(event: CounterEvent): CounterState {
        return when (event) {
            is CounterEvent.Init -> CounterState(id,1, event.initialValue)
            is CounterEvent.Added -> state.copy( counter = state.counter + 1 )
            is CounterEvent.Subtracted -> state.copy( counter = state.counter - 1 )
        }
    }

    companion object {
        fun new(initvalue: Int, id: UUID = UUID.randomUUID()): CounterAggregate =
            AggregateRootBuilder
                .build { CounterAggregate(it) }
                .newInstance( id, CounterEvent.Init(initvalue) )

        fun fromState(state: CounterState): CounterAggregate =
            AggregateRootBuilder
                .build { id -> CounterAggregate(id) }
                .fromState( state )
    }
}

data class CounterState(
    override val id: UUID,
    var version: Int = 1,
    var counter: Int = 0
) : State {
    override fun version(): Int = version
}

sealed class CounterEvent : StateChangeEvent() {
    data class Init(val initialValue: Int) : CounterEvent()
    object Added : CounterEvent()
    object Subtracted : CounterEvent()
}