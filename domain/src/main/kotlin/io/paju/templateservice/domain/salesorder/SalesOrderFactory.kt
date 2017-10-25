package io.paju.templateservice.domain.salesorder

import io.paju.templateservice.domain.customer.CustomerId

object SalesOrderFactory {

    fun createNew(customer: CustomerId): SalesOrder {
        val salesOrder = SalesOrder(customer)
        return salesOrder
    }

    // Factor exposes internal data to data containers that can be implemented by repositories
    fun <T: SalesOrderInternalData> salesOrderData(mapperFactory: (id: SalesOrderId,
                                                                   customerId: CustomerId,
                                                                   products: List<ProductAndStatus>,
                                                                   services: List<ServiceAndStatus>,
                                                                   participants: List<ParticipantAndRole>,
                                                                   deleted: Boolean,
                                                                   confirmed: Boolean)
                                                                 -> T, salesOrder: SalesOrder): T {
        val mapper = mapperFactory(salesOrder.id(),
                salesOrder.customer,
                salesOrder.listProducts(),
                salesOrder.listReservedServices(),
                salesOrder.listParticipantsAndRoles(),
                salesOrder.deleted,
                salesOrder.confirmed)
        return mapper
    }

    fun reconstitute(id: SalesOrderId,
                     customerId: CustomerId,
                     products: List<ProductAndStatus>,
                     services: List<ServiceAndStatus>,
                     participants: List<ParticipantAndRole>,
                     internalData: SalesOrderInternalData): SalesOrder {

        return SalesOrder(customerId,
                services.toMutableList(),
                products.toMutableList(),
                participants.toMutableList(),
                internalData.confirmed,
                internalData.deleted,
                id)
    }
}