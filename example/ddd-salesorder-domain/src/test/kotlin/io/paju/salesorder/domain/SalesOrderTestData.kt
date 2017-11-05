package io.paju.salesorder.domain

import io.paju.ddd.AggregateRootId
import io.paju.ddd.EntityId
import java.util.UUID

object SalesOrderTestData {
    fun makeSalesOrder(vararg products: Product): SalesOrder {
        val s = SalesOrder(AggregateRootId.fromObject(UUID.randomUUID()), customerId)
        products.forEach { s.addProduct(it) }
        return s
    }
    val customerId = EntityId.fromObject("1")
    val product1 = Product(EntityId.fromObject(UUID.randomUUID()), Price(10.0f, Vat.vat24), "Test product1", "Test product description")
    val product2 = Product(EntityId.fromObject(UUID.randomUUID()), Price(12.0f, Vat.vat24), "Test product2", "Test product description")
}

