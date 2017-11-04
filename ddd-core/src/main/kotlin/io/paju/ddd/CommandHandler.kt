package io.paju.ddd

interface CommandHandler {
    fun handle(command: Command)
}