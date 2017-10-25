package io.paju.templateservice.domain.product

import io.paju.templateservice.domain.IdentifiedValueObject

data class SellableProduct(override val price: Price,
                           override val name: String,
                           override val description: String): Product, IdentifiedValueObject() {
}