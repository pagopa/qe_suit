package it.pagopa.interop.common.attribute.application;

import it.pagopa.interop.common.attribute.domain.Attribute;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.kernel.domain.AttributeRef;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.EServiceDescriptorRef;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import org.springframework.plugin.core.Plugin;

public interface AttributeGateway extends Plugin<Channel> {
    Attribute getAttribute(AttributeRef attributeRef);

    void removeCertifiedAttributeThreshold(EServiceRef eServiceRef, EServiceDescriptorRef eServiceDescriptorRef, EServiceDescriptorState state, int groupIndex, int attributeIndex);;

    void certifiedAttributeThresholdShouldBeRemoved();

    Attribute createCertifiedAttribute(Attribute attribute);
}
