package io.paju.templateservice.infrastructure.repository

import io.paju.templateservice.domain.salesorder.SalesOrderId
import jdk.jfr.events.ExceptionThrownEvent
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.mapTo
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate

class PersonDao(val jdbi: Jdbi) {

    fun insert(data: PersonDb): PersonDb {
        return jdbi.withHandle<PersonDb, Exception> { handle ->
            val sql = "INSERT INTO person (first_name, last_name, sex, date_of_birth) " +
                    "VALUES (:first_name, :last_name, :sex, :date_of_birth)"
            val update = handle.createUpdate(sql)
            update.bind("first_name", data.first_name)
                    .bind("last_name", data.last_name)
                    .bind("sex", data.sex)
                    .bind("date_of_birth", data.date_of_birth)
                    .executeAndReturnGeneratedKeys()
                    .mapTo(PersonDb::class.java)
                    .findOnly()
        }
    }

    fun insert(data: PersonRoleInSalesOrderDb) {
        jdbi.useHandle<Exception>({ handle ->
            val sql = "INSERT INTO person_role_in_sales_order (sales_order_id, person_id, role) " +
                    "VALUES (:sales_order_id, :person_id, :role)"

            val update = handle.createUpdate(sql)
            update.bind("sales_order_id", data.sales_order_id)
                    .bind("person_id", data.person_id)
                    .bind("role", data.role)
        })
    }

    fun findPersons(id: SalesOrderId): List<PersonRoleDb> {
        return jdbi.withHandle<List<PersonRoleDb>, Exception>( { handle ->
            val sql ="SELECT p.id, p.first_name, p.last_name, p.sex, p.date_of_birth, r.role FROM person p, " +
                    "person_role_in_sales_order r WHERE r.person_id = p.id and r.sales_order_id = :id"

            val query = handle.createQuery(sql)
            val results: List<PersonRoleDb> = query.bind("id", id.value).mapTo<PersonRoleDb>().list()
            results
        })
    }

    fun delete(data: PersonDb) {
        jdbi.useHandle<Exception>({ handle ->
            val sql ="DELETE FROM person WHERE id = :id"
            val update = handle.createUpdate(sql)
            update.bind("id", data.id)
            update.execute()
        })
    }
}