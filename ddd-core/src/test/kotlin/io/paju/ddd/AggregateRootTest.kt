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
        val aggregate = CounterAggregate.new(10)
        assertEquals(10, aggregate.state.counter)
    }

    @Test
    fun builderInitializer() {
        val aggregate = CounterAggregate.new(20)
        assertEquals(20, aggregate.state.counter)
    }

    @Test
    fun shouldBeInitialized() {
        val aggregate = CounterAggregate.new(0)
        assertTrue(aggregate.isInitialized())
    }

    @Test
    fun shouldNotBeInitialized() {
        val aggregate = AggregateRootBuilder
            .build { CounterAggregate(it) }
            .newInstance( UUID.randomUUID() ) // leaves state to initialized
        assertFalse( aggregate.isInitialized() )
    }

    @Test
    fun mutatorChangeState() {
        val aggregate = CalculatorAggregate.new().apply {
            this.plus(4)
            this.minus(1)
        }
        assertTrue( aggregate.isInitialized() )
        assertEquals( 2, aggregate.state.operations )
        assertEquals( 3, aggregate.state.total )
        assertEquals( 1 + 2 + 2, aggregate.getEventMediator().uncommittedChanges().size )
    }

    @Test
    fun uncommittedChanges() {
        val aggregate = CounterAggregate.new(0)
            .apply {
                add()
                add()
                subtract()
            }
        assertEquals(1, aggregate.state.counter)
        assertEquals(4, aggregate.getEventMediator().uncommittedChanges().size)
    }

    @Test
    fun markChangesAsCommitted() {
        val aggregate = CounterAggregate.new(0)
            .apply {
                add()
                add()
                subtract()
            }
        aggregate.getEventMediator().markChangesAsCommitted()
        assertEquals(1, aggregate.state.counter)
        assertEquals(0, aggregate.getEventMediator().uncommittedChanges().size)
    }

    @Test
    fun reconstruct() {
        val aggregate = CounterAggregate.new(0)
            .apply {
                add()
                add()
                subtract()
            }
        val reconstructed = AggregateRootBuilder
            .build { CounterAggregate(it) }
            .fromEvents(aggregate.id, aggregate.getEventMediator().uncommittedChanges())
        assertEquals(1, reconstructed.state.counter)
        assertEquals(0, reconstructed.getEventMediator().uncommittedChanges().size)
    }

    @Test(expected = InvalidStateException::class)
    fun expectUninitializedState() {
        val aggregate = AggregateRootBuilder
            .build { StateTesterAggregate(it) }
            .newInstance(UUID.randomUUID())
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
            .build { FailingTesterAggregate(it) }
            .newInstance( UUID.randomUUID(), StateTesterEvent.SetStateThis )
        aggregate.runApplyChange ( StateTesterEvent.SetStateThis ) // ok
        assertEquals(StateTesterState.StateThis(aggregate.id), aggregate.state)
        aggregate.runApplyChange ( StateTesterEvent.SetStateThat ) // should fail
    }

    @Test(expected = InvalidStateException::class)
    fun expectInitializedState() {
        val aggregate = AggregateRootBuilder
            .build { StateTesterAggregate(it) }
            .newInstance( UUID.randomUUID(), StateTesterEvent.SetStateThis )
        aggregate.runExpectStateThis()
        aggregate.runExpectStateThat()
    }

    @Test(expected = InvalidStateException::class)
    fun expectStateWhenStateIdUninitializedState() {
        val aggregate = AggregateRootBuilder
            .build { StateTesterAggregate(it) }
            .newInstance( UUID.randomUUID() )
        aggregate.runExpectStateThat()
    }


    @Test(expected = InvalidStateException::class)
    fun expectStateLambda() {
        val aggregate = AggregateRootBuilder
            .build { StateTesterAggregate(it) }
            .newInstance( UUID.randomUUID(), StateTesterEvent.SetStateThis )
        aggregate.runExpectLambdaThatState()
    }

}
