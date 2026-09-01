package it.pagopa.interop.common.infrastructure.contract;

import it.pagopa.infrastructure.contract.browser.WebContractStages;
import it.pagopa.infrastructure.contract.browser.WebContractValidator;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.context.CurrentUserSession;

import java.util.Objects;

public class WebBrowserContractValidator {

    private final WebContractValidator delegate;
    private final CurrentUserSession currentUserSession;

    public WebBrowserContractValidator(
            WebContractValidator delegate,
            CurrentUserSession currentUserSession
    ) {
        this.delegate = Objects.requireNonNull(delegate);
        this.currentUserSession = Objects.requireNonNull(currentUserSession);
    }

    public WebContractStages.UserStage as(User user, Tenant tenant) {
        Objects.requireNonNull(user);
        Objects.requireNonNull(tenant);

        return delegate.withContext(
                () -> currentUserSession.set(user, tenant)
        );
    }
}
