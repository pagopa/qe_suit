package it.pagopa.interop.web.eservice.infrastructure.cucumber;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.web.eservice.infrastructure.page.EServiceDetailErogatorePage;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EServiceSteps {

    final private EServiceDetailErogatorePage eServiceDetailErogatorePage;

    @When("il Comune di Milano tenta di salvare attributi vuoti per l'{currentEService}")
    public void ilComuneDiMilanoTentaDiSalvareAttributiVuotiPerLEService(EService eService) {
        String eserviceId = String.valueOf(eService.getId());
        String descriptorId = String.valueOf(eService.getDescriptors().get(0).getId());
        eServiceDetailErogatorePage.navigateTo(eserviceId, descriptorId);
        eServiceDetailErogatorePage.assertLoaded();
    }

    @Then("il sistema non ritorna un messaggio di errore")
    public void ilSistemaNonRitornaUnMessaggioDiErrore() {
        eServiceDetailErogatorePage.assertNoErrorNotification();
    }

}
