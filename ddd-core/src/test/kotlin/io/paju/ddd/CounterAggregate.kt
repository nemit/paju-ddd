package io.paju.ddd

import java.util.UUID

class CounterAggregate(id: AggregateRootId) :
    AggregateRoot<CounterState, CounterEvent>(id),
    StateExposed<CounterState>,
    EventReconstructable<CounterEvent>,
    StateReconstructable<CounterState>
{

    var state = CounterState()

    // public api
    fun add() {
        applyChange(CounterEvent.Added)
    }

    fun subtract() {
        applyChange(CounterEvent.Subtracted)
    }

    override fun state(): CounterState {
        return state
    }

    override fun apply(event: CounterEvent) {
        when (event) {
            is CounterEvent.Added -> state.counter++
            is CounterEvent.Subtracted -> state.counter--
        }.let {} // let is required for exhaustive when
    }

    override fun reconstruct(state: CounterState) {
        this.state = state
    }

    override fun reconstruct(events: Iterable<CounterEvent>) {
        events.forEach { applyChange(it, false) }
    }
}

data class CounterState(
    var counter: Int = 0
) : State

sealed class CounterEvent: StateChangeEvent() {
    object Added : CounterEvent()
    object Subtracted : CounterEvent()
}

fun makeAggregate(): CounterAggregate {
    val aggregate = CounterAggregate(AggregateRootId.fromObject(UUID.randomUUID()))
    aggregate.apply {
        add()
        add()
        subtract()
    }
    return aggregate
}