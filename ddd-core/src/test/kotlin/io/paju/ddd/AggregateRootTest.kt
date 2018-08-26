package io.paju.ddd

import io.paju.ddd.exception.InvalidStateException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.UUID

class AggregateRootTest {

    @Test
    fun constructorInitializer() {
        val aggregate = AggregateRootBuilder
            .build { CounterAggregate(UUID.randomUUID(), initialValue = 10) }
            .newInstance( )
        assertEquals(10, aggregate.state.counter)
    }

    @Test
    fun builderInitializer() {
        val aggregate = AggregateRootBuilder
            .build { CounterAggregate(UUID.randomUUID()) }
            .newInstance( CounterEvent.Init(initialValue = 20) )
        assertEquals(20, aggregate.state.counter)
    }

    @Test
    fun shouldBeInitialized() {
        val aggregate = AggregateRootBuilder
            .build { CounterAggregate(UUID.randomUUID()) }
            .newInstance(CounterEvent.Init(initialValue = 0))
        assertTrue(aggregate.isInitialized())
    }

    @Test
    fun shouldNotBeInitialized() {
        val aggregate = AggregateRootBuilder
            .build { CounterAggregate(UUID.randomUUID()) }
            .newInstance( )
        assertFalse( aggregate.isInitialized() )
    }

    @Test
    fun uncommittedChanges() {
        val aggregate = makeAggregate()
        assertEquals(1, aggregate.state.counter)
        assertEquals(4, aggregate.getEventMediator().uncommittedChanges().size)
    }

    @Test
    fun markChangesAsCommitted() {
        val aggregate = makeAggregate()
        aggregate.getEventMediator().markChangesAsCommitted()
        assertEquals(1, aggregate.state.counter)
        assertEquals(0, aggregate.getEventMediator().uncommittedChanges().size)
    }

    @Test
    fun reconstruct() {
        val aggregate = makeAggregate()
        val reconstructed = AggregateRootBuilder
            .build { CounterAggregate(aggregate.id) }
            .fromEvents(aggregate.getEventMediator().uncommittedChanges())
        assertEquals(1, reconstructed.state.counter)
        assertEquals(0, reconstructed.getEventMediator().uncommittedChanges().size)
    }

    @Test(expected = InvalidStateException::class)
    fun expectUninitializedState() {
        val aggregate = AggregateRootBuilder
            .build { StateTesterAggregate(UUID.randomUUID()) }
            .newInstance()
        aggregate.runExpectUninitializedState()
        aggregate.runExpectInitializedState()
    }

    @Test(expected = InvalidStateException::class)
    fun expectStateIdInBuilder() {
        AggregateRootBuilder
            .build { StateTesterAggregate(UUID.randomUUID()) }
            .fromState( StateTesterState.StateThat(UUID.randomUUID()) )
    }

    @Test(expected = InvalidStateException::class)
    fun failWithInvalidStateId() {
        val aggregate = AggregateRootBuilder
            .build { FailingTesterAggregate(UUID.randomUUID()) }
            .newInstance( StateTesterEvent.SetStateThis )
        aggregate.runApplyChange ( StateTesterEvent.SetStateThis ) // ok
        assertEquals(StateTesterState.StateThis(aggregate.id), aggregate.state)
        aggregate.runApplyChange ( StateTesterEvent.SetStateThat ) // should fail
    }

    @Test(expected = InvalidStateException::class)
    fun expectInitializedState() {
        val aggregate = AggregateRootBuilder
            .build { StateTesterAggregate(UUID.randomUUID()) }
            .newInstance( StateTesterEvent.SetStateThis )
        aggregate.runExpectStateThis()
        aggregate.runExpectStateThat()
    }

    @Test(expected = InvalidStateException::class)
    fun expectStateLambda() {
        val aggregate = AggregateRootBuilder
            .build { StateTesterAggregate(UUID.randomUUID()) }
            .newInstance( StateTesterEvent.SetStateThis )
        aggregate.runExpectLambdaThatState()
    }

}
