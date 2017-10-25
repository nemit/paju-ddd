package io.paju.templateservice.infrastructure.repository

import io.paju.templateservice.domain.salesorder.SalesOrderId
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate

interface PersonDao {

    @SqlUpdate("INSERT INTO person (first_name, last_name, sex, date_of_birth) " +
            "VALUES (:data.first_name, :data.last_name, :data.sex, :data.date_of_birth)"  )
    @GetGeneratedKeys()
    fun insert(data: PersonDb): PersonDb

    @SqlUpdate("INSERT INTO person_role_in_sales_order (sales_order_id, person_id, role) " +
            "VALUES (:data.sales_order_id, :data.person_id, :data.role)")
    fun insert(data: PersonRoleInSalesOrderDb)

    @SqlQuery("SELECT p.id, p.first_name, p.last_name, p.sex, p.date_of_birth, r.role FROM person p, person_role_in_sales_order r WHERE r.person_id = p.id and r.sales_order_id = :id.value")
    fun findPersons(id: SalesOrderId): List<PersonRoleDb>

    @SqlUpdate("DELETE FROM person WHERE id = :data.id")
    fun delete(data: PersonDb)

}