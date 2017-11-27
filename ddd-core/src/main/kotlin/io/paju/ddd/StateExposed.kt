package io.paju.ddd

interface StateExposed<out S: State> {
    fun state() : S
}