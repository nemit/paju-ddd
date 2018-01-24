package io.paju.ddd

import io.paju.ddd.exception.InvalidStateException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.UUID

internal class AggregateRootTest {

    @Test
    fun constructorInitializer() {
        val aggregate = AggregateRootBuilder
            .build { CounterAggregate(AggregateRootId(UUID.randomUUID()), initialValue = 10) }
            .newInstance( )
        assertEquals(10, aggregate.state().counter)
    }

    @Test
    fun builderInitializer() {
        val aggregate = AggregateRootBuilder
            .build { CounterAggregate(AggregateRootId(UUID.randomUUID())) }
            .newInstance( CounterEvent.Init(initialValue = 20) )
        assertEquals(20, aggregate.state().counter)
    }

    @Test
    fun shouldBeInitialized() {
        val aggregate = AggregateRootBuilder
            .build { CounterAggregate(AggregateRootId(UUID.randomUUID())) }
            .newInstance(CounterEvent.Init(initialValue = 0))
        assertTrue(aggregate.isInitialized())
    }

    @Test
    fun shouldNotBeInitialized() {
        val aggregate = AggregateRootBuilder
            .build { CounterAggregate(AggregateRootId(UUID.randomUUID())) }
            .newInstance( )
        assertFalse( aggregate.isInitialized() )
    }

    @Test
    fun uncommittedChanges() {
        val aggregate = makeAggregate()
        assertEquals(1, aggregate.state().counter)
        assertEquals(4, aggregate.getEventMediator().uncommittedChanges().size)
    }

    @Test
    fun markChangesAsCommitted() {
        val aggregate = makeAggregate()
        aggregate.getEventMediator().markChangesAsCommitted()
        assertEquals(1, aggregate.state().counter)
        assertEquals(0, aggregate.getEventMediator().uncommittedChanges().size)
    }

    @Test
    fun reconstruct() {
        val aggregate = makeAggregate()
        val reconstructed = AggregateRootBuilder
            .build { CounterAggregate(aggregate.id) }
            .fromEvents(aggregate.getEventMediator().uncommittedChanges())
        assertEquals(1, reconstructed.state().counter)
        assertEquals(0, reconstructed.getEventMediator().uncommittedChanges().size)
    }

    @Test
    fun expectUninitializedState() {
        val aggregate = AggregateRootBuilder
            .build { StateTesterAggregate(AggregateRootId(UUID.randomUUID())) }
            .newInstance()
        aggregate.runExpectUninitializedState()
        assertThrows(InvalidStateException::class.java) { aggregate.runExpectInitializedState() }
    }

    @Test
    fun expectInitializedState() {
        val aggregate = AggregateRootBuilder
            .build { StateTesterAggregate(AggregateRootId(UUID.randomUUID())) }
            .newInstance( StateTesterEvent.SetStateThis )
        aggregate.runExpectThisState()
        assertThrows(InvalidStateException::class.java) { aggregate.runExpectThatState() }
        assertThrows(InvalidStateException::class.java) { aggregate.runExpectUninitializedState() }
    }

    @Test
    fun expectStateLambda() {
        val aggregate = AggregateRootBuilder
            .build { StateTesterAggregate(AggregateRootId(UUID.randomUUID())) }
            .newInstance( StateTesterEvent.SetStateThis )
        assertThrows(InvalidStateException::class.java) { aggregate.runExpectLambdaThatState() }
    }

}
