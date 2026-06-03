package it.pagopa.interop.ui.config.parameter_type.eservice_creation;

import io.cucumber.java.ParameterType;
import it.pagopa.interop.ui.page.eservice_creation.step.technical.TechnicalSpecStep;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TechSpecStepParameterType {

    private final TechnicalSpecStep technicalSpecificationStep;

    @ParameterType("Durata validità|Audience|Tempo massimo di risposta|Numero massimo di risultati per risposta|Durata di disponibilità del dato")
    public String techSpecErrorMessage(String inputName) {
        return switch (inputName.toLowerCase()) {
            case "audience" -> technicalSpecificationStep.voucherComponent().getAudienceErrorText();
            case "durata validità" -> technicalSpecificationStep.voucherComponent().getVoucherLifespanErrorText();
            case "tempo massimo di risposta" ->
                    technicalSpecificationStep.asyncComponent().getResponseTimeInputErrorText();
            case "numero massimo di risultati per risposta" ->
                    technicalSpecificationStep.asyncComponent().getMaxResultSetInputErrorText();
            case "durata di disponibilità del dato" ->
                    technicalSpecificationStep.asyncComponent().getResourceAvailableTimeInputErrorText();
            default -> throw new IllegalArgumentException("Input name not recognized: " + inputName);
        };
    }

    @ParameterType("Consenti download a blocchi")
    public Boolean techSpecCheckbox(String checkboxName) {
        return switch (checkboxName.toLowerCase()) {
            case "consenti download a blocchi" -> technicalSpecificationStep.asyncComponent().bulk().isDisabled();
            default -> throw new IllegalArgumentException("Checkbox name not recognized: " + checkboxName);
        };
    }
}
