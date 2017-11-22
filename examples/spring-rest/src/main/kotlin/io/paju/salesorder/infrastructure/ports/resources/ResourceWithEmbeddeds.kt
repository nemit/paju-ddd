package io.paju.salesorder.infrastructure.ports.resources

import org.springframework.hateoas.ResourceSupport
import org.springframework.hateoas.Resources
import org.springframework.hateoas.core.EmbeddedWrapper

abstract class ResourceWithEmbeddeds: ResourceSupport() {

    abstract val embeddeds: Resources<EmbeddedWrapper>
}