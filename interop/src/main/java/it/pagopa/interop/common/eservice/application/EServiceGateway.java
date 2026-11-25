package it.pagopa.interop.common.eservice.application;

import it.pagopa.interop.common.eservice.application.command.EServiceCreationCommand;
import it.pagopa.interop.common.eservice.application.command.EServiceUpdateCommand;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceUpdateFailureReason;
import it.pagopa.interop.common.eservice.domain.EServiceCreationFailureReason;
import it.pagopa.interop.common.eservice.domain.EServiceArchivingFailureReason;
import it.pagopa.interop.common.eservice.domain.EServiceUpdateFailureReason;
import it.pagopa.interop.common.eservice.domain.GracePeriodDays;
import it.pagopa.interop.common.eservice.domain.SoapInterfaceUploadFailureReason;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.EServiceDescriptorRef;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import org.springframework.plugin.core.Plugin;

public interface EServiceGateway extends Plugin<Channel> {
    void openEServiceCreation();

    void openDraftEServiceGeneralData(EService eService);

    void shouldFailToUpdateDraftEServiceDescription(String description, EServiceUpdateFailureReason reason);

    void shouldFailToUpdateDraftEServiceName(String name, EServiceUpdateFailureReason reason);

    void shouldFailToCreateEService(EServiceCreationCommand command, EServiceCreationFailureReason reason);

    EService createEService(EServiceCreationCommand command);

    void tryUpdateEServiceMetadata(EServiceRef eServiceRef, EServiceUpdateCommand command);

    void updateEServiceMetadata(EServiceRef eServiceRef, EServiceUpdateCommand command);

    void shouldFailToUpdateEServiceMetadata(EServiceUpdateFailureReason reason);

    void deleteEService(EService eService);

    EService getEService(EServiceRef eServiceRef);

    void archiveEService(EServiceRef eServiceRef, GracePeriodDays gracePeriodDays);

    void cloneEService(EServiceRef eServiceRef, EServiceDescriptorRef descriptorRef);

    void shouldFailToArchiveEService(EServiceRef eServiceRef, GracePeriodDays gracePeriodDays, EServiceArchivingFailureReason reason);

    void shouldFailToRequestEServiceArchiving(EServiceRef eServiceRef, GracePeriodDays gracePeriodDays, EServiceArchivingFailureReason reason);

    void shouldFailToImportSOAPEService(
            String eserviceName,
            String wsdlResourcePath,
            SoapInterfaceUploadFailureReason reason
    );

    void assertEServiceNotPresentInProducerCatalog(String eserviceName);

    void attemptProducerEServiceDetailsRead(EServiceRef eServiceRef);

    void attemptNameUpdate(EServiceRef eServiceRef, String name);

    void attemptDelegatedArchivingRequest(EServiceRef eServiceRef, GracePeriodDays gracePeriodDays);

    void submitDelegatedArchivingRequest(EServiceRef eServiceRef, GracePeriodDays gracePeriodDays);

    void attemptCancelDelegatedArchivingRequest(EServiceRef eServiceRef);

    void attemptEServiceDeletion(EServiceRef eServiceRef);

    void attemptDelegationFlagsUpdate(EServiceRef eServiceRef);

    void attemptSignalHubUpdate(EServiceRef eServiceRef);

    void attemptPersonalDataFlagUpdate(EServiceRef eServiceRef);

    void attemptInstanceUpgrade(EServiceRef eServiceRef);

    void attemptDescriptionUpdate(EServiceRef eServiceRef, String description);

    void attemptPartialNameUpdate(EServiceRef eServiceRef, String name);

    void attemptEServiceImportFromFile(String filePath);
}
