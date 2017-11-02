package io.paju.templateservice.domain.salesorder

import io.paju.templateservice.domain.customer.CustomerId

object SalesOrderFactory {

    fun createNew(customer: CustomerId): SalesOrder {
        val salesOrder = SalesOrder(customer)
        return salesOrder
    }

    // Factor exposes internal data to data containers that can be implemented by repositories
    // Infrastructure implementation is decoupled via provider constructor function (NAMING??)
    fun <T: SalesOrderInternalData> salesOrderData(providerConstructor: (id: SalesOrderId,
                                                                         deleted: Boolean,
                                                                         confirmed: Boolean)
                                                                 -> T, salesOrder: SalesOrder): T {
        val mapper = providerConstructor(salesOrder.id(),
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