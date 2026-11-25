package it.pagopa.interop.common.eservice.application;

import it.pagopa.interop.common.eservice.application.command.UpdateEServiceDescriptorCommand;
import it.pagopa.interop.common.eservice.domain.*;
import it.pagopa.interop.common.kernel.domain.ApiInterface;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.Document;
import it.pagopa.interop.common.kernel.domain.DocumentKind;
import it.pagopa.interop.common.kernel.domain.EServiceDescriptorRef;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import org.springframework.plugin.core.Plugin;

import java.util.List;

public interface EServiceDescriptorGateway extends Plugin<Channel> {

    EServiceDescriptor getEServiceDescriptor(EServiceRef eServiceRef, EServiceDescriptorRef descriptorRef);

    EServiceDescriptor awaitDescriptorState(
            EServiceRef eServiceRef, EServiceDescriptorRef descriptorRef, EServiceDescriptorState expectedState
    );

    EServiceDescriptor addDescriptor(EServiceRef eServiceRef);

    EServiceDescriptor addDocument(EServiceRef eServiceRef, EServiceDescriptorRef descriptorRef, String path, String name);

    EServiceDescriptor publishDescriptor(EServiceRef eServiceRef, EServiceDescriptorRef descriptorRef);

    EServiceDescriptor publishDescriptorAwaitingApproval(
            EServiceRef eServiceRef, EServiceDescriptorRef descriptorRef
    );

    EServiceDescriptor suspendDescriptor(EServiceRef eServiceRef, EServiceDescriptorRef descriptorRef);

    EServiceDescriptor updateDescriptor(EServiceRef eServiceRef, EServiceDescriptorRef descriptorRef, UpdateEServiceDescriptorCommand command);

    EServiceDescriptor linkOpenApiInterface(EServiceRef eServiceRef, EServiceDescriptorRef descriptorRef, ApiInterface apiInterface);

    void shouldFailToUploadInterface(
            EServiceRef eServiceRef,
            EServiceDescriptorRef descriptorRef,
            String interfaceResourcePath,
            SoapInterfaceUploadFailureReason reason
    );

    EServiceDescriptor linkOpenApiCallbackInterface(EServiceRef eServiceRef, EServiceDescriptorRef descriptorRef, ApiInterface apiInterface);

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

    void deleteDraftVersion(EServiceRef eServiceRef, EServiceDescriptorRef descriptorRef);

    void attemptProducerDescriptorDetailsRead(EServiceRef eServiceRef, EServiceDescriptorRef descriptorRef);

    void attemptDescriptorExport(EServiceRef eServiceRef, EServiceDescriptorRef descriptorRef);

    void attemptValidDocumentUpload(EServiceRef eServiceRef, EServiceDescriptorRef descriptorRef);

    void attemptDocumentUpload(EServiceRef eServiceRef, EServiceDescriptorRef descriptorRef, String path, String name);

    void attemptFileUpload(
            EServiceRef eServiceRef,
            EServiceDescriptorRef descriptorRef,
            String path,
            DocumentKind kind,
            String name
    );

    void attemptDocumentRename(EServiceRef eServiceRef, EServiceDescriptorRef descriptorRef, Document document, String newName);

    void attemptDocumentDeletion(EServiceRef eServiceRef, EServiceDescriptorRef descriptorRef, Document document);

    void attemptDescriptorCreation(EServiceRef eServiceRef);

    void attemptDraftVersionDeletion(EServiceRef eServiceRef, EServiceDescriptorRef descriptorRef);

    void submitDelegatedArchivingRequest(
            EServiceRef eServiceRef, EServiceDescriptorRef descriptorRef, GracePeriodDays gracePeriodDays
    );

    void attemptDelegatedArchivingRequest(
            EServiceRef eServiceRef, EServiceDescriptorRef descriptorRef, GracePeriodDays gracePeriodDays
    );

    void attemptCancelDelegatedArchivingRequest(EServiceRef eServiceRef, EServiceDescriptorRef descriptorRef);

    void attemptDescriptorConfigurationUpdate(EService eService, EServiceDescriptor descriptor);

    void attemptDescriptorPublication(EServiceRef eServiceRef, EServiceDescriptorRef descriptorRef);

    void attemptDescriptorSuspension(EServiceRef eServiceRef, EServiceDescriptorRef descriptorRef);

    void attemptDescriptorActivation(EServiceRef eServiceRef, EServiceDescriptorRef descriptorRef);

    void attemptAgreementApprovalPolicyUpdate(EServiceRef eServiceRef, EServiceDescriptorRef descriptorRef);

    void attemptAttributesUpdate(EServiceRef eServiceRef, EServiceDescriptorRef descriptorRef);

    void attemptTemplateInstanceSOAPInterfaceConfiguration(
            EServiceRef eServiceRef, EServiceDescriptorRef descriptorRef, List<SoapAddressConfiguration> addresses
    );

    void attemptTemplateInstanceRESTInterfaceConfiguration(
            EServiceRef eServiceRef, EServiceDescriptorRef descriptorRef, RestInterfaceConfiguration configuration
    );
}
