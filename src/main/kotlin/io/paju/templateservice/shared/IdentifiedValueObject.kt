package io.paju.templateservice.shared

abstract class IdentifiedValueObject {
    private var id: Long = -1

    fun valueObjectLocalId(): ValueObjectLocalId {
        return ValueObjectLocalId(this.id)
    }

    fun setValueObjectLocalId(id: ValueObjectLocalId) {
        this.id = id.id
    }

}

data class ValueObjectLocalId(val id: Long)