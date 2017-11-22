package io.paju.salesorder.infrastructure.ports.resources

import io.paju.salesorder.domain.state.SalesOrderState
import io.paju.salesorder.infrastructure.ports.controllers.SalesOrderController
import org.springframework.hateoas.Link
import org.springframework.hateoas.Resources
import org.springframework.hateoas.core.EmbeddedWrapper

class SalesOrderResourceAssembler : EmbeddableResourceAssemblerSupport<SalesOrderState, SalesOrderResource, SalesOrderController>(SalesOrderController::class.java, SalesOrderResource::class.java) {

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
        val customerAssembler = CustomerResourceAssember()
        // in real life if we'd like to embedded customer resource we'd either
        // a) fetch it from repository if resource is local
        // b) fetch wit HTTP is resource is remote
        // alternative to embedding is to add customer simply as link
        val customer = Customer("Kimmo", "Eklund", entity.customerId.toString())
        val embeddables = mutableListOf<EmbeddedWrapper>()
        embeddables.add(customerAssembler.toEmbeddable(customer))
        return SalesOrderResource(Resources<EmbeddedWrapper>(embeddables), entity.customerId.toString(), entity.confirmed, entity.deleted, entity.products)
    }
}