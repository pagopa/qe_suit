package it.pagopa.send.controller.destinatario;

import io.cucumber.java.en.When;
import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.pagopa.send.domain.web.pages.destinatario.AddressPage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CourtesyAddressSteps {
    private final WebPresentationGateway browser;

    @When("se presente viene rimosso l'indirizzo di cortesia")
    public void removeCourtesyAddress() {
        browser.bind(AddressPage.class).removeCourtesyAddress();
    }
}
