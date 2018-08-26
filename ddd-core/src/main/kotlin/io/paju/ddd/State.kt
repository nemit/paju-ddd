package io.paju.ddd

import io.paju.ddd.exception.InvalidStateException
import java.util.UUID

interface State {
    val id: UUID
    fun version(): Int
}

inline fun <reified T: State>expectState(instance: State): T {
    if(instance is T){
        return instance
    }else{
        throw InvalidStateException("Instance not in expected state. " +
            "Instance state was ${instance::class}, expected ${T::class}")
    }
}
