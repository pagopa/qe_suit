package it.pagopa.interop.common.purpose.infrastructure.cucumber;

import io.cucumber.java.en.When;
import it.pagopa.interop.common.purpose.application.PurposeUseCase;
import it.pagopa.interop.common.purpose.domain.Purpose;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PurposeCreationSteps {
    private final PurposeUseCase purposeUseCase;

    @When("il fruitore tenta di creare una finalità con lo stesso nome della {currentPurpose} in precedenza per lo stesso e-service")
    public void createPurposeWithSameTitle(Purpose existingPurpose) {
        purposeUseCase.shouldFailToCreatePurposeWithSameTitle(existingPurpose);
    }
}
