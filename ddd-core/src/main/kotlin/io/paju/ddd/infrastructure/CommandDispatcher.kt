package io.paju.ddd.infrastructure

import io.paju.ddd.Command

interface CommandDispatcher {
    fun <T : Command> dispatch(command: T)
}