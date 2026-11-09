package it.pagopa.interop.common.attribute.infrastructure.cucumber;

import io.cucumber.java.After;
import it.pagopa.infrastructure.channel.CurrentChannel;
import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.tenant.application.TenantUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class CertifiedAttributeHooks {
    private final CertifiedAttributeFixtures fixtures;
    private final TenantUseCase tenantUseCase;
    private final CurrentUserSession currentUserSession;
    private final CurrentChannel<Channel> currentChannel;

    @After(value = "@private-catalog", order = 80)
    public void revokeScenarioAssignments() {
        var assignments = fixtures.assignments();
        if (assignments.isEmpty()) {
            return;
        }

        var previousUser = currentUserSession.getUser();
        var previousTenant = currentUserSession.getTenant();
        var previousChannel = currentChannel.getCurrentChannel();
        var cleanupFailure = new IllegalStateException("Cleanup degli attributi certificati non completato");
        try {
            currentChannel.setCurrentChannel(Channel.BFF);
            for (var assignment : assignments) {
                try {
                    Tenant issuer = Tenant.fromOrganizationId(assignment.issuer().id());
                    currentUserSession.set(User.getTenantAdmin(issuer), issuer);
                    tenantUseCase.revokeCertifiedAttribute(assignment.tenant(), assignment.attribute());
                } catch (RuntimeException | AssertionError exception) {
                    log.error("Errore durante la revoca dell'attributo {} dal tenant {}",
                            assignment.attribute().id(), assignment.tenant().id(), exception);
                    cleanupFailure.addSuppressed(exception);
                }
            }
        } catch (RuntimeException | AssertionError exception) {
            cleanupFailure.addSuppressed(exception);
        } finally {
            try {
                currentUserSession.set(previousUser, previousTenant);
            } catch (RuntimeException | AssertionError exception) {
                cleanupFailure.addSuppressed(exception);
            } finally {
                try {
                    currentChannel.setCurrentChannel(previousChannel);
                } catch (RuntimeException | AssertionError exception) {
                    cleanupFailure.addSuppressed(exception);
                }
            }
        }
        if (cleanupFailure.getSuppressed().length > 0) {
            throw cleanupFailure;
        }
    }
}
