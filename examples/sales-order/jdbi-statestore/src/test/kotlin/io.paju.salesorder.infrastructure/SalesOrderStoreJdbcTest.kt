

import io.paju.ddd.EntityId
import io.paju.ddd.infrastructure.localstore.LocalEventStore
import io.paju.salesorder.domain.DeliveryStatus
import io.paju.salesorder.domain.SalesOrderTestData.makeSalesOrder
import io.paju.salesorder.domain.SalesOrderTestData.makeSalesOrderWithoutCustomer
import io.paju.salesorder.domain.SalesOrderTestData.product1
import io.paju.salesorder.domain.SalesOrderTestData.product2
import io.paju.salesorder.infrastructure.SalesOrderRepository
import io.paju.salesorder.infrastructure.SalesOrderStoreJdbc
import org.flywaydb.core.Flyway
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.util.UUID

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tag("integration")
internal class SalesOrderStoreJdbcTest {

    private val jdbcUrl = "jdbc:postgresql://localhost:5432/pajuddd?user=postgres&password=password"

    @BeforeAll
    fun migrateDb() {
        val jdbcUrl = "jdbc:postgresql://localhost:5432/pajuddd?user=postgres&password=password"
        val flyway = Flyway()
        flyway.setDataSource(jdbcUrl, "postgres", "password")
        flyway.migrate()
    }

    val store = SalesOrderStoreJdbc(jdbcUrl)
    val eventWriter = LocalEventStore()
    val repo = SalesOrderRepository(eventWriter, store, store, store)

    @Test
    fun addNewSalesOrderWithoutCustomerId() {
        val salesOrder = makeSalesOrderWithoutCustomer(product1, product2)
        salesOrder.deliverProduct(product1)
        repo.save(salesOrder, 1)

        val salesOrderFromDb = repo.getById(salesOrder.id())
        assertNotNull(salesOrderFromDb)
        assert(salesOrderFromDb.products(DeliveryStatus.NOT_DELIVERED).isNotEmpty())
        assert(salesOrderFromDb.products(DeliveryStatus.DELIVERED).isNotEmpty())
        assertNull(salesOrderFromDb.state().customerId)
    }

    @Test
    fun addAndModify() {
        val salesOrder = makeSalesOrder(product1, product2)
        val customerId = EntityId(UUID.randomUUID())
        salesOrder.deliverProduct(product1)
        salesOrder.setCustomer(customerId)
        repo.save(salesOrder, 1)

        val salesOrderFromDb = repo.getById(salesOrder.id())
        assertNotNull(salesOrderFromDb)

        assert(salesOrderFromDb.products(DeliveryStatus.NOT_DELIVERED).isNotEmpty())
        assert(salesOrderFromDb.products(DeliveryStatus.DELIVERED).isNotEmpty())
        assertEquals(salesOrderFromDb.state().customerId, customerId)

        salesOrderFromDb.deliverProduct(product2)
        salesOrderFromDb.confirmSalesOrder()

        repo.save(salesOrderFromDb, 2)

        val salesOrderFromDb2 = repo.getById(salesOrder.id())
        assertNotNull(salesOrderFromDb2)
        assertTrue(salesOrderFromDb2.isEveryProductDelivered())
        assertTrue(salesOrderFromDb2.state().confirmed)
    }
}