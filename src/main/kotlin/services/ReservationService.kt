package services

import model.*
import repository.InMemoryRepository
import java.util.*

class ReservationService {

    private val repository: InMemoryRepository = InMemoryRepository()

    /// PUBLIC SERVICE INTERFACE

    fun performReservationQuery(query: ReservationQuery): ReservationQueryResponse {
        return ReservationQueryResponse(availableCapacityForProducts(query.products, query.period))
    }

    fun createDraftReservation(reservation: DraftReservation) {
        if (reservation.productReservations.isEmpty()) throw Exception("Vähitään yksi product vaaditaan alustavaan varaukseen")
        repository.saveDraftReservation(reservation)
    }

    fun sendQuote(reservation: DraftReservation): QuotedReservation {
        // SEND EMAIL
        val quoted = QuotedReservation(reservation.id, Date(), reservation, reservation.reservationPeriod)
        repository.saveQuotedReservation(quoted)
        return quoted
    }

    fun listAllReservations(): List<Reservation> {
        return repository.allReservations()
    }

    // PRIVATE FUNCTIONS

    private fun availableCapacityForProducts(products: List<Product>, reservationPeriod: ReservationPeriod): List<ProductReservation> {
        val status = mutableListOf<ProductReservation>()

        for (product in products) {
            val random = Random()
            status.add(ProductReservation(product, reservationPeriod, random.nextBoolean()))
        }
        return status
    }

    private fun availableCapacityForProduct(product: Product, reservationPeriod: ReservationPeriod): ProductReservation
    {
        val random = Random()
        return ProductReservation(product, reservationPeriod, random.nextBoolean())
    }

}

