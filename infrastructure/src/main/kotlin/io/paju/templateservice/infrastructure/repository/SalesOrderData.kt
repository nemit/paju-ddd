package io.paju.templateservice.infrastructure.repository

import io.paju.templateservice.domain.customer.CustomerId
import io.paju.templateservice.domain.salesorder.*

class SalesOrderData(val customerId: CustomerId,
                     val products: List<ProductAndStatus>,
                     val services: List<ServiceAndStatus>,
                     val participants: List<ParticipantAndRole>,
                     override val confirmed: Boolean,
                     override val deleted: Boolean,
                     val id: SalesOrderId): SalesOrderInternalData {
}
