package io.paju.ddd

import java.util.UUID

data class CalculatorState(
    override val id: UUID,
    val version: Int = 1,
    val total: Int,
    val operations: Int
) : State {
    override fun version(): Int = version
}

sealed class CalculatorEvent : StateChangeEvent() {
    data class Init(val initialValue: Int) : CalculatorEvent()
    data class Added(val amount: Int) : CalculatorEvent()
    data class Subtracted(val amount: Int) : CalculatorEvent()
    object OperationCounted: CalculatorEvent()
}

class CalculatorAggregate(
    manager: AggregateRoot.StateManager<CalculatorState, CalculatorEvent>
) :
    AggregateRoot<CalculatorState, CalculatorEvent>(manager),
    Addition by AdditionCalculator(manager),
    Subtraction by SubtractionCalculator(manager)
{
    internal fun getEventMediator() = eventMediator

    override fun mutate(event: CalculatorEvent): CalculatorState {
        return when (event) {
            is CalculatorEvent.Init -> CalculatorState(id,1, event.initialValue, 0)
            is CalculatorEvent.OperationCounted -> state.copy(operations = state.operations + 1)
            else -> state
        }
    }

    companion object {
        fun new(id: UUID = UUID.randomUUID()): CalculatorAggregate =
            AggregateRootBuilder
                .build { CalculatorAggregate(StateManager(it)) }
                .newInstance( id, CalculatorEvent.Init(0) )

    }

}

interface Addition {
    fun plus(amount: Int)
}

class AdditionCalculator(manager: AggregateRoot.StateManager<CalculatorState, CalculatorEvent>)
    : Addition, AggregateRoot.Mutator<CalculatorState, CalculatorEvent>(manager){
    override fun plus(amount: Int) {
        applyChange(CalculatorEvent.Added(amount))
        applyChange(CalculatorEvent.OperationCounted)
    }
    override fun mutate(event: CalculatorEvent): CalculatorState {
        return when (event) {
            is CalculatorEvent.Added -> state.copy(total = state.total.plus(event.amount))
            else -> state
        }
    }
}

interface Subtraction {
    fun minus(amount: Int)
}

class SubtractionCalculator(manager: AggregateRoot.StateManager<CalculatorState, CalculatorEvent>)
    : Subtraction, AggregateRoot.Mutator<CalculatorState, CalculatorEvent>(manager)
{

    override fun minus(amount: Int) {
        applyChange(CalculatorEvent.Subtracted(amount))
        applyChange(CalculatorEvent.OperationCounted)
    }
    override fun mutate(event: CalculatorEvent): CalculatorState {
        return when (event) {
            is CalculatorEvent.Subtracted -> state.copy(total = state.total.minus(event.amount))
            else -> state
        }
    }
}