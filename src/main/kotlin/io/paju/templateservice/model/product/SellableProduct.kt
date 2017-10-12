package io.paju.templateservice.model.product

data class SellableProduct(override val price: Price,
                           override val vat: Vat,
                           override val name: String,
                           override val description: String): Product {
}