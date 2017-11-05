package io.paju.salesorder.infrastructure.dao

import io.paju.ddd.AggregateRootId
import io.paju.ddd.EntityId
import io.paju.salesorder.domain.state.SalesOrderState
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet
import java.sql.SQLException
import java.util.Optional

class SalesOrderDao(val jdbi: Jdbi) {

    private val insertSql =
        """
            INSERT INTO sales_order (id, customer_id, confirmed, deleted)
            VALUES (:id, :customer_id, :confirmed, :deleted)
        """.trimIndent()

    fun insert(data: SalesOrderState) {
        jdbi.useHandle<Exception> { handle ->
            handle
                .createUpdate(insertSql)
                .bindMap(buildBindMap(data))
                .execute()
        }
    }

    private val selectByIdSql =
        """
            SELECT id, customer_id, confirmed, deleted FROM sales_order
            where id = :id
        """.trimIndent()

    fun getById(id: AggregateRootId): Optional<SalesOrderState> {
        return jdbi.withHandle<Optional<SalesOrderState>, Exception> { handle ->
            handle
                .select(selectByIdSql, id.toString())
                .map(SalesOrderMapper)
                .findFirst()
        }
    }

    val updateSql =
        """
            INSERT INTO sales_order (id, customer_id, confirmed, deleted)
            VALUES (:id, :customer_id, :confirmed, :deleted)
        """.trimIndent()

    fun update(data: SalesOrderState) {
        jdbi.useHandle<Exception> { handle ->
            handle
                .createUpdate(updateSql)
                .bindMap(buildBindMap(data))
                .execute()
        }
    }

    private fun buildBindMap(data: SalesOrderState): Map<String, Any> {
        return mapOf(
            "id" to data.id.toString(),
            "customer_id" to data.customerId.toString(),
            "confirmed" to data.confirmed,
            "deleted" to data.deleted
        )
    }

    object SalesOrderMapper : RowMapper<SalesOrderState> {
        @Throws(SQLException::class)
        override fun map(r: ResultSet, ctx: StatementContext): SalesOrderState {
            return SalesOrderState(
                id = AggregateRootId.fromObject(r.getString("id")),
                customerId = EntityId.fromObject(r.getString("customer_id")),
                confirmed = r.getBoolean("confirmed"),
                deleted = r.getBoolean("deleted"),
                products = emptyList()
            )
        }
    }
}

