package it.pagopa.interop.common.eservice.infrastructure.cucumber;

import io.cucumber.java.After;
import it.pagopa.application.context.EntityStore;
import it.pagopa.infrastructure.channel.CurrentChannel;
import it.pagopa.interop.common.eservice.application.EServiceUseCase;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.eservice.domain.GracePeriodDays;
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
     * Deletes drafts and schedules retirement through BFF unless every version is already retiring or archived.
     * All cleanups are attempted; failures are propagated together.
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
                    boolean retirementAlreadyScheduled = !eservice.getDescriptors().isEmpty()
                            && eservice.getDescriptors().stream().allMatch(descriptor -> switch (descriptor.getState()) {
                                case ARCHIVING, ARCHIVING_SUSPENDED, ARCHIVED -> true;
                                default -> false;
                            });
                    if (retirementAlreadyScheduled) {
                        log.info("E-service gia' in archiviazione o archiviato, nessuna nuova richiesta: {}",
                                eservice.getId());
                        continue;
                    }
                    boolean draftOnly = eservice.getDescriptors().stream()
                            .allMatch(descriptor -> descriptor.getState() == EServiceDescriptorState.DRAFT);
                    if (draftOnly) {
                        eServiceUseCase.deleteEService(eservice);
                        log.info("E-service eliminato: {}", eservice.getId());
                    } else {
                        eServiceUseCase.archiveEService(eservice, GracePeriodDays.NUMBER_60);
                        log.info("Archiviazione e-service avviata: {}", eservice.getId());
                    }
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
