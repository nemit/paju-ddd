package io.paju.templateservice

import io.paju.templateservice.adapters.SalesOrderDto
import io.paju.templateservice.model.salesorder.SalesOrderRepository

object SalesOrderAppService {

    fun listSalesOrders(): List<SalesOrderDto> {
        val repo = SalesOrderRepository()
        return repo.findAll().map({so -> SalesOrderDto(so.id.value, so.listReservedServices(), so.listProducts(), so.listParticipantsAndRoles())})
    }
}