package io.paju.templateservice.domain.salesorder

import io.paju.templateservice.domain.customer.CustomerId
import io.paju.templateservice.shared.RepositoryMediator

/**
 * Interface used to define internal properties not accessible via public Aggregate interface
 * Repositories will have to implement this interface and use the implementation to access
 * these properties via SalesOrderFactory. These properties have internal visibility in the
 * Aggregate.
 */
interface SalesOrderInternalData {
    val confirmed: Boolean
    val deleted: Boolean
}