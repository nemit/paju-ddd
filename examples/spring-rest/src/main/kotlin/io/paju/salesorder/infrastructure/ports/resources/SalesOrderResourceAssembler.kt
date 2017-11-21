package io.paju.salesorder.infrastructure.ports.resources

import io.paju.salesorder.domain.state.SalesOrderState
import io.paju.salesorder.infrastructure.ports.controllers.SalesOrderController
import org.springframework.hateoas.Link
import org.springframework.hateoas.mvc.ResourceAssemblerSupport

class SalesOrderResourceAssembler : ResourceAssemblerSupport<SalesOrderState, SalesOrderResource>(SalesOrderController::class.java, SalesOrderResource::class.java) {

    override fun toResource(entity: SalesOrderState): SalesOrderResource {
        val r = createResourceWithId(entity.customerId.toString(), entity)

        // this links to other service, Customer
        // inside single Spring app we can use Entity links, but as this
        // represents other remote service it's not used.
        // TODO resolve how should Sales order discover link for Customer?
        //      - link maybe stored when customer added to Sales Order
        r.add(Link("http://localhost:8080/customers/${r.customerId}", "customer"))
        return r
    }

    override fun instantiateResource(entity: SalesOrderState): SalesOrderResource {
        return SalesOrderResource(entity.customerId.toString(), entity.confirmed, entity.deleted, entity.products)
    }
}