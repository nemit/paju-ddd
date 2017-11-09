package io.paju.ddd

import java.io.Serializable
import java.util.UUID

object NotInitializedEntityId : EntityId("not initialized")

open class EntityId protected constructor(private val id: String) : Serializable {

    override fun toString(): String {
        return id
    }

    companion object {
        fun fromObject(id: Any): EntityId {
            if (id is String) {
                return EntityId(id)
            } else if (id is UUID) {
                return EntityId(id.toString())
            } else {
                throw IllegalArgumentException("The id should be of either String or UUID type")
            }
        }
    }
}