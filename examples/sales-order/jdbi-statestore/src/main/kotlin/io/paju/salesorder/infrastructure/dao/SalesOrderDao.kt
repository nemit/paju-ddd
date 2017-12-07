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
import java.util.UUID

class SalesOrderDao(private val jdbi: Jdbi) {

    private val insertSql =
        """
            INSERT INTO sales_order (id, customer_id, confirmed, deleted)
            VALUES (:id, null, false, false)
        """.trimIndent()

    fun create(id: AggregateRootId) {
        jdbi.useHandle<Exception> { handle ->
            handle
                .createUpdate(insertSql)
                .bind("id", id.toUUID())
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
                .select(selectByIdSql)
                .bind("id", id.toUUID())
                .map(SalesOrderMapper)
                .findFirst()
        }
    }

    private val updateSql =
        """
            UPDATE sales_order SET customer_id = :customer_id, confirmed = :confirmed, deleted = :deleted
            where id = :id
        """.trimIndent()

    fun update(id: AggregateRootId, data: SalesOrderState) {
        jdbi.useHandle<Exception> { handle ->
            handle
                .createUpdate(updateSql)
                .bindMap(buildBindMap(id, data))
                .execute()
        }
    }

    private fun buildBindMap(id: AggregateRootId, data: SalesOrderState): Map<String, Any?> {
        return mapOf(
            "id" to id.toUUID(),
            "customer_id" to data.customerId?.toUUID(),
            "confirmed" to data.confirmed,
            "deleted" to data.deleted
        )
    }

    object SalesOrderMapper : RowMapper<SalesOrderState> {
        @Throws(SQLException::class)
        override fun map(r: ResultSet, ctx: StatementContext): SalesOrderState {
            return SalesOrderState(
                1, // TODO: implement version handling
                customerId =
                    if (r.getString("customer_id") != null) {
                      EntityId(r.getObject("customer_id") as UUID)
                    } else {
                        null
                    },
                confirmed = r.getBoolean("confirmed"),
                deleted = r.getBoolean("deleted"),
                products = emptyList()
            )
        }
    }
}

