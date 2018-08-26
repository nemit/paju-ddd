package io.paju.ddd

import java.util.UUID

open class StateTesterAggregate(id: UUID) :
    AggregateRoot<StateTesterState, StateTesterEvent>(id)
{
    override fun apply(event: StateTesterEvent): StateTesterState {
        return when (event) {
            is StateTesterEvent.SetStateThis -> StateTesterState.StateThis(id)
            is StateTesterEvent.SetStateThat -> StateTesterState.StateThat(id)
        }
    }

    fun runExpectInitializedState() = expectInitializedState()
    fun runExpectUninitializedState() = expectUninitializedState()
    fun runExpectStateThis() = expectState<StateTesterState.StateThis>()
    fun runExpectStateThat() = expectState<StateTesterState.StateThat>()
    fun runExpectLambdaThatState() = expectState<StateTesterState.StateThat>{
        false // this fails always
    }
}

class FailingTesterAggregate(id: UUID) : StateTesterAggregate(id)
{
    fun runApplyChange(event: StateTesterEvent){
        applyChange(event)
    }

    override fun apply(event: StateTesterEvent): StateTesterState {
        return when (event) {
            is StateTesterEvent.SetStateThis -> StateTesterState.StateThis(id) // correct
            is StateTesterEvent.SetStateThat -> StateTesterState.StateThat(UUID.randomUUID()) // failing
        }
    }
}

sealed class StateTesterState : State {
    data class StateThis(override val id: UUID): StateTesterState(){ override fun version(): Int = 1 }
    data class StateThat(override val id: UUID): StateTesterState(){ override fun version(): Int = 1 }
}

sealed class StateTesterEvent : StateChangeEvent() {
    object SetStateThis : StateTesterEvent()
    object SetStateThat : StateTesterEvent()
}