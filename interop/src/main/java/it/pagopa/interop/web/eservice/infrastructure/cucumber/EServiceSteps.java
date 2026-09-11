package it.pagopa.interop.web.eservice.infrastructure.cucumber;

import io.cucumber.java.PendingException;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.web.eservice.infrastructure.page.EServiceDetailPage;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EServiceSteps {

    final private EServiceDetailPage eServiceDetailPage;

    @When("il Comune di Milano tenta di salvare attributi vuoti per l'{currentEService}")
    public void ilComuneDiMilanoTentaDiSalvareAttributiVuotiPerLEService(EService eService) {
        String eserviceId = String.valueOf(eService.getId());
        String descriptorId = String.valueOf(eService.getDescriptors().get(0).getId());
        eServiceDetailPage.navigateTo(eserviceId, descriptorId);
        eServiceDetailPage.assertLoaded();
    }

    @Then("il sistema non ritorna un messaggio di errore")
    public void ilSistemaNonRitornaUnMessaggioDiErrore() {
        throw new PendingException();
    }

}
