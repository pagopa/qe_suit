package it.pagopa.interop.web.eservice.infrastructure.cucumber;

import io.cucumber.java.en.Then;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.web.eservice.infrastructure.page.CatalogEServiceDetailPage;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WebEServiceCreationSteps {
    CatalogEServiceDetailPage catalogEServiceDetailPage;

    @Then("(il ){tenant} consulta la pagina dell'eservice e trova il pulsante di richiesta di fruizione disabilitato " +
            "per tutte le versioni antecedenti l'ultima")
    public void verifySubscribeButtonDisabledForPreviousVersions(Tenant potenzialeFruitore) {
        catalogEServiceDetailPage.verifySubscribeButtonDisabledForPreviousVersions();
    }

}
