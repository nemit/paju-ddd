package io.paju.templateservice.shared

open class AbstractRepository {
    protected fun unitOfWork(aggregate: AbstractAggregate): RepositoryMediator {
        return aggregate.repositoryMediator
    }
}