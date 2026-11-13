package it.pagopa.interop.common.infrastructure.contract;

import it.pagopa.infrastructure.contract.http.HttpContractAuthentication;
import it.pagopa.infrastructure.contract.http.HttpContractStages;
import it.pagopa.infrastructure.contract.http.HttpContractValidator;
import it.pagopa.interop.common.kernel.context.AuthSession;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.m2m.kernel.domain.M2MRole;

import java.util.Objects;

public class InteropHttpContractValidator {
    private final HttpContractValidator delegate;
    private final AuthSession authSession;

    public InteropHttpContractValidator(
            HttpContractValidator delegate,
            AuthSession authSession
    ) {
        this.delegate = Objects.requireNonNull(delegate, "delegate must not be null");
        this.authSession = Objects.requireNonNull(authSession, "authSession must not be null");
    }

    public HttpContractStages.ApiCallSelectionStage as(Tenant tenant, User user) {
        Objects.requireNonNull(tenant, "tenant must not be null");
        Objects.requireNonNull(user, "user must not be null");

        HttpContractAuthentication authentication = () -> authSession.set(user, tenant);
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

        HttpContractAuthentication authentication = () -> authSession.set(m2MRole, tenant);
        return operationSupplier -> delegate.apiCall(authentication, operationSupplier);
    }
}
