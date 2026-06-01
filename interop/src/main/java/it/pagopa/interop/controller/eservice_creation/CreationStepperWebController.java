package it.pagopa.interop.controller.eservice_creation;

import io.cucumber.java.en.When;
import it.pagopa.interop.web.pages.eservice_creation.EServiceCreationPage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CreationStepperWebController {

    private final EServiceCreationPage eServiceCreationPage;

    @When("l'utente clicca sul button 'Salva bozza e prosegui'")
    public void saveDraft() {
        eServiceCreationPage.saveDraft();
    }

}