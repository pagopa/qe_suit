package it.pagopa.interop.ui.config.parameter_type.eservice_creation;

import io.cucumber.java.ParameterType;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.interop.ui.domain.page.eservice_creation.step.GeneralDataStepComponent;
import it.pagopa.interop.ui.domain.page.eservice_creation.step.ThresholdAndAttributeStep;
import it.pagopa.interop.ui.domain.page.eservice_creation.step.technical.TechnicalSpecificationStepComponent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CommonEServiceCreationParameterType {

    private final GeneralDataStepComponent generalDataStepComponent;
    private final TechnicalSpecificationStepComponent technicalSpecificationStepComponent;
    private final ThresholdAndAttributeStep thresholdAndAttributeStep;

    @ParameterType("Informazioni generali|Specifiche tecniche|Soglie e attributi")
    public Component eServiceCreationStep(String stepName) {
        return switch (stepName.toLowerCase()) {
            case "informazioni generali", "dati generali" -> generalDataStepComponent;
            case "specifiche tecniche" -> technicalSpecificationStepComponent;
            case "soglie e attributi" -> thresholdAndAttributeStep;
            default -> throw new IllegalArgumentException("Illegal eservice creation step name: " + stepName);
        };
    }
}
