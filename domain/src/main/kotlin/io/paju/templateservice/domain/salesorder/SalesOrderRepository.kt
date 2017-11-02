package io.paju.templateservice.domain.salesorder

interface SalesOrderRepository {
    fun save(salesOrder: SalesOrder)
    fun findAll(): List<SalesOrder>
    fun salesOrderOfId(id: SalesOrderId): SalesOrder?
}