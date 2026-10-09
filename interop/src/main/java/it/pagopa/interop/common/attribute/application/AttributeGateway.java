package it.pagopa.interop.common.attribute.application;

import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.EServiceDescriptorRef;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import org.springframework.plugin.core.Plugin;

public interface AttributeGateway extends Plugin<Channel> {

    void removeCertifiedAttributeThresholdFromDraftEService(EServiceRef eServiceRef, EServiceDescriptorRef eServiceDescriptorRef, int groupIndex, int attributeIndex);

    void removeCertifiedAttributeThresholdFromPublishedEService(EServiceRef eServiceRef, EServiceDescriptorRef eServiceDescriptorRef, int groupIndex, int attributeIndex);

    void certifiedAttributeThresholdRemoved();
}
