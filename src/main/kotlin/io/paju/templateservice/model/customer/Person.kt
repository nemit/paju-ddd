package io.paju.templateservice.model.customer

import io.paju.templateservice.shared.IdentifiedValueObject
import java.time.LocalDate
import java.util.*

/**
    Person is VALUE OBJECT representing person
 */
data class Person(val dateOfBirth: LocalDate,
                  val firstName: String,
                  val lastName: String,
                  val sex: PersonSex = PersonSex.UNDISCLOSED): IdentifiedValueObject()

enum class PersonSex {
    MALE, FEMALE, UNDISCLOSED
}


fun sexEnumFromString(sex: String): PersonSex {
    return when (sex) {
        "MALE" -> PersonSex.MALE
        "FEMALE" -> PersonSex.FEMALE
        else -> PersonSex.UNDISCLOSED
    }
}