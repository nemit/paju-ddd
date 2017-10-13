package io.paju.templateservice.model.product

import io.paju.templateservice.shared.IdentifiedValueObject

data class SellableProduct(override val price: Price,
                           override val name: String,
                           override val description: String): Product, IdentifiedValueObject() {
}