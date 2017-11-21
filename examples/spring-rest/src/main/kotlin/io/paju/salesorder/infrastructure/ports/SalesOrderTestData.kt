package io.paju.salesorder.domain

import io.paju.ddd.AggregateRootId
import io.paju.ddd.EntityId
import java.math.BigDecimal
import java.util.UUID

object SalesOrderTestData {
    fun makeSalesOrder(vararg products: Product): SalesOrder {
        return SalesOrder(AggregateRootId.fromObject(UUID.randomUUID()), true).apply {
            setCustomer(customerId)
            products.forEach { addProduct(it) }
        }
    }
    val customerId = EntityId.fromObject("1")
    val product1 = Product(EntityId.fromObject(UUID.randomUUID()), Price(BigDecimal.valueOf(10.0), Vat.vat24), "Test product1", "Test product description")
    val product2 = Product(EntityId.fromObject(UUID.randomUUID()), Price(BigDecimal.valueOf(12.0), Vat.vat24), "Test product2", "Test product description")
}

