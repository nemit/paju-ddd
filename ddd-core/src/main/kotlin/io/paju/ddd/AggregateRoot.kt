package io.paju.ddd

import io.paju.ddd.state.AggregateRootState
import org.slf4j.LoggerFactory
import java.lang.reflect.InvocationTargetException

abstract class AggregateRoot constructor(val id: AggregateRootId) {
    private val changes = mutableListOf<Event>()
    var version: Int = 0

    fun uncommittedChanges(): List<Event> {
        return changes.toList()
    }

    fun markChangesAsCommitted() {
        changes.clear()
    }

    fun loadFromHistory(history: Iterable<Event>) {
        for (e in history) {
            if (version < e.version) {
                version = e.version
            }
            applyChange(e, false)
        }
    }

    protected fun <E: Event> applyChange(event: Event, isNew: Boolean = false, block: AggregateRoot.(event: Event) -> Unit) {
        applyChange(event, true)

        block(event)

        if (isNew) {
            changes.add(event)
        }
    }

    private fun applyChange(event: Event, isNew: Boolean) {
        invokeApplyIfEntitySupports(event)

        if (isNew) {
            changes.add(event)
        }
    }

    private fun invokeApplyIfEntitySupports(event: Event) {
        val eventType = nonAnonymous(event.javaClass)
        try {
            val method = this.javaClass.getDeclaredMethod(APPLY_METHOD_NAME, eventType)
            method.isAccessible = true
            method.invoke(this, event)
        } catch (ex: SecurityException) {
            throw RuntimeException(ex)
        } catch (ex: IllegalAccessException) {
            throw RuntimeException(ex)
        } catch (ex: InvocationTargetException) {
            throw RuntimeException(ex)
        } catch (ex: NoSuchMethodException) {
            logger.warn("Event {} not applicable to {}!", event, this)
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(AggregateRoot::class.java)

        private val APPLY_METHOD_NAME = "apply"

        @Suppress("UNCHECKED_CAST")
        private fun <T> nonAnonymous(clazz: Class<T>): Class<T> {
            return if (clazz.isAnonymousClass) clazz.superclass as Class<T> else clazz
        }
    }
}