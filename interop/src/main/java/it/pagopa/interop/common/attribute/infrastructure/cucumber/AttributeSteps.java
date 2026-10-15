package it.pagopa.interop.common.attribute.infrastructure.cucumber;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.common.attribute.application.AttributeUseCase;
import it.pagopa.interop.common.eservice.domain.EService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AttributeSteps {
    private final AttributeUseCase attributeUseCase;

    @When("l'utente rimuove la soglia personalizzata dall'attributo certificato {int} del gruppo {int} dall'{currentEService}")
    public void removeCertifiedAttributeThreshold(int groupIndex, int attributeIndex, EService eService) {
        attributeUseCase.removeCertifiedAttributeThreshold(eService, groupIndex, attributeIndex);
    }

    @Then("viene confermato l'esito di successo per la rimozione della soglia personalizzata")
    public void certifiedAttributeThresholdRemoved() {
        attributeUseCase.certifiedAttributeThresholdRemoved();
    }
}
