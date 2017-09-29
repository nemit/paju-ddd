package model


import common.DateRange
import java.time.temporal.ChronoUnit
import java.util.*

// COMMON RESERVATION INTERFACE

interface Reservation {
    val productReservations: List<ProductReservation>
    val reservationPeriod: ReservationPeriod
    val id: UUID
}

// RESERVATION TYPES

class DraftReservation(override val id: UUID, override val reservationPeriod: ReservationPeriod, override val productReservations: MutableList<ProductReservation>) : Reservation {
    val reservationDate = Date()
    val expires = Date(reservationDate.toInstant().plus(14, ChronoUnit.DAYS).toEpochMilli())

    fun confirmReservation(confirmationDate: Date): ConfirmedReservation {
        if (confirmationDate.after(expires)) throw Exception("Reservation has expired")

        return ConfirmedReservation(this.id, confirmationDate, this.reservationDate, this.reservationPeriod, this.productReservations)
    }
}

data class QuotedReservation(override val id: UUID, val quoteSent: Date, val reservations: DraftReservation, override val reservationPeriod: ReservationPeriod): Reservation {
    override val productReservations: List<ProductReservation>
        get() = this.reservations.productReservations
}

data class ConfirmedReservation(override val id:UUID, val confirmationDate: Date, val varausPvm:Date, override val reservationPeriod: ReservationPeriod, override val productReservations: MutableList<ProductReservation> ): Reservation {
    fun beginServiceProcess(): StartedService {
        return StartedService(Date())
    }
}

// SUPPORT TYPES

class ReservationPeriod(start: Date, end: Date) : DateRange(start, end)
data class ProductReservation(val product: Product, val reservationPeriod: ReservationPeriod, val available: Boolean)
data class ReservationQuery(val products: List<Product>, val period: ReservationPeriod)
data class ReservationQueryResponse(val productAvailablity: List<ProductReservation>)



