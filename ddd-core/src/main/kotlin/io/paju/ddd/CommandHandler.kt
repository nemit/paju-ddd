package io.paju.ddd

interface CommandHandler<in C : Command> {
    fun handle(command: C)
}