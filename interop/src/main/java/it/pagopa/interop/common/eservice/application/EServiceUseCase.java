package it.pagopa.interop.common.eservice.application;

import it.pagopa.interop.common.eservice.application.command.EServiceCreationCommand;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceUpdateFailureReason;
import it.pagopa.interop.common.eservice.domain.EServiceCreationFailureReason;
import it.pagopa.interop.common.eservice.domain.EServiceArchivingFailureReason;
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

    public void deleteEService(EService eService) {
        eServiceGateway.deleteEService(eService);
    }

    public EService getEService(EService eService) {
        return eServiceGateway.getEService(eService.getRef());
    }

    public void archiveEService(EService eService, GracePeriodDays gracePeriodDays) {
        eServiceGateway.archiveEService(eService.getRef(), gracePeriodDays);
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
}
