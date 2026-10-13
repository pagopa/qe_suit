package it.pagopa.interop.common.eservice.application;

import it.pagopa.interop.common.eservice.application.command.EServiceCreationCommand;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.GracePeriodDays;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import org.springframework.plugin.core.Plugin;

public interface EServiceGateway extends Plugin<Channel> {
    EService createEService(EServiceCreationCommand command);

    default EService createEServiceInstanceFromTemplate(EServiceTemplate template) {
        throw new UnsupportedOperationException("Creazione da template disponibile soltanto nel canale BFF");
    }

    default void deleteEService(EService eService) {
        throw new UnsupportedOperationException("Eliminazione E-service disponibile soltanto nel canale BFF");
    }

    EService getEService(EServiceRef eServiceRef);

    void archiveEService(EServiceRef eServiceRef, GracePeriodDays gracePeriodDays);
}
