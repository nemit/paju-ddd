package io.paju.ddd

import java.util.UUID

class CounterAggregate(id: UUID) :
    AggregateRoot<CounterState, CounterEvent>(id),
    StateExposed<CounterState>
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

    override fun state(): CounterState = state

    internal fun getEventMediator() = eventMediator

    override fun apply(event: CounterEvent): CounterState {
        return when (event) {
            is CounterEvent.Init -> CounterState(1, event.initialValue)
            is CounterEvent.Added -> state.copy( counter = state.counter + 1 )
            is CounterEvent.Subtracted -> state.copy( counter = state.counter - 1 )
        }
    }
}

data class CounterState(
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

fun makeAggregate(): CounterAggregate {
    val aggregate = AggregateRootBuilder
        .build { CounterAggregate(UUID.randomUUID()) }
        .newInstance( CounterEvent.Init(0) )
    aggregate.apply {
        add()
        add()
        subtract()
    }
    return aggregate
}