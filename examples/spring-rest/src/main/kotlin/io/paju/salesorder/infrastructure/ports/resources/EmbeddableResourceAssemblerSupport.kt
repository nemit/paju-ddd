package io.paju.salesorder.infrastructure.ports.resources

import org.springframework.hateoas.ResourceSupport
import org.springframework.hateoas.core.EmbeddedWrapper
import org.springframework.hateoas.core.EmbeddedWrappers
import org.springframework.hateoas.mvc.ResourceAssemblerSupport
import kotlin.streams.toList

abstract class EmbeddableResourceAssemblerSupport<T, D: ResourceSupport, C>(controllerClass: Class<C>, resourceType: Class<D>): ResourceAssemblerSupport<T, D>(controllerClass, resourceType) {

    fun toEmbeddable(entities: Iterable<T>): List<EmbeddedWrapper> {
        val wrapper = EmbeddedWrappers(true) // Prefer collection
        val resources = toResources(entities)
        return resources.stream().map { a -> wrapper.wrap(a) }.toList()
    }

    fun toEmbeddable(entity: T): EmbeddedWrapper {
        val wrapper = EmbeddedWrappers(false)
        val resource = toResource(entity)
        return wrapper.wrap(resource)
    }

}




