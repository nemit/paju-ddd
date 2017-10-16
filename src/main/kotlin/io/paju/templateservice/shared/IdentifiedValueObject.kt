package io.paju.templateservice.shared

abstract class IdentifiedValueObject {
    private var id: Long = -1

    internal fun valueObjectLocalId(): Long {
        return this.id
    }

    internal fun setValueObjectLocalId(id: Long) {
        this.id = id
    }

}

