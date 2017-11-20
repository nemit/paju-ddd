package io.paju.ddd

import java.io.Serializable
import java.util.UUID

class AggregateRootId constructor(val id: String) : Serializable {

    override fun toString(): String {
        return id
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AggregateRootId

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    companion object {
        fun fromObject(id: Any): AggregateRootId {
            return when (id) {
                is String -> AggregateRootId(id)
                is UUID -> AggregateRootId(id.toString())
                else -> throw IllegalArgumentException("The id should be of either String or UUID type")
            }
        }
        val NotInitialized = AggregateRootId("0")
    }

}

