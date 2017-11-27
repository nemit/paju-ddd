package io.paju.ddd.exception

import io.paju.ddd.AggregateRootId

class InvalidStateException(id: AggregateRootId, version: Int, message: String) : ApplicationException(message, id, version)