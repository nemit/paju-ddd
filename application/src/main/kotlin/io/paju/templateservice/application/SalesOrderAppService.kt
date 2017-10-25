package io.paju.templateservice.application

import io.paju.templateservice.domain.adapters.SalesOrderDto
import io.paju.templateservice.domain.salesorder.SalesOrderRepository

class SalesOrderAppService(val repo: SalesOrderRepository) {
    fun listSalesOrders(): List<SalesOrderDto> {
        return repo.findAll().map({so -> SalesOrderDto(so.id().value, so.listReservedServices(), so.listProducts(), so.listParticipantsAndRoles())})
    }
}