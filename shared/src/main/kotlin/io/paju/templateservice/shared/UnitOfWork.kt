package io.paju.templateservice.shared

class UnitOfWork {
    private val dirtyObjects = mutableListOf<Any>()
    private val newObjects = mutableListOf<Any>()
    private val removedObjects = mutableListOf<Any>()

    // TODO add logic which objects are accepted as new, dirty and removed
    // value objects should never be dirty
    // remove should ignore value objects with id -1

    fun registerNew(obj: Any) {
        newObjects.add(obj)
    }

    fun registerRemoved(obj: Any) {
        removedObjects.add(obj)
    }

    fun registerDirty(obj: Any) {
        dirtyObjects.add(obj)
    }

    fun newObjects(): List<Any> {
        return newObjects
    }

    fun dirtyObjects(): List<Any> {
        return dirtyObjects
    }

    fun removedObjects(): List<Any> {
        return removedObjects
    }
}