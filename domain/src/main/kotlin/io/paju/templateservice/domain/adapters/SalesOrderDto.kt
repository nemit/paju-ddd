package io.paju.templateservice.domain.adapters

import io.paju.templateservice.domain.salesorder.ParticipantAndRole
import io.paju.templateservice.domain.salesorder.ProductAndStatus
import io.paju.templateservice.domain.salesorder.ServiceAndStatus
import java.util.*

// TODO move from domain layer to application layer
data class SalesOrderDto(val id: UUID, val services: List<ServiceAndStatus>, val products: List<ProductAndStatus>, val participants: List<ParticipantAndRole>)