package it.pagopa.interop.web.agreement.infrastructure.cucumber;

import io.cucumber.java.en.Then;
import it.pagopa.interop.common.agreement.domain.Agreement;
import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.web.agreement.application.WebAgreementUseCase;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WebAgreementSteps {
    private final WebAgreementUseCase webAgreementUseCase;
    private final CurrentUserSession currentUserSession;

    @Then("il sistema nega al fruitore {tenant} di accedere alla pagina di dettaglio nella sezione Erogazione per la {currentAgreement}")
    @Then("il sistema nega al fruitore {tenant} di accedere alla pagina di dettaglio della {currentAgreement} nella sezione Erogazione")
    public void ilSistemaNegaAlFruitoreDiAccedereAllaPaginaDiDettaglioNellaSezioneErogazione(Tenant consumer, Agreement agreement) {
        currentUserSession.set(User.getTenantAdmin(consumer), consumer);
        webAgreementUseCase.blockUnauthorizedAccessToErogazioneSection(agreement);
    }
}
