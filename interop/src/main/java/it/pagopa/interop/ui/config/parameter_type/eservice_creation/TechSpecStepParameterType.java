package it.pagopa.interop.ui.config.parameter_type.eservice_creation;

import io.cucumber.java.ParameterType;
import it.pagopa.interop.ui.domain.page.eservice_creation.step.technical.TechnicalSpecificationStepComponent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TechSpecStepParameterType {

    private final TechnicalSpecificationStepComponent technicalSpecificationStepComponent;

    @ParameterType("Consenti download a blocchi")
    public Boolean techSpecCheckbox(String checkboxName) {
        return switch (checkboxName.toLowerCase()) {
            case "consenti download a blocchi" ->
                    technicalSpecificationStepComponent.asyncComponent().bulk().isDisabled();
            default -> throw new IllegalArgumentException("Checkbox name not recognized: " + checkboxName);
        };
    }
}
