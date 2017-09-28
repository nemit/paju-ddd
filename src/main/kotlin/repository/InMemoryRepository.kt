package repository

import model.*

class InMemoryRepository() {

    val draftReservations:MutableList<DraftReservation> = mutableListOf()
    val quotedReservations: MutableList<QuotedReservation> = mutableListOf()
    val confirmedReservations: MutableList<ConfirmedReservation> = mutableListOf()

    fun saveDraftReservation(data: DraftReservation) {
        draftReservations.add(data)
    }

    fun saveQuotedReservation(data: QuotedReservation) {
        quotedReservations.add(data)
    }

    fun saveConfirmedReservation(data: ConfirmedReservation) {
        confirmedReservations.add(data)
    }

    fun allReservations(): List<Reservation> {
        val list: MutableList<Reservation> = mutableListOf()
        list.addAll(draftReservations)
        list.addAll(quotedReservations)
        list.addAll(confirmedReservations)
        return list
    }
}