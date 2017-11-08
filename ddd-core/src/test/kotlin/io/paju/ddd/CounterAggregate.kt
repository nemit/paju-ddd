package io.paju.ddd

import java.util.UUID

class CounterAggregate(id: AggregateRootId) :
    AggregateRoot<CounterState, CounterEvent>(id), AggregateStateExposer<CounterState>
{

    override val state = CounterState()

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

    companion object : AggregateEventConstructor<CounterState, CounterEvent, CounterAggregate> {
        override fun constructAggregate(id: AggregateRootId, events: Iterable<CounterEvent>): CounterAggregate {
            val aggregate = CounterAggregate(id)
            aggregate.reconstruct(events)
            return aggregate
        }
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