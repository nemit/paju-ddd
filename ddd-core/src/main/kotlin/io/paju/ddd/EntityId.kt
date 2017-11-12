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
            return when (id) {
                is String -> EntityId(id)
                is UUID -> EntityId(id.toString())
                else -> throw IllegalArgumentException("The id should be of either String or UUID type")
            }
        }
    }
}