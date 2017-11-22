package io.paju.salesorder.infrastructure.ports.resources

import io.paju.salesorder.domain.SalesOrder
import io.paju.salesorder.infrastructure.ports.controllers.SalesOrderController
import org.springframework.hateoas.EntityLinks
import org.springframework.hateoas.Resources
import org.springframework.hateoas.core.EmbeddedWrapper

class SalesOrderResourceAssembler(val entityLinks: EntityLinks) : EmbeddableResourceAssemblerSupport<SalesOrder, SalesOrderResource, SalesOrderController>(SalesOrderController::class.java, SalesOrderResource::class.java) {

    override fun toResource(entity: SalesOrder): SalesOrderResource {
        val r = createResourceWithId(entity.state.customerId.toString(), entity)

        if (!entity.isEveryProductPaid()) {
            r.add(entityLinks.linkForSingleResource(r::class.java, entity.id.toString()).slash("payment").withRel("payment"))
        }

        if (!entity.isEveryProductDelivered()) {
            r.add(entityLinks.linkForSingleResource(r::class.java, entity.id.toString()).slash("delivery").withRel("delivery"))
        }

        return r
    }

    // overriding instantiateResource is required because by default ResourceAssemblerSupport uses non-arg constructor
    // to instantiate resource class. That forces using vars which is not nice.
    override fun instantiateResource(entity: SalesOrder): SalesOrderResource {
        val customerAssembler = CustomerResourceAssember()
        // in real life if we'd like to embedded customer resource we'd either
        // a) fetch it from repository if resource is local
        // b) fetch with HTTP if resource is remote
        // alternative to embedding is to add customer simply as link with custom rel, for example "customer"
        // Here as example we simply create ad-hoc customer "entity"
        val customer = Customer("Kimmo", "Eklund", entity.state.customerId.toString())
        val embeddables = mutableListOf<EmbeddedWrapper>()
        embeddables.add(customerAssembler.toEmbeddable(customer))
        return SalesOrderResource(Resources<EmbeddedWrapper>(embeddables), entity.state.customerId.toString(), entity.state.confirmed, entity.state.deleted, entity.state.products)
    }
}