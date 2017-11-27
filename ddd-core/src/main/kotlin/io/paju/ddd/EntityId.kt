package io.paju.ddd

import java.io.Serializable
import java.util.UUID

class EntityId constructor(val id: String) : Serializable {

    override fun toString(): String {
        return id
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EntityId

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    companion object {
        fun fromObject(id: Any): EntityId {
            return when (id) {
                is String -> EntityId(id)
                is UUID -> EntityId(id.toString())
                else -> throw IllegalArgumentException("The id should be of either String or UUID type")
            }
        }

        val NotInitialized = EntityId.fromObject("0")
    }
}