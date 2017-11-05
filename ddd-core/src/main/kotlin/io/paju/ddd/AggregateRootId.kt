package io.paju.ddd

import java.io.Serializable
import java.util.UUID

open class AggregateRootId constructor(val id: String) : Serializable {

    override fun toString(): String {
        return id
    }

    companion object {

        fun fromObject(id: Any): AggregateRootId {
            if (id is String) {
                return AggregateRootId(id)
            } else if (id is UUID) {
                return AggregateRootId(id.toString())
            } else {
                throw IllegalArgumentException("The id should be of either String or UUID type")
            }
        }

    }
}

object NotInitializedAggregateRootId : AggregateRootId("0")

