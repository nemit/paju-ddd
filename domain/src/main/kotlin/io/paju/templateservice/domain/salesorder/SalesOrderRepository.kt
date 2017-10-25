package io.paju.templateservice.domain.salesorder

interface SalesOrderRepository {
    fun add(salesOrder: SalesOrder)
    fun save(salesOrder: SalesOrder)
    fun findAll(): List<SalesOrder>
    fun salesOrderOfId(id: SalesOrderId): SalesOrder?
}