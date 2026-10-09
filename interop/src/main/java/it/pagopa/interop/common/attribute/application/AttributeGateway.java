package it.pagopa.interop.common.attribute.application;

import it.pagopa.interop.common.attribute.domain.Attribute;
import it.pagopa.interop.common.kernel.domain.AttributeRef;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.generated.openapi.clients.bff.model.AttributeSeed;
import org.springframework.plugin.core.Plugin;


public interface AttributeGateway extends Plugin<Channel> {
    Attribute getAttribute(AttributeRef attributeRef);

    Attribute createCertifiedAttribute(AttributeSeed attributeSeed);

}
