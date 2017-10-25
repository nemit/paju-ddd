package io.paju.templateservice.shared

abstract class AbstractAggregate {
    val repositoryMediator: RepositoryMediator = RepositoryMediator()
}