package io.paju.salesorder.infrastructure.ports

import io.paju.ddd.EntityId
import io.paju.ddd.infrastructure.localstore.LocalEventStore
import io.paju.salesorder.EmbeddedPostgresServer
import io.paju.salesorder.command.SalesOrderCommandHandler
import io.paju.salesorder.domain.PaymentMethod
import io.paju.salesorder.domain.Product
import io.paju.salesorder.domain.SalesOrderTestData
import io.paju.salesorder.infrastructure.SalesOrderRepository
import io.paju.salesorder.infrastructure.SalesOrderStoreJdbc
import io.paju.salesorder.service.DummyPaymentService
import org.slf4j.LoggerFactory

import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class SpringRestPort {

    private val logger = LoggerFactory.getLogger(SpringRestPort::class.java)

    @Bean
    fun init(repository: SalesOrderRepository) =
        CommandLineRunner {
            val so = SalesOrderTestData.makeSalesOrder()
            repository.save(so, 1)
            logger.info("Created sales order with ${so.id}")
        }


    @Bean
    fun salesOrderRepository(): SalesOrderRepository {
        val embeddedPostgres = EmbeddedPostgresServer.instance
        logger.info("Postgres started to ${embeddedPostgres.url}")
        val store = SalesOrderStoreJdbc(embeddedPostgres.url)
        val eventWriter = LocalEventStore()
        val repository = SalesOrderRepository(eventWriter, store, store, store)
        return repository
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

fun main(args: Array<String>) {
    SpringApplication.run(SpringRestPort::class.java, *args)
}