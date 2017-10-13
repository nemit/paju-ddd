package io.paju.templateservice.shared

abstract class IdentifiedValueObject {
    private var id: Long = -1

    fun valueObjectLocalId(): ValueObjectLocalId {
        return ValueObjectLocalId(this.id)
    }
}

data class ValueObjectLocalId(val id: Long)