package io.paju.templateservice.infrastructure.repository

import io.paju.templateservice.domain.salesorder.SalesOrderRepository

object RepositoryFactory {
    fun salesOrderRepository(): SalesOrderRepository {
        return SalesOrderJdbiRepository()
    }
}