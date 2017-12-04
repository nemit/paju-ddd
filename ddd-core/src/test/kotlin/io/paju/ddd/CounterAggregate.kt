package io.paju.ddd

import java.util.UUID

class CounterAggregate(id: AggregateRootId) :
    AggregateRoot<CounterState, CounterEvent>(id),
    StateExposed<CounterState>
{
    // public api
    fun add() {
        applyChange(CounterEvent.Added)
    }

    fun subtract() {
        applyChange(CounterEvent.Subtracted)
    }

    override fun state(): CounterState = getState()

    override fun initialState(): CounterState = CounterState()

    override fun apply(event: CounterEvent, toState: CounterState): CounterState {
        return when (event) {
            is CounterEvent.Added -> toState.copy( counter =  toState.counter + 1 )
            is CounterEvent.Subtracted -> toState.copy( counter =  toState.counter - 1 )
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
    object Added : CounterEvent()
    object Subtracted : CounterEvent()
}

fun makeAggregate(): CounterAggregate {
    val aggregate = AggregateRootBuilder
        .build { CounterAggregate(AggregateRootId.fromObject(UUID.randomUUID())) }
        .newInstance()
    aggregate.apply {
        add()
        add()
        subtract()
    }
    return aggregate
}