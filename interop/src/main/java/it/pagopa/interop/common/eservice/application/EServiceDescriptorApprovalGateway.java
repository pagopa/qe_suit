package it.pagopa.interop.common.eservice.application;

import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.EServiceDescriptorRef;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import org.springframework.plugin.core.Plugin;

public interface EServiceDescriptorApprovalGateway extends Plugin<Channel> {
    EServiceDescriptor rejectDescriptor(
            EServiceRef serviceRef, EServiceDescriptorRef descriptorRef, String reason
    );
}
