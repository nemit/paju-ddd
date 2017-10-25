package io.paju.templateservice.shared

abstract class AbstractAggregate {
    val unitOfWork: UnitOfWork = UnitOfWork()
}