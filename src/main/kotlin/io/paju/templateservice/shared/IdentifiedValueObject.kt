package io.paju.templateservice.shared

/*
This class is used by Value Objects to hide surrogage ID required by manyTo mapping in the ER model
Kotlin internal visibility should be used to hide ID from users of SalesOrder
 */

abstract class IdentifiedValueObject {
    private var id: Long = -1

    internal fun valueObjectLocalId(): Long {
        return this.id
    }

    internal fun setValueObjectLocalId(id: Long) {
        this.id = id
    }

}

