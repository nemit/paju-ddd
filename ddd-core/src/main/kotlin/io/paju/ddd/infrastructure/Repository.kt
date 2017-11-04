package io.paju.ddd.infrastructure

import io.paju.ddd.AggregateRoot
import io.paju.ddd.AggregateRootId

interface Repository<T : AggregateRoot> {
    fun save(aggregate: T, version: Int)
    fun getById(id: AggregateRootId): T
}