package io.paju.salesorder.infrastructure.ports.resources

import com.fasterxml.jackson.annotation.JsonUnwrapped
import io.paju.salesorder.domain.state.ProductState
import org.springframework.hateoas.Resources
import org.springframework.hateoas.core.EmbeddedWrapper

data class SalesOrderResource(override val embeddeds: Resources<EmbeddedWrapper>,
    val customerId: String,
    val confirmed: Boolean,
    val deleted: Boolean,
    @JsonUnwrapped
    val products: List<ProductState>) : ResourceWithEmbeddeds()