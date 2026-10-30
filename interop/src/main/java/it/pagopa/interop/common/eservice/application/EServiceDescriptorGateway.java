package it.pagopa.interop.common.eservice.application;

import it.pagopa.interop.common.eservice.application.command.UpdateEServiceDescriptorCommand;
import it.pagopa.interop.common.eservice.domain.*;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.EServiceDescriptorRef;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import org.springframework.plugin.core.Plugin;

import java.util.List;

public interface EServiceDescriptorGateway extends Plugin<Channel> {

    EServiceDescriptor getEServiceDescriptor(EServiceRef eServiceRef, EServiceDescriptorRef descriptorRef);

    EServiceDescriptor addDescriptor(EServiceRef eServiceRef);

    EServiceDescriptor publishDescriptor(EServiceRef eServiceRef, EServiceDescriptorRef descriptorRef);

    EServiceDescriptor updateDescriptor(EServiceRef eServiceRef, EServiceDescriptorRef descriptorRef, UpdateEServiceDescriptorCommand command);

    EServiceDescriptor linkOpenApiInterface(EServiceRef eServiceRef, EServiceDescriptorRef descriptorRef, String openApiInterfacePath, String name);

    EServiceDescriptor linkOpenApiCallbackInterface(EServiceRef eServiceRef, EServiceDescriptorRef descriptorRef, String openApiInterfacePath, String name);

    void archiveDescriptor(EServiceRef eServiceRef, EServiceDescriptorRef descriptorRef, GracePeriodDays gracePeriodDays);

    EServiceDescriptor configureTemplateInstanceSOAPInterface(
            EServiceRef eServiceRef,
            EServiceDescriptorRef descriptorRef,
            List<SoapAddressConfiguration> addresses
    );

    void shouldFailToConfigureSOAPInterface(
            EServiceRef eServiceRef,
            EServiceDescriptorRef descriptorRef,
            List<SoapAddressConfiguration> addresses,
            EServiceTemplateSOAPInterfaceFailureReason reason
    );

    EServiceDescriptor configureTemplateInstanceRESTInterface(
            EServiceRef eServiceRef,
            EServiceDescriptorRef descriptorRef,
            RestInterfaceConfiguration configuration
    );

    EServiceDescriptor downloadInterface(
            EServiceRef eServiceRef,
            EServiceDescriptorRef descriptorRef
    );

    java.util.Optional<EServiceDescriptorInterface> readInterfaceSnapshot(
            EServiceRef eServiceRef,
            EServiceDescriptorRef descriptorRef
    );

    java.util.Optional<EServiceDescriptorInterface> readInterfaceSnapshotAfterDelay(
            EServiceRef eServiceRef,
            EServiceDescriptorRef descriptorRef
    );
}
