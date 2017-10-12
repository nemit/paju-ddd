package io.paju.templateservice.ports

import io.paju.templateservice.shared.DateFormat
import io.paju.templateservice.model.*
import spark.ModelAndView
import spark.kotlin.get
import spark.kotlin.post
import spark.template.velocity.VelocityTemplateEngine
import java.util.*

fun main(args: Array<String>) {
    val queryCache = mutableMapOf<String, Any>()

    get("/salesorders") {
        response.type("text/html")
        try {
            /*
            val result = VelocityTemplateEngine().render(ModelAndView(mutableMapOf<String, Any>(Pair("reservations", reservationList.map({r -> r.convertToViewModel()}))),
                    "reservations.vm"))
            result*/
        } catch (e: Exception) {
            println(e.message)
            "error"
        }
    }
}