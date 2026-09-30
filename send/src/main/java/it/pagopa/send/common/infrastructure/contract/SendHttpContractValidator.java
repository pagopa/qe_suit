package it.pagopa.send.common.infrastructure.contract;

import it.pagopa.infrastructure.contract.http.HttpContractAuthentication;
import it.pagopa.infrastructure.contract.http.HttpContractStages;
import it.pagopa.infrastructure.contract.http.HttpContractValidator;
import it.pagopa.send.common.kernel.context.CurrentUserSession;
import it.pagopa.send.common.user.domain.Tenant;


import java.util.Objects;

public class SendHttpContractValidator {
    private final HttpContractValidator delegate;
    private final CurrentUserSession currentUserSession;

    public SendHttpContractValidator(
            HttpContractValidator delegate,
            CurrentUserSession currentUserSession
    ) {
        this.delegate = Objects.requireNonNull(delegate, "delegate must not be null");
        this.currentUserSession = Objects.requireNonNull(currentUserSession, "currentUserSession must not be null");
    }

    public HttpContractStages.ApiCallSelectionStage as(Tenant sender) {
        Objects.requireNonNull(sender, "tenant must not be null");

        HttpContractAuthentication authentication = () -> currentUserSession.setSender(sender);
        return operationSupplier -> delegate.apiCall(authentication, operationSupplier);
    }
}
