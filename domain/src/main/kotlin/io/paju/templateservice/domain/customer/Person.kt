package io.paju.templateservice.domain.customer

import io.paju.templateservice.domain.IdentifiedValueObject
import java.time.LocalDate

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