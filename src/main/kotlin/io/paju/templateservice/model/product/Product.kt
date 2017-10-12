package io.paju.templateservice.model.product

interface Product {
    val price: Price
    val vat: Vat
    val name: String
    val description: String
}