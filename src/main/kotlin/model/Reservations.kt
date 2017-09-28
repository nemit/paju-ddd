package model


import java.time.temporal.ChronoUnit
import java.util.*

class ReservationPeriod(start: Date, end: Date) : DateRange(start, end)

open class DateRange(val start: Date, val end: Date) : Iterable<Date>{
    override fun iterator(): Iterator<Date>  = object : Iterator<Date> {
        var current: Date = start

        override fun next(): Date {
            if (!hasNext()) {
                throw NoSuchElementException()
            }

            val result = current
            current = Date(current.toInstant().plus(1, ChronoUnit.DAYS).toEpochMilli())
            return result
        }

        override fun hasNext(): Boolean {
            return current <= end
        }
    }
}

data class ProductReservation(val product: Product, val reservationPeriod: ReservationPeriod, val available: Boolean)
data class QuotedReservation(override val id: UUID, val quoteSent: Date, val reservations: DraftReservation, override val reservationPeriod: ReservationPeriod): Reservation {
    override val productReservations: List<ProductReservation>
        get() = this.reservations.productReservations
}

interface Reservation {
    val productReservations: List<ProductReservation>
    val reservationPeriod: ReservationPeriod
    val id: UUID
}

// this would also contain customer and any other
data class ReservationQuery(val products: List<Product>, val period: ReservationPeriod)
data class ReservationQueryResponse(val productAvailablity: List<ProductReservation>)

class DraftReservation(override val id: UUID, override val reservationPeriod: ReservationPeriod, override val productReservations: MutableList<ProductReservation>) : Reservation {
    val reservationDate = Date()
    val expires = Date(reservationDate.toInstant().plus(14, ChronoUnit.DAYS).toEpochMilli())

    fun confirmReservation(confirmationDate: Date): ConfirmedReservation {
        if (confirmationDate.after(expires)) throw Exception("Reservation has expired")

        return ConfirmedReservation(this.id, confirmationDate, this.reservationDate, this.reservationPeriod, this.productReservations)
    }
}

data class ConfirmedReservation(override val id:UUID, val confirmationDate: Date, val varausPvm:Date, override val reservationPeriod: ReservationPeriod, override val productReservations: MutableList<ProductReservation> ): Reservation {
    fun beginServiceProcess(): StartedService {
        return StartedService(Date())
    }
}


