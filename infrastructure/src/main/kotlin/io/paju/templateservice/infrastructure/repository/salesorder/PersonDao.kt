package io.paju.templateservice.infrastructure.repository.salesorder

import io.paju.templateservice.domain.customer.Person
import io.paju.templateservice.domain.salesorder.ParticipantAndRole
import io.paju.templateservice.domain.salesorder.SalesOrderId
import io.paju.templateservice.domain.salesorder.participantRoleFromString
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.generic.GenericType

class PersonDao(val jdbi: Jdbi) {
    fun insert(data: Person): Person {
        return jdbi.withHandle<Person, Exception> { handle ->
            val sql =
                """
                    INSERT INTO person (first_name, last_name, sex, date_of_birth)
                    VALUES (:first_name, :last_name, :sex, :date_of_birth)
                """
            val map = handle.createUpdate(sql)
                .bind("first_name", data.firstName)
                .bind("last_name", data.lastName)
                .bind("sex", data.sex)
                .bind("date_of_birth", data.dateOfBirth)
                .executeAndReturnGeneratedKeys()
                .collectInto(object : GenericType<Map<Person, Long>>() {})
            val person = map.keys.first()
            val id = map.getOrDefault(person, -1) // should return error instead
            person.setValueObjectLocalId(id)
            person
        }
    }

    fun insert(salesOrderId: SalesOrderId, data: ParticipantAndRole) {
        jdbi.useHandle<Exception>({ handle ->
            val sql =
                """
                    INSERT INTO person_role_in_sales_order (sales_order_id, person_id, role)
                    VALUES (:sales_order_id, :person_id, :role)
                """

            val update = handle.createUpdate(sql)
            update.bind("sales_order_id", salesOrderId.value)
                .bind("person_id", data.participant.valueObjectLocalId())
                .bind("role", data.role)
        })
    }

    fun findParticipants(id: SalesOrderId): List<ParticipantAndRole> {
        val map = jdbi.withHandle<Map<Person, String>, Exception>({ handle ->
            val sql =
                """
                    SELECT p.id, p.first_name, p.last_name, p.sex, p.date_of_birth, r.role FROM person p,
                        person_role_in_sales_order r WHERE r.person_id = p.id and r.sales_order_id = :id
                """
            val query = handle.createQuery(sql)
            val results: Map<Person, String> = query.bind("id", id.value).collectInto(object : GenericType<Map<Person, String>>() {})
            results
        })
        val list = mutableListOf<ParticipantAndRole>()
        for ((person, role) in map) {
            list.add(ParticipantAndRole(person, participantRoleFromString(role)))
        }
        return list
    }

    fun delete(data: Person) {
        jdbi.useHandle<Exception>({ handle ->
            val sql = "DELETE FROM person WHERE id = :id"
            val update = handle.createUpdate(sql)
            update.bind("id", data.valueObjectLocalId())
            update.execute()
        })
    }
}
