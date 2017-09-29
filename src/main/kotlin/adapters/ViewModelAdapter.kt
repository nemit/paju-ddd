package adapters

import common.DateFormat
import model.*

fun Reservation.convertToViewModel(): ReservationViewModel {
    return ReservationViewModel("${DateFormat.html.format(this.reservationPeriod.start)} -> ${DateFormat.html.format(this.reservationPeriod.end)}",
            this.productReservations.fold("", { acc, prod -> if(acc.length > 0) acc + ", " + prod.product.name else prod.product.name}),
            when (this) {
                is QuotedReservation -> "Tarjottu varaus"
                is DraftReservation -> "Alustava varaus"
                is ConfirmedReservation -> "Vahvistettu varaus, vahvituspvm " + this.confirmationDate
                else -> "Tuntematon varaustyyppi"
            }
    )
}

fun Product.converToViewModel(selected: Boolean = false): ProductViewModel {
    return ProductViewModel(this.name, selected)
}


