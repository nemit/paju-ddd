package io.paju.ddd.state

import io.paju.ddd.AggregateRootId

interface AggregateRootState {
    val id: AggregateRootId
}