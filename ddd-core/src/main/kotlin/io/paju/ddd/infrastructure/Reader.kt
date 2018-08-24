package io.paju.ddd.infrastructure

import io.paju.ddd.ExposedState
import io.paju.ddd.State
import java.util.UUID

interface Reader<out S: State> {
    fun get(id: UUID): ExposedState<S>
}