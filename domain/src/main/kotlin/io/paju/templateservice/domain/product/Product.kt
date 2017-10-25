package io.paju.templateservice.domain.product

interface Product {
    val price: Price
    val name: String
    val description: String
}