package io.paju.ddd

import java.util.UUID

class StateTesterAggregate(id: UUID) :
    AggregateRoot<StateTesterState, StateTesterEvent>(id)
{
    override fun apply(event: StateTesterEvent): StateTesterState {
        return when (event) {
            is StateTesterEvent.SetStateThis -> StateTesterState.StateThis
            is StateTesterEvent.SetStateThat -> StateTesterState.StateThat
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

sealed class StateTesterState : State {
    object StateThis: StateTesterState(){ override fun version(): Int = 1 }
    object StateThat: StateTesterState(){ override fun version(): Int = 1 }
}

sealed class StateTesterEvent : StateChangeEvent() {
    object SetStateThis : StateTesterEvent()
    object SetStateThat : StateTesterEvent()
}