package it.pagopa.send.common.infrastructure.cucumber.step;

import io.cucumber.java.en.Given;
import it.pagopa.send.common.kernel.context.CurrentUserSession;
import it.pagopa.send.common.user.domain.User;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Impostazione dell'utente dello scenario, agnostica dal canale: scrive solo in {@link CurrentUserSession}, da cui
 * gli adapter di ogni canale leggono l'utente con cui operare.
 */
@RequiredArgsConstructor
public class UserSteps {
    private final CurrentUserSession currentUserSession;

    @Given("l'utente è un {string} di {user}")
    public void setSender(String role, User tenant) {
        currentUserSession.setSender(tenant);
    }

    @Given("il destinatario è {user}")
    public void setRecipient(User recipient) {
        currentUserSession.setSender(null);
        currentUserSession.setRecipients(List.of(recipient));
    }
}
