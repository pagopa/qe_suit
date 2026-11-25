package it.pagopa.send.common.partner_tecnologico.infrastructure.cucumber;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import it.pagopa.send.common.partner_tecnologico.application.PtInstitutionsUseCase;
import lombok.RequiredArgsConstructor;

/** Step sulla ricerca degli enti deleganti del Partner Tecnologico che ha effettuato l'accesso. */
@RequiredArgsConstructor
public class PtInstitutionsSteps {
    private final PtInstitutionsUseCase ptInstitutionsUseCase;

    @And("cerca gli enti per {string}")
    public void searchInstitutions(String text) {
        ptInstitutionsUseCase.searchInstitutions(text);
    }

    @Then("il filtro degli enti è vuoto")
    public void verifySearchFilterIsEmpty() {
        ptInstitutionsUseCase.verifySearchFilterIsEmpty();
    }
}
