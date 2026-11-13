package it.pagopa.interop.common.eservice.application;

import it.pagopa.interop.common.eservice.application.command.EServiceCreationCommand;
import it.pagopa.interop.common.eservice.application.command.EServiceUpdateCommand;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceArchivingFailureReason;
import it.pagopa.interop.common.eservice.domain.GracePeriodDays;
import it.pagopa.interop.common.eservice.domain.SoapInterfaceUploadFailureReason;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import org.springframework.plugin.core.Plugin;

public interface EServiceGateway extends Plugin<Channel> {
    EService createEService(EServiceCreationCommand command);

    void updateEService(EServiceRef eServiceRef, EServiceUpdateCommand command);

    void shouldFailToUpdateEService(EServiceRef eServiceRef, EServiceUpdateCommand command);

    void deleteEService(EService eService);

    EService getEService(EServiceRef eServiceRef);

    void archiveEService(EServiceRef eServiceRef, GracePeriodDays gracePeriodDays);

    void shouldFailToArchiveEService(EServiceRef eServiceRef, GracePeriodDays gracePeriodDays, EServiceArchivingFailureReason reason);

    void shouldFailToRequestEServiceArchiving(EServiceRef eServiceRef, GracePeriodDays gracePeriodDays, EServiceArchivingFailureReason reason);

    void shouldFailToImportSOAPEService(
            String eserviceName,
            String wsdlResourcePath,
            SoapInterfaceUploadFailureReason reason
    );

    void assertEServiceNotPresentInProducerCatalog(String eserviceName);
}
