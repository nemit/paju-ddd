package io.paju.salesorder.infrastructure.ports.resources

import io.paju.salesorder.domain.DeliveryStatus
import io.paju.salesorder.domain.PaymentStatus
import io.paju.salesorder.domain.SalesOrder
import io.paju.salesorder.infrastructure.ports.controllers.SalesOrderController
import org.springframework.hateoas.EntityLinks
import org.springframework.hateoas.Link
import org.springframework.hateoas.Resources
import org.springframework.hateoas.core.EmbeddedWrapper

class SalesOrderResourceAssembler(val entityLinks: EntityLinks) : EmbeddableResourceAssemblerSupport<SalesOrder, SalesOrderResource, SalesOrderController>(SalesOrderController::class.java, SalesOrderResource::class.java) {

    override fun toResource(entity: SalesOrder): SalesOrderResource {
        val r = createResourceWithId(entity.state.customerId.toString(), entity)
        // this links to other resouce, Customer
        // inside single Spring app we can use Entity links, but as this
        // represents other remote service it's not used.
        // TODO resolve how should Sales order discover link for Customer?
        //      - link maybe stored when customer added to Sales Order
        r.add(Link("http://localhost:8080/customers/${r.customerId}", "customer"))

        if (entity.products(PaymentStatus.OPEN).isNotEmpty()) {
            r.add(entityLinks.linkForSingleResource(r::class.java, entity.id.toString()).slash("payment").withRel("payment"))
        }

        if (entity.products(DeliveryStatus.NOT_DELIVERED).isNotEmpty()) {
            r.add(entityLinks.linkForSingleResource(r::class.java, entity.id.toString()).slash("delivery").withRel("delivery"))
        }

        return r
    }

    override fun instantiateResource(entity: SalesOrder): SalesOrderResource {
        val customerAssembler = CustomerResourceAssember()
        // in real life if we'd like to embedded customer resource we'd either
        // a) fetch it from repository if resource is local
        // b) fetch wit HTTP is resource is remote
        // alternative to embedding is to add customer simply as link
        val customer = Customer("Kimmo", "Eklund", entity.state.customerId.toString())
        val embeddables = mutableListOf<EmbeddedWrapper>()
        embeddables.add(customerAssembler.toEmbeddable(customer))
        return SalesOrderResource(Resources<EmbeddedWrapper>(embeddables), entity.state.customerId.toString(), entity.state.confirmed, entity.state.deleted, entity.state.products)
    }
}