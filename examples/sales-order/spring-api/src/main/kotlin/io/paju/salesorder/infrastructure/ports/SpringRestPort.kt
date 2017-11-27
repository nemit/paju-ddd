package io.paju.salesorder.infrastructure.ports

import io.paju.ddd.EntityId
import io.paju.ddd.infrastructure.localstore.LocalEventStore
import io.paju.salesorder.command.SalesOrderCommandHandler
import io.paju.salesorder.domain.PaymentMethod
import io.paju.salesorder.domain.Product
import io.paju.salesorder.domain.SalesOrderTestData
import io.paju.salesorder.domain.SalesOrderTestData.product1
import io.paju.salesorder.domain.SalesOrderTestData.product2
import io.paju.salesorder.infrastructure.SalesOrderRepository
import io.paju.salesorder.infrastructure.SalesOrderStoreJdbc
import io.paju.salesorder.service.DummyPaymentService
import org.flywaydb.core.Flyway
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import ru.yandex.qatools.embed.postgresql.EmbeddedPostgres

fun main(args: Array<String>) {
    SpringApplication.run(SpringRestPort::class.java, *args)
}

@SpringBootApplication
class SpringRestPort() {

    private val jdbcUrl = "jdbc:postgresql://localhost:5432/pajulahti?user=postgres&password=password"
    private val logger = LoggerFactory.getLogger(SpringRestPort::class.java)

    @Bean
    fun init(repository: SalesOrderRepository) =
        CommandLineRunner {
            val flyway = Flyway()
            flyway.setDataSource(jdbcUrl, EmbeddedPostgres.DEFAULT_USER, EmbeddedPostgres.DEFAULT_PASSWORD)
            flyway.migrate()
            val so = SalesOrderTestData.makeSalesOrder(product1, product2)
            so.deliverProduct(product2)
            repository.save(so, 1)
            logger.info("Sales Order created with id:${so.id}")
        }

    @Bean
    fun salesOrderRepository(): SalesOrderRepository {
        val store = SalesOrderStoreJdbc(jdbcUrl)
        val eventWriter = LocalEventStore()
        return SalesOrderRepository(eventWriter, store, store, store)
    }

    @Bean
    fun commandHandler(repository: SalesOrderRepository): SalesOrderCommandHandler {
        return SalesOrderCommandHandler(repository, DummyPaymentServiceImpl)
    }

}

object DummyPaymentServiceImpl : DummyPaymentService {
    @Suppress("unused_parameter")
    override fun handleProductPayment(product: Product, customerId: EntityId, paymentMethod: PaymentMethod) {
    }
}
