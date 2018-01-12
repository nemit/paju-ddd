package io.paju.ddd

import io.paju.ddd.exception.DddRuntimeException

fun <C: Command>AggregateRoot<*,*>.checkId(command: C) {
    if(this.id != command.id){
        throw DddRuntimeException("Invalid command id: ${this.id} != $id")
    }
}

inline fun <reified T: State>expectState(instance: State): T {
    if(instance is T){
        return instance
    }else{
        throw DddRuntimeException("Invalid instance type")
    }
}