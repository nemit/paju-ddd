package io.paju.templateservice.shared

import io.paju.templateservice.model.salesorder.customer
import io.paju.templateservice.model.salesorder.person1
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class IdentifiedValueObjectTest {

    @Test
    fun testLocalId() {
        // Test that surrogate ID won't effect value object equality
        val customerWithId = person1.copy()
        customerWithId.setValueObjectLocalId(99)
        assertEquals(person1, customerWithId)

        val customerWithIdAndDifferentName = person1.copy(firstName = "Modified")
        customerWithIdAndDifferentName.setValueObjectLocalId(99)

        assertNotEquals(customerWithId, customerWithIdAndDifferentName)
    }
}