package it.pagopa.interop.web.config.parameter_type.eservice_creation;

import io.cucumber.java.ParameterType;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.interop.web.page.eservice_creation.step.GeneralDataStep;
import it.pagopa.interop.web.page.eservice_creation.step.ThresholdAndAttributeStep;
import it.pagopa.interop.web.page.eservice_creation.step.technical.TechnicalSpecStep;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CommonEServiceCreationParameterType {

    private final GeneralDataStep generalDataStepComponent;
    private final TechnicalSpecStep technicalSpecStep;
    private final ThresholdAndAttributeStep thresholdAndAttributeStep;

    @ParameterType("Informazioni generali|Specifiche tecniche|Soglie e attributi")
    public Component eServiceCreationStep(String stepName) {
        return switch (stepName.toLowerCase()) {
            case "informazioni generali", "dati generali" -> generalDataStepComponent;
            case "specifiche tecniche" -> technicalSpecStep;
            case "soglie e attributi" -> thresholdAndAttributeStep;
            default -> throw new IllegalArgumentException("Illegal eservice creation step name: " + stepName);
        };
    }
}
