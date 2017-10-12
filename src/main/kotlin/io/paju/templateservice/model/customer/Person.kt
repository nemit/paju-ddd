package io.paju.templateservice.model.customer

import java.util.*

/**
    Person is VALUE OBJECT representing person
 */
data class Person(val dateOfBirth: Date, val firstName: String, val lastName: String, val sex: PersonSex = PersonSex.UNDISCLOSED)

enum class PersonSex {
    MALE, FEMALE, UNDISCLOSED
}