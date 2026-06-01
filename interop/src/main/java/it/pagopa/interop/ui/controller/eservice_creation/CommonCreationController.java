package it.pagopa.interop.ui.controller.eservice_creation;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.interop.ui.domain.page.eservice_creation.EServiceCreationPage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CommonCreationController {

    private final EServiceCreationPage eServiceCreationPage;

    @Given("si trova allo step {eServiceCreationStep}")
    public void assertStepLoaded(Component currentStep) {
        currentStep.assertLoaded();
    }

    @When("clicca sul button 'Salva bozza e prosegui'")
    public void saveDraft() {
        eServiceCreationPage.saveDraftButton().click();
    }
}