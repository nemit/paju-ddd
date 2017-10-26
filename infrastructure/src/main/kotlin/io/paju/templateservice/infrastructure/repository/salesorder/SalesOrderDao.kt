package io.paju.templateservice.infrastructure.repository.salesorder

import io.paju.templateservice.domain.customer.CustomerId
import io.paju.templateservice.domain.salesorder.*
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import java.util.*


interface SalesOrderDao {
    @SqlUpdate("INSERT INTO sales_order (id, customer_id, confirmed, deleted) " +
            "VALUES (:id.value, :customer_id.value, :confirmed, :deleted)")
    fun insert(id: SalesOrderId, customer_id: CustomerId, confirmed: Boolean, deleted: Boolean)

    @SqlQuery("SELECT * FROM sales_order WHERE id = :id.value")
    fun findSalesOrderById(id: SalesOrderId): SalesOrderResult

    @SqlQuery("SELECT * FROM sales_order")
    fun findAllSalesOrders(): List<SalesOrderResult>

    // update
    @SqlUpdate("UPDATE sales_order SET customer_id = :customer_id.value, confirmed = :confirmed, deleted = :deleted WHERE id = :id.value")
    fun update(id: SalesOrderId, customer_id: CustomerId, confirmed: Boolean, deleted: Boolean)
}

data class SalesOrderResult(val id: UUID, val customer_id: UUID, val confirmed: Boolean, val deleted: Boolean)