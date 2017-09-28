package adapters

import java.util.*

data class ReservationViewModel(val reservationPeriod: String, val productList: String, val type: String)
data class ReservationQueryViewModel(val startDate: String, val endDate: String, val products: List<ProductViewModel>)
data class ProductViewModel(val name: String, val selected: Boolean)
data class ProductAvailableViewModel(val name: String, val available: Boolean)


