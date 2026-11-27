package it.pagopa.interop.common.tenant.infrastructure.cucumber;

import io.cucumber.java.en.When;
import it.pagopa.interop.common.kernel.context.AuthSession;
import it.pagopa.interop.common.kernel.domain.TenantRef;
import it.pagopa.interop.common.tenant.application.TenantUseCase;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TenantContactSteps {
    private final TenantUseCase tenantUseCase;
    private final AuthSession session;

    @When("l'utente tenta di salvare un contatto email dell'ente inserendo un link nelle note")
    public void attemptContactEmailSaveWithHyperlinkInNotes() {
        tenantUseCase.attemptContactEmailSaveWithHyperlinkInNotes(
                TenantRef.of(session.getTenant().getOrganizationId()));
    }
}
