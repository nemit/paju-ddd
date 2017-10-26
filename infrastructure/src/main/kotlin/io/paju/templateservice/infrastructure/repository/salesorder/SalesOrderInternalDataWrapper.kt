package io.paju.templateservice.infrastructure.repository.salesorder

import io.paju.templateservice.domain.salesorder.*

//TODO Better naming for these
class SalesOrderInternalDataWrapper(override val confirmed: Boolean,
                                    override val deleted: Boolean,
                                    val id: SalesOrderId): SalesOrderInternalData {
}
