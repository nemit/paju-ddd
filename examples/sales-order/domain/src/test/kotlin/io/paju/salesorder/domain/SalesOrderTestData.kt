package io.paju.salesorder.domain

import io.paju.ddd.AggregateRootBuilder
import io.paju.ddd.AggregateRootId
import io.paju.ddd.EntityId
import java.math.BigDecimal
import java.util.UUID

object SalesOrderTestData {
    fun makeSalesOrder(vararg products: Product) =
        AggregateRootBuilder
            .build { SalesOrder(AggregateRootId.random()) }
            .newInstance()
            .apply {
                setCustomer(customerId)
                products.forEach { addProduct(it) }
            }

    fun makeSalesOrderWithoutCustomer(vararg products: Product) =
        AggregateRootBuilder
            .build { SalesOrder(AggregateRootId.random()) }
            .newInstance()
            .apply {
                products.forEach { addProduct(it) }
            }

    val customerId = EntityId.random()
    val product1 = Product(EntityId.random(), Price(BigDecimal.valueOf(10.0), Vat.vat24), "Test product1", "Test product description")
    val product2 = Product(EntityId.random(), Price(BigDecimal.valueOf(12.0), Vat.vat24), "Test product2", "Test product description")
    val product3 = Product(EntityId.random(), Price(BigDecimal.valueOf(14.0), Vat.vat24), "Test product3", "Test product description")
}

