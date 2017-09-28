package ports

import adapters.*
import model.*
import services.ReservationService
import services.listProducts
import spark.ModelAndView
import spark.kotlin.get
import spark.kotlin.post
import spark.template.velocity.VelocityTemplateEngine
import java.util.*

fun main(args: Array<String>) {

    val service = ReservationService()
    val products = listProducts()
    val queryCache = mutableMapOf<String, Any>()

    get("/reservations") {
        val reservationList = service.listAllReservations()
        response.type("text/html")
        try {
            val result = VelocityTemplateEngine().render(ModelAndView(mutableMapOf<String, Any>(Pair("reservations", reservationList.map({r -> r.convertToViewModel()}))),
                    "reservations.vm"))
            result
        } catch (e: Exception) {
            println(e.message)
            "error"
        }
    }

    get("reservationquery") {
        response.type("text/html")
        try {
            val products = listProducts()
            queryCache.put("products", products.map({p -> p.converToViewModel()}))

            if (queryCache.get("startDate") == null)
                queryCache.put("startDate", DateFormat.html.format(Date()))

            if (queryCache.get("endDate") == null)
                queryCache.put("endDate", DateFormat.html.format(Date()))

            val result = VelocityTemplateEngine().render(ModelAndView(queryCache,
                    "reservation_query.vm"))
            result
        } catch (e: Exception) {
            println(e.message)
            e.message.toString()
        }
    }

    post("reservationquery") {
        val startDate = request.queryParams("startDate")
        val endDate = request.queryParams("endDate")

        if (startDate != null && endDate != null) {
            queryCache.put("startDate", startDate)
            queryCache.put("endDate", endDate)
            val productMap = request.queryMap("product")
            val queryProducts = mutableListOf<Product>()

            for ((key, value) in productMap.toMap()) {
                queryProducts.add(Product(key))
            }

            queryCache.put("products", listProducts().map({p -> p.converToViewModel(queryProducts.find({it.name.equals(p.name)}) != null)}))
            val period = ReservationPeriod(DateFormat.html.parse(startDate), DateFormat.html.parse(endDate))
            queryCache.put("dates", period.toSortedSet())

            val capacityResult = service.performReservationQuery(ReservationQuery(queryProducts, period))
            queryCache.put("productReservations", capacityResult.productAvailablity)
            queryCache.put("productAvailability", capacityResult.productAvailablity.map({p -> ProductAvailableViewModel(p.product.name, p.available)}))
        }

        val result = VelocityTemplateEngine().render(ModelAndView(queryCache,
                "reservation_query.vm"))
        result
    }

    post("draftreservation") {
        val startDate = queryCache.get("startDate") as String
        val endDate = queryCache.get("endDate") as String
        val productReservations = queryCache.get("productReservations") as List<ProductReservation>
        val reservation = DraftReservation(UUID.randomUUID(), ReservationPeriod(DateFormat.html.parse(startDate),DateFormat.html.parse(endDate)), productReservations.toMutableList())
        service.createDraftReservation(reservation)
        redirect("reservations")
    }
}