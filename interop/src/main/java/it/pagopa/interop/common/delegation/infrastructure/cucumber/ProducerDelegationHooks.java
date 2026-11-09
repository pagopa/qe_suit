package it.pagopa.interop.common.delegation.infrastructure.cucumber;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import it.pagopa.application.context.EntityStore;
import it.pagopa.infrastructure.channel.CurrentChannel;
import it.pagopa.interop.common.delegation.application.DelegationUseCase;
import it.pagopa.interop.common.delegation.domain.ProducerDelegation;
import it.pagopa.interop.common.delegation.domain.ProducerDelegationState;
import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class ProducerDelegationHooks {

    private final EntityStore entityStore;
    private final DelegationUseCase delegationUseCase;
    private final CurrentUserSession currentUserSession;
    private final CurrentChannel<Channel> currentChannel;

    private Set<UUID> preexistingDelegationIds;

    @Before(value = "@private-catalog", order = Integer.MIN_VALUE + 1)
    public void snapshotProducerDelegations() {
        preexistingDelegationIds = entityStore.getAll(ProducerDelegation.class).stream()
                .map(ProducerDelegation::getId)
                .collect(Collectors.toSet());
    }

    @After(value = "@private-catalog", order = 110)
    public void cleanUpProducerDelegations() {
        // Without a successful boundary snapshot, resource ownership cannot be established.
        if (preexistingDelegationIds == null) {
            return;
        }
        var delegations = entityStore.getAll(ProducerDelegation.class).stream()
                .filter(delegation -> !preexistingDelegationIds.contains(delegation.getId()))
                .toList();
        if (delegations.isEmpty()) {
            return;
        }
        var previousUser = currentUserSession.getUser();
        var previousTenant = currentUserSession.getTenant();
        var previousChannel = currentChannel.getCurrentChannel();
        var cleanupFailure = new IllegalStateException("Cleanup delle deleghe di erogazione non completato");
        try {
            currentChannel.setCurrentChannel(Channel.BFF);
            for (ProducerDelegation delegation : delegations) {
                try {
                    cleanUp(delegation);
                } catch (RuntimeException | AssertionError exception) {
                    log.error("Errore durante il cleanup della delega {}", delegation.getId(), exception);
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

    private void cleanUp(ProducerDelegation delegation) {
        actAs(delegation.getDelegatorId());
        ProducerDelegation current = delegationUseCase.getProducerDelegation(delegation.getRef());
        switch (current.getState()) {
            case ACTIVE -> {
                actAs(current.getDelegatorId());
                delegationUseCase.revokeProducerDelegation(current.getRef());
                delegationUseCase.waitForProducerDelegationState(
                        current.getRef(), ProducerDelegationState.REVOKED
                );
            }
            case WAITING_FOR_APPROVAL -> {
                actAs(current.getDelegateId());
                delegationUseCase.rejectProducerDelegation(current.getRef());
                delegationUseCase.waitForProducerDelegationState(
                        current.getRef(), ProducerDelegationState.REJECTED
                );
            }
            case REJECTED, REVOKED -> {
            }
        }
    }

    private void actAs(UUID tenantId) {
        Tenant tenant = Tenant.fromOrganizationId(tenantId);
        currentUserSession.set(User.getTenantAdmin(tenant), tenant);
    }
}
