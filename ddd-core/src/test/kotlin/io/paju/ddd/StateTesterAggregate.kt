package io.paju.ddd

class StateTesterAggregate(id: AggregateRootId) :
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
    fun runExpectThisState() = expectState<StateTesterState.StateThis>()
    fun runExpectThatState() = expectState<StateTesterState.StateThat>()
}

sealed class StateTesterState : State {
    object StateThis: StateTesterState(){ override fun version(): Int = 1 }
    object StateThat: StateTesterState(){ override fun version(): Int = 1 }
}

sealed class StateTesterEvent : StateChangeEvent() {
    object SetStateThis : StateTesterEvent()
    object SetStateThat : StateTesterEvent()
}