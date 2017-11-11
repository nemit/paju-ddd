package io.paju.salesorder.infrastructure.dao

import io.paju.ddd.AggregateRootId
import io.paju.ddd.EntityId
import io.paju.salesorder.domain.Currencies
import io.paju.salesorder.domain.DeliveryStatus
import io.paju.salesorder.domain.PaymentMethod
import io.paju.salesorder.domain.PaymentStatus
import io.paju.salesorder.domain.Price
import io.paju.salesorder.domain.Product
import io.paju.salesorder.domain.Vat
import io.paju.salesorder.domain.state.ProductState
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet
import java.sql.SQLException
import java.util.Optional

class ProductDao(private val jdbi: Jdbi) {

    private val insertSql =
        """
            INSERT INTO product (sales_order_id, id, name, description, price, price_currency, price_vat, payment_method, delivery_status)
            VALUES (:sales_order_id, :id, :name, :description, :price, :price_currency, :price_vat, :payment_method, :delivery_status)
        """.trimIndent()

    fun insert(salesOrderId: AggregateRootId, data: ProductState) {
        jdbi.useHandle<Exception> { handle ->
            handle
                .createUpdate(insertSql)
                .bindMap(buildBindMap(salesOrderId, data))
                .execute()
        }
    }

    private val selectByIdSql =
        """
            SELECT sales_order_id, id, name, description, price, price_currency, price_vat, payment_method, delivery_status
            where sales_order_id = :sales_order_id and id = :id
        """.trimIndent()

    fun getById(salesOrderId: AggregateRootId, id: EntityId): Optional<ProductState> {
        return jdbi.withHandle<Optional<ProductState>, Exception> { handle ->
            handle
                .select(selectByIdSql, salesOrderId.toString(), id.toString())
                .map(ProductStateMapper())
                .findFirst()
        }
    }

    private val selectBySalesOrderIdSql =
        """
            SELECT sales_order_id, id, name, description, price, price_currency, price_vat, payment_method, delivery_status
            where sales_order_id = :sales_order_id
        """.trimIndent()

    fun getBySalesOrderId(salesOrderId: AggregateRootId): List<ProductState> {
        return jdbi.withHandle<List<ProductState>, Exception> { handle ->
            handle
                .select(selectBySalesOrderIdSql, salesOrderId.toString())
                .map(ProductStateMapper())
                .list()
        }
    }

    private val updateSql =
        """
            UPDATE product SET name = :name, description = :description, price = :price, price_currency = :price_currency, price_vat = :price_vat, payment_method = :payment_method, delivery_status = :delivery_status
            WHERE sales_order_id = :sales_order_id and id = :id
        """.trimIndent()

    fun update(salesOrderId: AggregateRootId, data: ProductState) {
        jdbi.useHandle<Exception> { handle ->
            handle
                .createUpdate(updateSql)
                .bindMap(buildBindMap(salesOrderId, data))
                .execute()
        }
    }

    private val deleteSql =
        """
            DELETE FROM product
            WHERE sales_order_id = :sales_order_id and id = :id
        """.trimIndent()

    fun delete(salesOrderId: AggregateRootId, id: EntityId) {
        jdbi.useHandle<Exception> { handle ->
            handle
                .createUpdate(deleteSql)
                .bind("sales_order_id", salesOrderId.toString())
                .bind("id", id.toString())
                .execute()
        }
    }

    private fun buildBindMap(salesOrderId: AggregateRootId, data: ProductState): Map<String, Any> {
        return mapOf(
            "sales_order_id" to salesOrderId.toString(),
            "id" to data.product.id.toString(),
            "name" to data.product.name,
            "description" to data.product.description,
            "price" to data.product.price,
            "price_currency" to data.product.price.currency.name,
            "price_vat" to data.product.price.vat.name,
            "payment_status" to data.paymentStatus.name,
            "payment_method" to data.paymentMethod.name,
            "delivery_status" to data.deliveryStatus.name
        )
    }

}

class ProductStateMapper : RowMapper<ProductState> {
    @Throws(SQLException::class)
    override fun map(r: ResultSet, ctx: StatementContext): ProductState {
        return ProductState(
            Product(
                id = EntityId.fromObject(r.getString("id")),
                price = Price (
                    price = r.getBigDecimal("price"),
                    vat = Vat.valueOf(r.getString("price_vat")),
                    currency = Currencies.valueOf(r.getString("price_currency"))
                ),
                name = r.getString("name"),
                description = r.getString("description")
            ),
            paymentStatus = PaymentStatus.valueOf(r.getString("payment_status")),
            paymentMethod = PaymentMethod.valueOf(r.getString("payment_method")),
            deliveryStatus = DeliveryStatus.valueOf(r.getString("delivery_status"))
        )
    }
}