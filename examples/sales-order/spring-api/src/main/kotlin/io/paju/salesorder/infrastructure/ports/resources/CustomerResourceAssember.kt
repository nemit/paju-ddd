package io.paju.salesorder.infrastructure.ports.resources

import io.paju.salesorder.infrastructure.ports.controllers.CustomerController

class CustomerResourceAssember : EmbeddableResourceAssemblerSupport<Customer, CustomerResource, CustomerController>(CustomerController::class.java, CustomerResource::class.java) {

    override fun toResource(entity: Customer): CustomerResource {
        return createResourceWithId(entity.id, entity)
    }

    // by default Spinrg uses no-arg constructor, but it can be changed by overriding this function
    override fun instantiateResource(entity: Customer): CustomerResource {
        return CustomerResource(entity.firstName, entity.lastName, entity.id)
    }
}