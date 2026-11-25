package it.pagopa.interop.common.eservice.infrastructure.cucumber;

import io.cucumber.java.After;
import it.pagopa.application.context.EntityStore;
import it.pagopa.infrastructure.channel.CurrentChannel;
import it.pagopa.interop.common.eservice.application.EServiceDescriptorApprovalUseCase;
import it.pagopa.interop.common.eservice.application.EServiceDescriptorUseCase;
import it.pagopa.interop.common.eservice.application.EServiceUseCase;
import it.pagopa.interop.common.eservice.application.EServiceRequestFactory;
import it.pagopa.utils.RandomUtils;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.eservice.domain.GracePeriodDays;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.kernel.context.AuthSession;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class EServiceHooks {
    private final EntityStore entityStore;
    private final EServiceUseCase eServiceUseCase;
    private final EServiceRequestFactory requestFactory;
    private final EServiceDescriptorApprovalUseCase descriptorUseCase;
    private final EServiceDescriptorUseCase descriptorReader;
    private final AuthSession authSession;
    private final CurrentChannel<Channel> currentChannel;

    /**
     * Rejects pending versions, deletes drafts and schedules retirement through BFF.
     * All cleanups are attempted; failures are propagated together.
     */
    @After(value = "@private-catalog or @clean-up", order = 100)
    public void cleanUpEServices() {
        var eservices = entityStore.getAll(EService.class);
        if (eservices.isEmpty()) {
            return;
        }

        var templateInstanceIds = entityStore.getAll(EServiceTemplate.class).stream()
                .flatMap(template -> template.getInstances().stream())
                .map(instance -> instance.eserviceRef().id())
                .collect(Collectors.toSet());
        var previousUser = authSession.getActor();
        var previousTenant = authSession.getTenant();
        var previousChannel = currentChannel.getCurrentChannel();
        var cleanupFailure = new IllegalStateException("Cleanup degli e-service non completato");

        try {
            currentChannel.setCurrentChannel(Channel.BFF);
            for (EService eservice : eservices) {
                try {
                    Tenant producer = Tenant.fromOrganizationId(UUID.fromString(eservice.getProducerId()));
                    authSession.set(producer);
                    var descriptors = eservice.getDescriptors().stream()
                            .map(descriptor -> descriptor.getState() == EServiceDescriptorState.WAITING_FOR_APPROVAL
                                    ? descriptorReader.getDescriptor(eservice, descriptor) : descriptor)
                            .map(descriptor -> descriptor.getState() == EServiceDescriptorState.WAITING_FOR_APPROVAL
                                    ? descriptorUseCase.rejectDescriptor(eservice, descriptor, "Cleanup dello scenario")
                                    : descriptor)
                            .toList();
                    boolean retirementAlreadyScheduled = !descriptors.isEmpty()
                            && descriptors.stream().allMatch(descriptor -> switch (descriptor.getState()) {
                                case ARCHIVING, ARCHIVING_SUSPENDED, ARCHIVED -> true;
                                default -> false;
                            });
                    boolean draftOnly = descriptors.stream()
                            .allMatch(descriptor -> descriptor.getState() == EServiceDescriptorState.DRAFT);
                    if (draftOnly && !retirementAlreadyScheduled) {
                        eServiceUseCase.deleteEService(eservice);
                        log.info("E-service eliminato: {}", eservice.getId());
                    } else {
                        // Archiving does not release (producer, name). Rename first to make
                        // literal fixture names reusable in the next scenario execution.
                        // A failed rename must not prevent retirement from being attempted.

                        if (templateInstanceIds.contains(eservice.getId())) {
                            // Il nome dell'istanza viene propagato dal template padre.
                            log.info("Istanza di template {}: nessuna rinomina diretta", eservice.getId());
                        } else {
                            try {
                                var command = requestFactory.defaultUpdateEServiceCommand()
                                        .name(RandomUtils.randomAlphanumericName("cleanup-eservice", 20));
                                eServiceUseCase.updateEServiceMetadata(eservice, command);
                            } catch (RuntimeException | AssertionError exception) {
                                log.error("Impossibile rinominare l'e-service {} durante il cleanup", eservice.getId(), exception);
                                cleanupFailure.addSuppressed(exception);
                            }
                        }
                        if (!retirementAlreadyScheduled) {
                            eServiceUseCase.archiveEService(eservice, GracePeriodDays.NUMBER_60);
                            log.info("Archiviazione e-service avviata: {}", eservice.getId());
                        } else {
                            log.info("E-service gia' in archiviazione o archiviato: {}", eservice.getId());
                        }
                    }
                } catch (RuntimeException | AssertionError exception) {
                    log.error("Errore durante il cleanup dell'e-service {}", eservice.getId(), exception);
                    cleanupFailure.addSuppressed(exception);
                }
            }
        } finally {
            currentChannel.setCurrentChannel(previousChannel);
            authSession.set(previousUser, previousTenant);
        }

        if (cleanupFailure.getSuppressed().length > 0) {
            throw cleanupFailure;
        }
    }
}
