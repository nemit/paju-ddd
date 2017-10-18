package io.paju.templateservice.adapters

import io.paju.templateservice.model.salesorder.ParticipantAndRole
import io.paju.templateservice.model.salesorder.ProductAndStatus
import io.paju.templateservice.model.salesorder.ServiceAndStatus
import java.util.*

data class SalesOrderDto(val id: UUID, val services: List<ServiceAndStatus>, val products: List<ProductAndStatus>, val participants: List<ParticipantAndRole>)