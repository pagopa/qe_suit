package it.pagopa.interop.common.infrastructure.contract;

import it.pagopa.infrastructure.contract.http.HttpContractAuthentication;
import it.pagopa.infrastructure.contract.http.HttpContractStages;
import it.pagopa.infrastructure.contract.http.HttpContractValidator;
import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRole;

import java.util.Objects;

public class InteropHttpContractValidator {
    private final HttpContractValidator delegate;
    private final CurrentUserSession currentUserSession;

    public InteropHttpContractValidator(
            HttpContractValidator delegate,
            CurrentUserSession currentUserSession
    ) {
        this.delegate = Objects.requireNonNull(delegate, "delegate must not be null");
        this.currentUserSession = Objects.requireNonNull(currentUserSession, "currentUserSession must not be null");
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
}
