package io.paju.templateservice.ports

import io.paju.templateservice.SalesOrderAppService
import io.paju.templateservice.shared.DateFormat
import io.paju.templateservice.model.*
import spark.ModelAndView
import spark.kotlin.get
import spark.kotlin.post
import spark.template.velocity.VelocityTemplateEngine
import java.util.*

fun main(args: Array<String>) {

    // run main and check http://localhost:4567/salesorders for simple html representation

    get("/salesorders") {
        response.type("text/html")
        try {
            val salesOrders = SalesOrderAppService.listSalesOrders()
            // DTO should be mapped to proper view model..
            val result = VelocityTemplateEngine().render(ModelAndView(mutableMapOf<String, Any>(Pair("salesOrders", salesOrders)),
                    "salesorders.vm"))
            result
        } catch (e: Exception) {
            println(e.message)
            "error"
        }
    }
}