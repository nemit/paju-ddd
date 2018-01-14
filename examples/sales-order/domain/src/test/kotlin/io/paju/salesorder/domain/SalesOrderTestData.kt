package io.paju.salesorder.domain

import io.paju.ddd.AggregateRootBuilder
import io.paju.ddd.AggregateRootId
import io.paju.ddd.EntityId
import io.paju.salesorder.domain.event.SalesOrderEvent
import java.math.BigDecimal
import java.util.UUID

object SalesOrderTestData {
    fun makeSalesOrder(vararg products: Product) =
        AggregateRootBuilder
            .build { SalesOrder(AggregateRootId(UUID.randomUUID())) }
            .newInstance( SalesOrderEvent.Init )
            .apply {
                setCustomer(customerId)
                products.forEach { addProduct(it) }
            }

    fun makeSalesOrderWithoutCustomer(vararg products: Product) =
        AggregateRootBuilder
            .build { SalesOrder(AggregateRootId(UUID.randomUUID())) }
            .newInstance()
            .apply {
                products.forEach { addProduct(it) }
            }

    val customerId = EntityId(UUID.randomUUID())
    val product1 = Product(EntityId(UUID.randomUUID()), Price(BigDecimal.valueOf(10.0), Vat.vat24), "Test product1", "Test product description")
    val product2 = Product(EntityId(UUID.randomUUID()), Price(BigDecimal.valueOf(12.0), Vat.vat24), "Test product2", "Test product description")
    val product3 = Product(EntityId(UUID.randomUUID()), Price(BigDecimal.valueOf(14.0), Vat.vat24), "Test product3", "Test product description")
}

