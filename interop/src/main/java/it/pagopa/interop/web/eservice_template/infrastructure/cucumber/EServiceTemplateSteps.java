package it.pagopa.interop.web.eservice_template.infrastructure.cucumber;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.web.eservice_template.infrastructure.page.TemplateEServiceDetailErogatorePage;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EServiceTemplateSteps {

    final private TemplateEServiceDetailErogatorePage templateEServiceDetailErogatorePage;

    @When("il Comune di Milano tenta di salvare attributi vuoti per il {currentEServiceTemplate}")
    public void ilComuneDiMilanoTentaDiSalvareAttributiVuotiPerIlTemplateEService(EServiceTemplate eServiceTemplate) {
        String eserviceTemplateId = String.valueOf(eServiceTemplate.getId());
        String eserviceTemplateVersionId = String.valueOf(eServiceTemplate.getVersions().get(0).getId());
        templateEServiceDetailErogatorePage.navigateTo(eserviceTemplateId, eserviceTemplateVersionId);
        templateEServiceDetailErogatorePage.assertLoaded();
    }

    @Then("il sistema non ritorna un messaggio di errore per il Template-EService")
    public void ilSistemaNonRitornaUnMessaggioDiErrorePerIlTemplateEService() {
        templateEServiceDetailErogatorePage.assertNoErrorNotification();
    }
}

