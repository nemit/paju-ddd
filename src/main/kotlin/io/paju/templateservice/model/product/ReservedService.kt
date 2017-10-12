package io.paju.templateservice.model.product

import io.paju.templateservice.shared.DateRange
import java.util.*

/**
 * ReservedService is ENTITY representing instance of service that has been reserved for particular SalesOrder
 */

class ReservedService(override val price: Price,
                      override val vat: Vat,
                      override val name: String,
                      override val description: String,
                      val reservationPeriod: ReservationPeriod): Product {
}

class ReservationPeriod(start: Date, end: Date) : DateRange(start, end)
