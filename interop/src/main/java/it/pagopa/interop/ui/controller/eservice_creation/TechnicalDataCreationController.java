package it.pagopa.interop.ui.controller.eservice_creation;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.ui.domain.model.eservice_creation.TechnicalSpecModel;
import it.pagopa.interop.ui.service.eservice_creation.TechnicalDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TechnicalDataCreationController {

    private final TechnicalDataService technicalDataService;

    @When("cancella i valori da tutti gli input delle specifiche tecniche")
    public void cleanInput() {
        technicalDataService.fill(TechnicalSpecModel.buildEmpty());
    }

    @Then("la checkbox {techSpecCheckbox} è disabilitata")
    public void checkboxIsDisabled(Boolean isDisabled) {
        assertThat(isDisabled)
                .as("La checkbox deve essere disabilitata")
                .isTrue();
    }
}
