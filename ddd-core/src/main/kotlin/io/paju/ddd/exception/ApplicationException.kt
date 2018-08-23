package io.paju.ddd.exception

import java.util.UUID

abstract class ApplicationException(message: String, val id: UUID, val version: Int) : DddException(message)