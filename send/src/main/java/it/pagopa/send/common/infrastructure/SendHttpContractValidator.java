package it.pagopa.send.common.infrastructure;

import it.pagopa.infrastructure.contract.http.HttpAuthorizationStages;
import it.pagopa.infrastructure.contract.http.HttpContractIdentity;
import it.pagopa.infrastructure.contract.http.HttpContractValidator;
import it.pagopa.send.common.kernel.context.CurrentUserSession;
import it.pagopa.send.common.user.domain.User;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Test HTTP di autorizzazione (utenza x API): imposta l'utente nella sessione corrente, da cui i
 * request spec delle API (es. {@code DeliveryRequestSpecFactory}) ricavano le credenziali del client.
 * Matrice, esecuzione e asserzioni sono del motore {@link HttpContractValidator}.
 */
public class SendHttpContractValidator {

    private final HttpContractValidator delegate;
    private final CurrentUserSession currentUserSession;

    public SendHttpContractValidator(HttpContractValidator delegate, CurrentUserSession currentUserSession) {
        this.delegate = Objects.requireNonNull(delegate, "delegate must not be null");
        this.currentUserSession = Objects.requireNonNull(currentUserSession, "currentUserSession must not be null");
    }

    public HttpAuthorizationStages.ApiCallsStage as(List<User> users) {
        Objects.requireNonNull(users, "users must not be null");
        return as(users.stream());
    }

    public HttpAuthorizationStages.ApiCallsStage as(Stream<User> users) {
        Objects.requireNonNull(users, "users must not be null");
        return delegate.as(users.map(this::toIdentity));
    }

    private HttpContractIdentity toIdentity(User user) {
        Objects.requireNonNull(user, "user must not be null");
        String label = user.getUsername() == null || user.getUsername().isBlank() ? String.valueOf(user.getType()) : user.getUsername();
        return new HttpContractIdentity(label, () -> currentUserSession.setUser(user));
    }
}
