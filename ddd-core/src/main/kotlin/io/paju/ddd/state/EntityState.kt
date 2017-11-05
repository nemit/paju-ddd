package io.paju.ddd.state

import io.paju.ddd.EntityId

/**
 * Marker for entity state
 */
interface EntityState {
    val id: EntityId
}