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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class EServiceUseCase {
    private final EServiceGateway eServiceGateway;
    private final EServiceRequestFactory requestFactory;

    public EService createEService(Consumer<EServiceCreationCommand> config) {
        EServiceCreationCommand creationCommand = requestFactory.defaultCreationEServiceCommand();
        config.accept(creationCommand);

        return createEService(creationCommand);
    }

    public EService createEService(EServiceCreationCommand creationCommand) {
        return eServiceGateway.createEService(creationCommand);
    }

    public void tryUpdateEServiceMetadata(EService eService, EServiceUpdateCommand command) {
        eServiceGateway.tryUpdateEServiceMetadata(eService.getRef(), command);
    }

    public void updateEServiceMetadata(EService eService, EServiceUpdateCommand command) {
        eServiceGateway.updateEServiceMetadata(eService.getRef(), command);
    }

    public void shouldFailToUpdateEServiceMetadata(EServiceUpdateFailureReason reason) {
        eServiceGateway.shouldFailToUpdateEServiceMetadata(reason);
    }

    public void deleteEService(EService eService) {
        eServiceGateway.deleteEService(eService);
    }

    public EService getEService(EService eService) {
        return getEService(eService.getRef());
    }

    public EService getEService(it.pagopa.interop.common.kernel.domain.EServiceRef eServiceRef) {
        return eServiceGateway.getEService(eServiceRef);
    }

    public void archiveEService(EService eService, GracePeriodDays gracePeriodDays) {
        eServiceGateway.archiveEService(eService.getRef(), gracePeriodDays);
    }

    public void cloneEService(EService eService) {
        eServiceGateway.cloneEService(eService.getRef(), eService.getLatestDescriptor().getRef());
    }

    public void shouldFailToArchiveEService(EService eService, GracePeriodDays gracePeriodDays, EServiceArchivingFailureReason reason) {
        eServiceGateway.shouldFailToArchiveEService(eService.getRef(), gracePeriodDays, reason);
    }

    public void shouldFailToRequestEServiceArchiving(EService eService, GracePeriodDays gracePeriodDays, EServiceArchivingFailureReason reason) {
        eServiceGateway.shouldFailToRequestEServiceArchiving(eService.getRef(), gracePeriodDays, reason);
    }

    public void shouldFailToImportSOAPEService(
            String eserviceName,
            String wsdlResourcePath,
            SoapInterfaceUploadFailureReason reason
    ) {
        eServiceGateway.shouldFailToImportSOAPEService(eserviceName, wsdlResourcePath, reason);
    }

    public void assertEServiceNotPresentInProducerCatalog(String eserviceName) {
        eServiceGateway.assertEServiceNotPresentInProducerCatalog(eserviceName);
    }

    public void openEServiceCreation() {
        eServiceGateway.openEServiceCreation();
    }

    public void openDraftEServiceGeneralData(EService eService) {
        eServiceGateway.openDraftEServiceGeneralData(eService);
    }

    public void shouldFailToUpdateDraftEServiceDescription(String description, EServiceUpdateFailureReason reason) {
        eServiceGateway.shouldFailToUpdateDraftEServiceDescription(description, reason);
    }

    public void shouldFailToUpdateDraftEServiceName(String name, EServiceUpdateFailureReason reason) {
        eServiceGateway.shouldFailToUpdateDraftEServiceName(name, reason);
    }

    public void shouldFailToCreateEService(Consumer<EServiceCreationCommand> config, EServiceCreationFailureReason reason) {
        EServiceCreationCommand command = requestFactory.defaultCreationEServiceCommand();
        config.accept(command);
        shouldFailToCreateEService(command, reason);
    }

    public void shouldFailToCreateEService(EServiceCreationCommand command, EServiceCreationFailureReason reason) {
        eServiceGateway.shouldFailToCreateEService(command, reason);
    }

    public void attemptProducerEServiceDetailsRead(EService eService) {
        eServiceGateway.attemptProducerEServiceDetailsRead(eService.getRef());
    }

    public void attemptNameUpdate(EService eService, String name) {
        eServiceGateway.attemptNameUpdate(eService.getRef(), name);
    }

    public void attemptDelegatedArchivingRequest(EService eService, GracePeriodDays gracePeriodDays) {
        eServiceGateway.attemptDelegatedArchivingRequest(eService.getRef(), gracePeriodDays);
    }

    public void submitDelegatedArchivingRequest(EService eService, GracePeriodDays gracePeriodDays) {
        eServiceGateway.submitDelegatedArchivingRequest(eService.getRef(), gracePeriodDays);
    }

    public void attemptCancelDelegatedArchivingRequest(EService eService) {
        eServiceGateway.attemptCancelDelegatedArchivingRequest(eService.getRef());
    }

    public void attemptEServiceDeletion(EService eService) {
        eServiceGateway.attemptEServiceDeletion(eService.getRef());
    }

    public void attemptDelegationFlagsUpdate(EService eService) {
        eServiceGateway.attemptDelegationFlagsUpdate(eService.getRef());
    }

    public void attemptSignalHubUpdate(EService eService) {
        eServiceGateway.attemptSignalHubUpdate(eService.getRef());
    }

    public void attemptPersonalDataFlagUpdate(EService eService) {
        eServiceGateway.attemptPersonalDataFlagUpdate(eService.getRef());
    }

    public void attemptInstanceUpgrade(EService eService) {
        eServiceGateway.attemptInstanceUpgrade(eService.getRef());
    }

    public void attemptDescriptionUpdate(EService eService, String description) {
        eServiceGateway.attemptDescriptionUpdate(eService.getRef(), description);
    }

    public void attemptPartialNameUpdate(EService eService, String name) {
        eServiceGateway.attemptPartialNameUpdate(eService.getRef(), name);
    }

    public void attemptEServiceImportFromFile(String filePath) {
        if (filePath == null || filePath.isBlank()) {
            throw new IllegalArgumentException("Import file path must not be blank");
        }
        eServiceGateway.attemptEServiceImportFromFile(filePath);
    }
}
