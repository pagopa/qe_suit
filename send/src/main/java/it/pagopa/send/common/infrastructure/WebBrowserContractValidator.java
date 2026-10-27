package it.pagopa.send.common.infrastructure;

import it.pagopa.infrastructure.contract.browser.WebContractStages;
import it.pagopa.infrastructure.contract.browser.WebContractValidator;
import it.pagopa.send.common.user.domain.User;
import it.pagopa.send.common.kernel.context.CurrentUserSession;

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

    /**
     * Esegue i test autenticando il browser con l'utente indicato (PA, PF o PG).
     */
    public WebContractStages.UserStage as(User user) {
        Objects.requireNonNull(user);

        return delegate.withContext(() -> currentUserSession.setUser(user));
    }

    /**
     * Come {@link #as(User)}: rende esplicito, nei test, che l'utente è un destinatario PF/PG.
     */
    public WebContractStages.UserStage asRecipient(User recipient) {
        return as(recipient);
    }
}
