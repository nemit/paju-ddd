package io.paju.salesorder.infrastructure.ports.resources

import com.fasterxml.jackson.annotation.JsonUnwrapped
import io.paju.salesorder.domain.state.ProductState
import org.springframework.hateoas.ResourceSupport

data class SalesOrderResource(val customerId: String,
    val confirmed: Boolean,
    val deleted: Boolean,
    @JsonUnwrapped
    val products: List<ProductState>) : ResourceSupport()