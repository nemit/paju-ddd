package io.paju.templateservice.infrastructure.repository

abstract class AbstractIdentifiedValueObject {
    abstract protected val id: Long
    fun visit(visitor: IdentifiedValueObjectVisitor) {
        visitor.setId(this.id)
    }
}

interface IdentifiedValueObjectVisitor {
    fun setId(id: Long)
}