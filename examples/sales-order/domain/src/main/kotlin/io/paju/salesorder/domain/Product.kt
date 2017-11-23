package io.paju.salesorder.domain

import io.paju.ddd.Entity
import io.paju.ddd.EntityId

data class Product(
    override val id: EntityId,
    val price: Price,
    val name: String,
    val description: String
) : Entity
