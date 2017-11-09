package io.paju.ddd

import java.util.UUID

class CounterAggregate(id: AggregateRootId) :
    AggregateRoot<CounterState, CounterEvent>(id),
    AggregateStateExposed<CounterState>,
    AggregateEventReconstructable<CounterEvent>,
    AggregateStateReconstructable<CounterState>
{

    var state = CounterState()

    // public api
    fun add() {
        applyChange(CounterEvent.Added(id))
    }

    fun subtract() {
        applyChange(CounterEvent.Subtracted(id))
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
        this.state = state;
    }

    override fun reconstruct(events: Iterable<CounterEvent>) {
        events.forEach { applyChange(it, false) }
    }
}

data class CounterState(
    var counter: Int = 0
) : AggregateState

sealed class CounterEvent: Event() {
    data class Added(override val id: AggregateRootId) : CounterEvent()
    data class Subtracted(override val id: AggregateRootId) : CounterEvent()
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