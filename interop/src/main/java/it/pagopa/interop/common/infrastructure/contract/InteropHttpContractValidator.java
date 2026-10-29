package it.pagopa.interop.common.infrastructure.contract;

import it.pagopa.infrastructure.contract.http.*;
import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.m2m.kernel.context.CurrentM2MSession;
import it.pagopa.interop.m2m.kernel.domain.M2MRole;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class InteropHttpContractValidator {
    private final HttpContractValidator delegate;
    private final CurrentUserSession currentUserSession;
    private final CurrentM2MSession currentM2MSession;

    public InteropHttpContractValidator(
            HttpContractValidator delegate,
            CurrentUserSession currentUserSession,
            CurrentM2MSession currentM2MSession
    ) {
        this.delegate = Objects.requireNonNull(delegate, "delegate must not be null");
        this.currentUserSession = Objects.requireNonNull(currentUserSession, "currentUserSession must not be null");
        this.currentM2MSession = Objects.requireNonNull(currentM2MSession, "currentM2MSession must not be null");
    }

    public HttpContractStages.ApiCallSelectionStage as(Tenant tenant, User user) {
        Objects.requireNonNull(tenant, "tenant must not be null");
        Objects.requireNonNull(user, "user must not be null");

        HttpContractAuthentication authentication = () -> currentUserSession.set(user, tenant);
        return operationSupplier -> delegate.apiCall(authentication, operationSupplier);
    }

    public HttpContractStages.ApiCallSelectionStage as(Tenant tenant, UserRole role) {
        Objects.requireNonNull(tenant, "tenant must not be null");
        Objects.requireNonNull(role, "role must not be null");
        return as(tenant, User.getTenantUser(tenant, role));
    }

    public HttpContractStages.ApiCallSelectionStage as(Tenant tenant, M2MRole m2MRole) {
        Objects.requireNonNull(tenant, "tenant must not be null");
        Objects.requireNonNull(m2MRole, "m2MRole must not be null");

        HttpContractAuthentication authentication = () -> currentM2MSession.set(tenant, m2MRole);
        return operationSupplier -> delegate.apiCall(authentication, operationSupplier);
    }

    /**
     * Test di autorizzazione (utenza x API, default 403) per utenti BFF dello stesso tenant.
     */
    public HttpAuthorizationStages.ApiCallsStage as(Tenant tenant, List<User> users) {
        Objects.requireNonNull(tenant, "tenant must not be null");
        Objects.requireNonNull(users, "users must not be null");
        return as(users.stream().map(user -> InteropContractIdentity.bff(tenant, user)));
    }

    public HttpAuthorizationStages.ApiCallsStage as(Tenant tenant, Stream<User> users) {
        Objects.requireNonNull(tenant, "tenant must not be null");
        Objects.requireNonNull(users, "users must not be null");
        return as(users.map(user -> InteropContractIdentity.bff(tenant, user)));
    }

    /**
     * Test di autorizzazione per utenze con tenant differenti, BFF o M2M.
     */
    public HttpAuthorizationStages.ApiCallsStage as(List<InteropContractIdentity> identities) {
        Objects.requireNonNull(identities, "identities must not be null");
        return as(identities.stream());
    }

    public HttpAuthorizationStages.ApiCallsStage as(Stream<InteropContractIdentity> identities) {
        Objects.requireNonNull(identities, "identities must not be null");
        return delegate.as(identities.map(this::toHttpIdentity));
    }

    private HttpContractIdentity toHttpIdentity(InteropContractIdentity identity) {
        Objects.requireNonNull(identity, "identity must not be null");
        HttpContractAuthentication authentication = identity.user() != null
                ? () -> currentUserSession.set(identity.user(), identity.tenant())
                : () -> currentM2MSession.set(identity.tenant(), identity.m2mRole());
        return new HttpContractIdentity(identity.label(), authentication);
    }
}
