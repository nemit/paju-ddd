package io.paju.ddd

abstract class AggregateRoot<out S : AggregateState, E : Event>
constructor(val id: AggregateRootId)
{
    // version of aggregate
    var version: Int = 0

    // all events will modify this
    abstract protected val state: S

    // all new uncommitted events
    private val changes = mutableListOf<E>()

    // now events will modify state is implemented here
    abstract protected fun apply(event: E)

    fun uncommittedChanges(): List<E> {
        return changes.toList()
    }

    fun markChangesAsCommitted() {
        changes.clear()
    }

    protected fun applyChange(event: E) {
        applyChange(event, true)
    }

    protected fun reconstruct(events: Iterable<E>) {
        events.forEach { applyChange(it, false) }
    }

    private fun applyChange(event: E, isNew: Boolean) {
        apply(event)

        if (isNew) {
            changes.add(event)
        }
    }
}