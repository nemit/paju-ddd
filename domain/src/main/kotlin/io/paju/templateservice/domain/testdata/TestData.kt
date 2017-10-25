package io.paju.templateservice.domain

import io.paju.templateservice.domain.customer.Customer
import io.paju.templateservice.domain.customer.CustomerId
import io.paju.templateservice.domain.customer.Person
import io.paju.templateservice.domain.customer.PersonSex
import io.paju.templateservice.domain.product.Price
import io.paju.templateservice.domain.product.SellableProduct
import io.paju.templateservice.domain.product.Vat
import java.time.LocalDate
import java.util.*

object TestData {
    val customer = Customer(CustomerId(UUID.randomUUID()),"Test customer", Person(LocalDate.now(), "Test", "Person", PersonSex.MALE))
    val product1 = SellableProduct(Price(10.0f, Vat.vat24), "Test product1", "Test product description")
    val product2 = SellableProduct(Price(12.0f, Vat.vat24), "Test product2", "Test product description")
    val person1 = Person(LocalDate.now(), "Pekka", "Person", PersonSex.MALE)
}

