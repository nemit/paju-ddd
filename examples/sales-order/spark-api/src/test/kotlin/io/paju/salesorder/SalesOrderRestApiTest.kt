package io.paju.salesorder

import io.paju.ddd.AggregateRootId
import io.paju.ddd.EntityId
import io.paju.ddd.EntityId.Companion.NotInitialized
import io.paju.salesorder.command.AddProductToSalesOrder
import io.paju.salesorder.command.CreateSalesOrder
import io.paju.salesorder.command.DeleteSalesOrder
import io.paju.salesorder.command.PayDeliveredProduct
import io.paju.salesorder.domain.Currencies
import io.paju.salesorder.domain.PaymentMethod
import io.paju.salesorder.domain.Price
import io.paju.salesorder.domain.Product
import io.paju.salesorder.domain.Vat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.util.UUID

internal class SalesOrderRestApiTest {

    val aggregateId = AggregateRootId(UUID.fromString("e94ce3ee-1631-47f7-a1e1-3a3f4f3fa4a5"))
    val enityId = NotInitialized

    @Test
    fun jsonToCreateSalesOrder() {
        val command = CreateSalesOrder(aggregateId, enityId)
        val serialized = Serializer.commandToJson(command)
        val deserialize = Serializer.jsonToCommand(serialized)
        assertEquals(command, deserialize)
    }

    @Test
    fun jsonToPayDeliveredProducts() {
        val command = PayDeliveredProduct(aggregateId, 1, EntityId(UUID.randomUUID().toString()), PaymentMethod.INVOICE)
        val serialized = Serializer.commandToJson(command)
        val deserialize = Serializer.jsonToCommand(serialized)
        assertEquals(command, deserialize)
    }

    @Test
    fun jsonToAddProductToSalesOrder() {
        val command = AddProductToSalesOrder(aggregateId, 1, Product(NotInitialized, Price(BigDecimal.ONE, Vat.vat0, Currencies.EURO), "qwerty", "qwerty"))
        val serialized = Serializer.commandToJson(command)
        val deserialize = Serializer.jsonToCommand(serialized)
        assertEquals(command, deserialize)
    }

    @Test
    @Disabled
    fun printJsonCommandToConsole() {
        val create = CreateSalesOrder(aggregateId, enityId)
        val deleted = DeleteSalesOrder(aggregateId, 1)
        val addProduct = AddProductToSalesOrder(aggregateId, 1, Product(NotInitialized, Price(BigDecimal.ONE, Vat.vat0, Currencies.EURO), "qwerty", "qwerty"))
        println(Serializer.commandToJson(create))
        println(Serializer.commandToJson(deleted))
        println(Serializer.commandToJson(addProduct))

    }

}