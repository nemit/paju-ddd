package io.paju.templateservice.model.salesorder

import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate


interface SalesOrderDao {
    @SqlUpdate("INSERT INTO sales_order (id, customer_id, confirmed, deleted) " +
            "VALUES (:data.id, :data.customer_id, :data.confirmed, :data.deleted)")
    fun insert(data: SalesOrderDb)

    @SqlUpdate("INSERT INTO person (first_name, last_name, sex, date_of_birth) " +
            "VALUES (:data.first_name, :data.last_name, :data.sex, :data.date_of_birth)"  )
    @GetGeneratedKeys()
    fun insert(data: PersonDb): PersonDb

    @SqlUpdate("INSERT INTO reserved_service (id, name, description, price, price_currency, price_vat, service_id, " +
            "date_start, date_end) VALUES (:data.id, :data.name, :data.description, :data.price, :data.price_currency," +
            "data:price_vat, data: service_id" )
    fun insert(data: ReservedServiceDb)

    @SqlUpdate("INSERT INTO reserved_services_in_sales_order (reserved_service_id, sales_order_id, payment_status," +
            "payment_method, delivery_status) VALUES (:data.reserved_service_id, :data.sales_order_id, :data.payment_status," +
            ":data.payment_method, :data.delivery_status)")
    fun insert(data: ReservedServicesInSalesOrder)

    @SqlUpdate("INSERT INTO person_role_in_sales_order (sales_order_id, person_id, role) " +
            "VALUES (:data.sales_order_id, :data.person_id, :data.role)")
    fun insert(data: PersonRoleInSalesOrderDb)

    @SqlUpdate("INSERT INTO product (name, description," +
            "price, price_currency, price_vat) " +
            "VALUES (:data.name, :data.description, :data.price, :data.price_currency, :data.price_vat)")
    @GetGeneratedKeys
    fun insert(data: ProductDb): ProductDb

    @SqlUpdate("INSERT INTO products_in_sales_order (sales_order_id, product_id, payment_status, payment_method, delivery_status) " +
            "VALUES (:data.sales_order_id, :data.product_id, :data.payment_status, :data.payment_method, :data.delivery_status)")
    fun insert(data: ProductsInSalesOrderDb)

    @SqlQuery("SELECT * FROM sales_order WHERE id = :data.value")
    fun findSalesOrderById(data: SalesOrderId): SalesOrderDb

    @SqlQuery("SELECT p.id, p.first_name, p.last_name, p.sex, p.date_of_birth, r.role FROM person p, person_role_in_sales_order r WHERE r.person_id = p.id and r.sales_order_id = :id.value")
    fun findPersons(id: SalesOrderId): List<PersonRoleDb>

    @SqlQuery("SELECT p.id, p.name, p.description, p.price, p.price_vat, p.price_currency, m.delivery_status, " +
            "m.payment_method, m.payment_status FROM product p, products_in_sales_order m WHERE p.id = m.product_id AND m.sales_order_id = :id.value")
    fun findProducts(id: SalesOrderId): List<ProductAndDeliveryStatusDb>

    @SqlQuery("SELECT * FROM sales_order")
    fun findAllSalesOrders(): List<SalesOrderDb>

    // update
    @SqlUpdate("UPDATE sales_order SET customer_id = :data.customer_id, confirmed = :data.confirmed, deleted = :data.deleted WHERE id = :data.id")
    fun update(data: SalesOrderDb)

    @SqlUpdate("DELETE FROM person WHERE id = :data.id")
    fun delete(data: PersonDb)

    @SqlUpdate("DELETE FROM product WHERE id = :data.id")
    fun delete(data: ProductDb)

}