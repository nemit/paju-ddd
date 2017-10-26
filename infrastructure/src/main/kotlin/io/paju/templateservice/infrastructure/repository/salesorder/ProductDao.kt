package io.paju.templateservice.infrastructure.repository.salesorder

import io.paju.templateservice.domain.salesorder.SalesOrderId
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate

interface ProductDao {
    @SqlQuery("SELECT p.id, p.name, p.description, p.price, p.price_vat, p.price_currency, m.delivery_status, " +
            "m.payment_method, m.payment_status FROM product p, products_in_sales_order m WHERE p.id = m.product_id AND m.sales_order_id = :id.value")
    fun findProducts(id: SalesOrderId): List<ProductAndDeliveryStatusDb>

    @SqlUpdate("INSERT INTO product (name, description," +
            "price, price_currency, price_vat) " +
            "VALUES (:data.name, :data.description, :data.price, :data.price_currency, :data.price_vat)")
    @GetGeneratedKeys
    fun insert(data: ProductDb): ProductDb

    @SqlUpdate("INSERT INTO products_in_sales_order (sales_order_id, product_id, payment_status, payment_method, delivery_status) " +
            "VALUES (:data.sales_order_id, :data.product_id, :data.payment_status, :data.payment_method, :data.delivery_status)")
    fun insert(data: ProductsInSalesOrderDb)


    @SqlUpdate("DELETE FROM product WHERE id = :data.id")
    fun delete(data: ProductDb)
}