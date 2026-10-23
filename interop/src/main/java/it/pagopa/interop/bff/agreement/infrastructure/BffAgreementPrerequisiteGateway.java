package it.pagopa.interop.bff.agreement.infrastructure;

import it.pagopa.infrastructure.template.action.strategy.PollingStrategy;
import it.pagopa.interop.common.agreement.application.AgreementGateway;
import it.pagopa.interop.common.agreement.domain.Agreement;
import it.pagopa.interop.common.agreement.domain.AgreementState;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import it.pagopa.interop.common.kernel.domain.AgreementRef;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class BffAgreementPrerequisiteGateway {
    private static final int PAGE_SIZE = 50;
    private final BffAgreementRestClient restClient;
    private final CurrentUserSession session;

    public void ensureActiveAgreement(EService service, EServiceDescriptor descriptor, AgreementGateway agreements) {
        var originalUser = session.getUser();
        var originalTenant = session.getTenant();
        Throwable primaryFailure = null;

        try {
            Tenant producer = Tenant.fromOrganizationId(java.util.UUID.fromString(service.getProducerId()));
            session.set(User.getTenantAdmin(producer), producer);

            if (hasActiveAgreement(service, descriptor, agreements)) return;

            Agreement created = agreements.createAgreement(service, descriptor, null);
            Agreement submitted = agreements.submitAgreement(created);
            Agreement resolved = agreements.awaitState(submitted, Set.of(AgreementState.ACTIVE, AgreementState.PENDING));

            if (resolved.getState() == AgreementState.PENDING)
                resolved = agreements.activateAgreement(resolved, null);

            resolved = agreements.awaitState(resolved, Set.of(AgreementState.ACTIVE));

            if (!matches(resolved, service, descriptor) || !producer.getOrganizationId().equals(resolved.getConsumerId()))
                throw new IllegalStateException("Self-consumer agreement is not ACTIVE for descriptor " + descriptor.getId());

        } catch (RuntimeException | Error failure) {
            primaryFailure = failure;
            throw failure;
        } finally {
            try {
                session.set(originalUser, originalTenant);
            } catch (RuntimeException | Error restorationFailure) {
                if (primaryFailure == null) throw restorationFailure;
                if (primaryFailure != restorationFailure) primaryFailure.addSuppressed(restorationFailure);
            }
        }
    }

    private boolean hasActiveAgreement(EService service, EServiceDescriptor descriptor, AgreementGateway agreements) {
        for (int offset = 0; ; offset += PAGE_SIZE) {

            var page = restClient.activeProducerAgreements(service.getId(), offset, PAGE_SIZE)
                    .withPolling(PollingStrategy.UNTIL_SUCCESS)
                    .get();

            if (page == null || page.getPagination().getTotalCount() < 0 || page.getResults().size() > PAGE_SIZE)
                throw new IllegalStateException("Invalid producer agreement page for e-service " + service.getId());

            for (var entry : page.getResults()) {
                if (entry == null)
                    throw new IllegalStateException("Producer agreement entry missing identity or descriptor");

                if (descriptor.getId().equals(entry.getDescriptor().getId())
                        && matches(agreements.getAgreement(new AgreementRef(entry.getId())), service, descriptor)) {
                    return true;
                }
            }
            if (offset + page.getResults().size() >= page.getPagination().getTotalCount()) return false;
            if (page.getResults().size() != PAGE_SIZE)
                throw new IllegalStateException("Incomplete producer agreement page at offset " + offset);
        }
    }

    private boolean matches(Agreement agreement, EService service, EServiceDescriptor descriptor) {
        return agreement != null && agreement.getState() == AgreementState.ACTIVE
                && service.getId().equals(agreement.getEserviceId())
                && descriptor.getId().equals(agreement.getDescriptorId());
    }
}
