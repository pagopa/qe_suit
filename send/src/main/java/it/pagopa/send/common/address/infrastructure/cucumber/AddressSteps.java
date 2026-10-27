package it.pagopa.send.common.address.infrastructure.cucumber;

import io.cucumber.java.en.When;
import it.pagopa.send.common.address.application.AddressGateway;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AddressSteps {
    private final AddressGateway addressGateway;

    @When("viene abilitato il domicilio digitale SERCQ utilizzando la seguente email: {string}")
    public void enableSercqDigitalDomicile(String email) {
        addressGateway.enableSercqDigitalDomicile(email);
    }

    @When("se presente viene disabilitato il domicilio digitale SERCQ")
    @When("viene disabilitato il domicilio digitale SERCQ")
    public void disableSercqDigitalDomicile() {
        addressGateway.disableSercqDigitalDomicile();
    }

    @When("se presente viene rimosso l'indirizzo di cortesia")
    public void removeCourtesyAddress() {
        addressGateway.removeCourtesyAddress();
    }
}
