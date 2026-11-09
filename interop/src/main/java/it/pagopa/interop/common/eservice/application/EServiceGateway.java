package it.pagopa.interop.common.eservice.application;

import it.pagopa.interop.common.eservice.application.command.EServiceCreationCommand;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceUpdateFailureReason;
import it.pagopa.interop.common.eservice.domain.EServiceCreationFailureReason;
import it.pagopa.interop.common.eservice.domain.EServiceArchivingFailureReason;
import it.pagopa.interop.common.eservice.domain.GracePeriodDays;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import org.springframework.plugin.core.Plugin;

public interface EServiceGateway extends Plugin<Channel> {
    void openEServiceCreation();

    void openDraftEServiceGeneralData(EService eService);

    void shouldFailToUpdateDraftEServiceDescription(String description, EServiceUpdateFailureReason reason);

    void shouldFailToUpdateDraftEServiceName(String name, EServiceUpdateFailureReason reason);

    void shouldFailToCreateEService(EServiceCreationCommand command, EServiceCreationFailureReason reason);

    EService createEService(EServiceCreationCommand command);

    void deleteEService(EService eService);

    EService getEService(EServiceRef eServiceRef);

    void archiveEService(EServiceRef eServiceRef, GracePeriodDays gracePeriodDays);

    void shouldFailToArchiveEService(EServiceRef eServiceRef, GracePeriodDays gracePeriodDays, EServiceArchivingFailureReason reason);

    void shouldFailToRequestEServiceArchiving(EServiceRef eServiceRef, GracePeriodDays gracePeriodDays, EServiceArchivingFailureReason reason);
}
