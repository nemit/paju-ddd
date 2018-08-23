package io.paju.ddd

import java.util.UUID

interface Command {
    val id: UUID
    val originalVersion: Int
}