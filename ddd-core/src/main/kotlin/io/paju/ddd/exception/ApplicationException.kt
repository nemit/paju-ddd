package io.paju.ddd.exception

import io.paju.ddd.AggregateRootId

abstract class ApplicationException(message: String, val id: AggregateRootId, val version: Int) : RuntimeException(message)