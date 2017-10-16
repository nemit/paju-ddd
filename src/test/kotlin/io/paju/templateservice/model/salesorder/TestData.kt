package io.paju.templateservice.model.salesorder

import io.paju.templateservice.model.customer.Customer
import io.paju.templateservice.model.customer.CustomerId
import io.paju.templateservice.model.customer.Person
import io.paju.templateservice.model.customer.PersonSex
import io.paju.templateservice.model.product.Price
import io.paju.templateservice.model.product.SellableProduct
import io.paju.templateservice.model.product.Vat
import java.time.LocalDate
import java.util.*

val customer = Customer(CustomerId(UUID.randomUUID()),"Test customer", Person(LocalDate.now(), "Test", "Person", PersonSex.MALE))
val product1 = SellableProduct(Price(10.0f, Vat.vat24), "Test product1", "Test product description")
val product2 = SellableProduct(Price(12.0f, Vat.vat24), "Test product2", "Test product description")
val person1 = Person(LocalDate.now(), "Pekka", "Person", PersonSex.MALE)