package it.pagopa.interop.common.attribute.application;

import it.pagopa.interop.common.attribute.domain.Attribute;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.generated.openapi.clients.bff.model.AttributeSeed;
import org.springframework.plugin.core.Plugin;

import java.util.UUID;

public interface AttributeGateway extends Plugin<Channel> {
    Attribute getAttribute(UUID attributeId);

    Attribute createCertifiedAttribute(AttributeSeed attributeSeed);

}
