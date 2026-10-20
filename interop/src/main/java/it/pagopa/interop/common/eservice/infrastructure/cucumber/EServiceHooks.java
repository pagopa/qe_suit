package it.pagopa.interop.common.eservice.infrastructure.cucumber;

import io.cucumber.java.After;
import it.pagopa.application.context.EntityStore;
import it.pagopa.infrastructure.channel.CurrentChannel;
import it.pagopa.interop.common.eservice.application.EServiceUseCase;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class EServiceHooks {
    private final EntityStore entityStore;
    private final EServiceUseCase eServiceUseCase;
    private final CurrentUserSession currentUserSession;
    private final CurrentChannel<Channel> currentChannel;

    /**
     * Deletes the scenario's stored e-services through BFF as their respective producers.
     * All deletions are attempted; failures are propagated together without an archive fallback.
     */
    @After(value = "@private-catalog", order = 100)
    public void cleanUpEServices() {
        var eservices = entityStore.getAll(EService.class);
        if (eservices.isEmpty()) {
            return;
        }

        var previousUser = currentUserSession.getUser();
        var previousTenant = currentUserSession.getTenant();
        var previousChannel = currentChannel.getCurrentChannel();
        var cleanupFailure = new IllegalStateException("Cleanup degli e-service non completato");

        try {
            currentChannel.setCurrentChannel(Channel.BFF);
            for (EService eservice : eservices) {
                try {
                    Tenant producer = Tenant.fromOrganizationId(UUID.fromString(eservice.getProducerId()));
                    currentUserSession.set(User.getTenantAdmin(producer), producer);
                    eServiceUseCase.deleteEService(eservice);
                    log.info("E-service eliminato: {}", eservice.getId());
                } catch (RuntimeException | AssertionError exception) {
                    log.error("Errore durante il cleanup dell'e-service {}", eservice.getId(), exception);
                    cleanupFailure.addSuppressed(exception);
                }
            }
        } finally {
            currentUserSession.set(previousUser, previousTenant);
            currentChannel.setCurrentChannel(previousChannel);
        }

        if (cleanupFailure.getSuppressed().length > 0) {
            throw cleanupFailure;
        }
    }
}
