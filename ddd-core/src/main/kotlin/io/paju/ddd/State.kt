package io.paju.ddd

import io.paju.ddd.exception.DddRuntimeException

interface State {
    fun version(): Int
}

inline fun <reified T: State>expectState(instance: State): T {
    if(instance is T){
        return instance
    }else{
        throw DddRuntimeException("Invalid instance type")
    }
}

fun AggregateRoot<*, *>.expectInitializedState() {
    if(!isInitialized()) {
        throw DddRuntimeException("Invalid instance type")
    }
}

fun AggregateRoot<*, *>.expectUninitializedState() {
    if(isInitialized()) {
        throw DddRuntimeException("Invalid instance type")
    }
}