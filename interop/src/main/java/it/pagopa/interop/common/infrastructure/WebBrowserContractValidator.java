package it.pagopa.interop.common.infrastructure;

import it.pagopa.infrastructure.contract.browser.WebContractStages;
import it.pagopa.infrastructure.contract.browser.WebContractValidator;
import it.pagopa.interop.common.kernel.context.AuthSession;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;

import java.util.Objects;

public class WebBrowserContractValidator {

    private final WebContractValidator delegate;
    private final AuthSession authSession;

    public WebBrowserContractValidator(
            WebContractValidator delegate,
            AuthSession authSession
    ) {
        this.delegate = Objects.requireNonNull(delegate);
        this.authSession = Objects.requireNonNull(authSession);
    }

    public WebContractStages.UserStage as(User user, Tenant tenant) {
        Objects.requireNonNull(user);
        Objects.requireNonNull(tenant);

        return delegate.withContext(
                () -> authSession.set(user, tenant)
        );
    }
}
