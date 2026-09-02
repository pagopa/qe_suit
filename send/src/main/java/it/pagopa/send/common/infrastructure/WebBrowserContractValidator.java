package it.pagopa.send.common.infrastructure;

import it.pagopa.infrastructure.contract.browser.WebContractStages;
import it.pagopa.infrastructure.contract.browser.WebContractValidator;
import it.pagopa.send.common.domain.Recipient;
import it.pagopa.send.common.domain.Tenant;
import it.pagopa.send.common.kernel.context.CurrentUserSession;

import java.util.List;
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

    public WebContractStages.UserStage as(Tenant tenant, List<Recipient> recipients) {
        Objects.requireNonNull(tenant);
        Objects.requireNonNull(recipients);

        return delegate.withContext(
                () -> {
                    currentUserSession.setSender(tenant);
                    currentUserSession.setRecipients(recipients);
                }
        );
    }
}
