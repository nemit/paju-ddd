package io.paju.templateservice.infrastructure.repository

import io.paju.templateservice.domain.salesorder.SalesOrderRepository
import io.paju.templateservice.infrastructure.repository.salesorder.SalesOrderJdbiRepository

object RepositoryFactory {
    fun salesOrderRepository(): SalesOrderRepository {
        return SalesOrderJdbiRepository()
    }
}