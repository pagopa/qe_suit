package it.pagopa.interop.controller.eservice_creation.web;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.interop.web.pages.eservice_creation.EServiceCreationPage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CommonStepController {

    private final EServiceCreationPage eServiceCreationPage;

    @Given("si trova allo step {eServiceCreationStep}")
    public void assertStepLoaded(Component currentStep) {
        currentStep.assertLoaded();
    }

    @When("clicca sul button 'Salva bozza e prosegui'")
    public void saveDraft() {
        eServiceCreationPage.saveDraft();
    }
}