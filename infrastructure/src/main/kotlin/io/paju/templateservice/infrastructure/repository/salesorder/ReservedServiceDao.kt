package io.paju.templateservice.infrastructure.repository.salesorder

import org.jdbi.v3.sqlobject.statement.SqlUpdate

interface ReservedServiceDao {
    @SqlUpdate("INSERT INTO reserved_service (id, name, description, price, price_currency, price_vat, service_id, " +
            "date_start, date_end) VALUES (:data.id, :data.name, :data.description, :data.price, :data.price_currency," +
            "data:price_vat, data: service_id" )
    fun insert(data: ReservedServiceDb)

    @SqlUpdate("INSERT INTO reserved_services_in_sales_order (reserved_service_id, sales_order_id, payment_status," +
            "payment_method, delivery_status) VALUES (:data.reserved_service_id, :data.sales_order_id, :data.payment_status," +
            ":data.payment_method, :data.delivery_status)")
    fun insert(data: ReservedServicesInSalesOrder)
}