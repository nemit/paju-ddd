package io.paju.ddd

abstract class AggregateRoot<S : AggregateState, E : Event>
constructor(val id: AggregateRootId)
{
    // version of aggregate
    var version: Int = 0

    // all events will modify this
    abstract fun state(): S

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

    // aggregate state modification events
    protected fun applyChange(event: E) {
        applyChange(event, true)
    }

    protected fun applyChange(event: E, isNew: Boolean) {
        apply(event)

        if (isNew) {
            changes.add(event)
        }
    }
}