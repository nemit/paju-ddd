package io.paju.ddd

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class AggregateRootTest {

    @Test
    fun uncommittedChanges() {
        val aggregate = makeAggregate()
        assertEquals(1, aggregate.state().counter)
        assertEquals(3, aggregate.uncommittedChanges().size)
    }

    @Test
    fun markChangesAsCommitted() {
        val aggregate = makeAggregate()
        aggregate.markChangesAsCommitted()
        assertEquals(1, aggregate.state().counter)
        assertEquals(0, aggregate.uncommittedChanges().size)
    }

    @Test
    fun reconstruct() {
        val aggregate = makeAggregate()
        val reconstructed = AggregateRootBuilder
            .build{CounterAggregate(aggregate.id)}
            .fromEvents(aggregate.uncommittedChanges())
        assertEquals(1, reconstructed.state().counter)
        assertEquals(0, reconstructed.uncommittedChanges().size)
    }
}
