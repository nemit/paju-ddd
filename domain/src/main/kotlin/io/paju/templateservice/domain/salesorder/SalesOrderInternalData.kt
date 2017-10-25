package io.paju.templateservice.domain.salesorder

import io.paju.templateservice.domain.customer.CustomerId
import io.paju.templateservice.shared.RepositoryMediator

interface SalesOrderInternalData {
    val confirmed: Boolean
    val deleted: Boolean
}