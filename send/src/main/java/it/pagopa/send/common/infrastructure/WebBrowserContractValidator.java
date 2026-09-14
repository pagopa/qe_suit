package it.pagopa.send.common.infrastructure;

import it.pagopa.infrastructure.contract.browser.WebContractStages;
import it.pagopa.infrastructure.contract.browser.WebContractValidator;
import it.pagopa.send.common.user.domain.Recipient;
import it.pagopa.send.common.user.domain.Tenant;
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

    /**
     * Come {@link #as(Tenant, List)}, ma per gli scenari "come destinatario PF/PG": nessun
     * tenant mittente, un solo destinatario che diventa anche l'attore autenticato lato UI
     * (vedi {@code CurrentUserSession#getCurrentActor()}).
     */
    public WebContractStages.UserStage asRecipient(Recipient recipient) {
        Objects.requireNonNull(recipient);

        return delegate.withContext(
                () -> currentUserSession.setRecipients(List.of(recipient))
        );
    }
}
