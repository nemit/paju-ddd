package io.paju.templateservice.infrastructure.ports


import io.paju.templateservice.infrastructure.repository.RepositoryFactory
import io.paju.templateservice.application.SalesOrderAppService
import spark.ModelAndView
import spark.kotlin.get
import spark.template.velocity.VelocityTemplateEngine

fun main(args: Array<String>) {

    val salesOrderService = SalesOrderAppService(RepositoryFactory.salesOrderRepository())
    // run main and check http://localhost:4567/salesorders for simple html representation

    get("/salesorders") {
        response.type("text/html")
        try {

            val salesOrders = salesOrderService.listSalesOrders()
            // DTO should be mapped to proper view domain..
            val result = VelocityTemplateEngine().render(ModelAndView(mutableMapOf<String, Any>(Pair("salesOrders", salesOrders)),
                    "salesorders.vm"))
            result
        } catch (e: Exception) {
            println(e.message)
            "error"
        }
    }
}