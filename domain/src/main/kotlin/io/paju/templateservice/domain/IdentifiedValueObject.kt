package io.paju.templateservice.domain

/*
This class is used by Value Objects to hide surrogage ID required by manyTo mapping in the ER domain
Kotlin internal visibility should be used to hide ID from users of SalesOrder
 */

abstract class IdentifiedValueObject {
    private var id: Long = -1

    fun valueObjectLocalId(): Long {
        return this.id
    }

    fun setValueObjectLocalId(id: Long) {
        this.id = id
    }
}

