package it.pagopa.interop.web.eservice.infrastructure.cucumber;

import io.cucumber.java.ParameterType;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.interop.web.eservice.infrastructure.page.component.creation_wizard.GeneralDataWizard;
import it.pagopa.interop.web.eservice.infrastructure.page.component.creation_wizard.ThresholdAndAttributeWizard;
import it.pagopa.interop.web.eservice.infrastructure.page.component.creation_wizard.technical.TechnicalSpecWizard;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WebEServiceCreationParameterType {

    private final GeneralDataWizard generalDataWizardComponent;
    private final TechnicalSpecWizard technicalSpecWizard;
    private final ThresholdAndAttributeWizard thresholdAndAttributeWizard;

    @ParameterType("Informazioni generali|Specifiche tecniche|Soglie e attributi")
    public Component eServiceCreationStep(String stepName) {
        return switch (stepName.toLowerCase()) {
            case "informazioni generali", "dati generali" -> generalDataWizardComponent;
            case "specifiche tecniche" -> technicalSpecWizard;
            case "soglie e attributi" -> thresholdAndAttributeWizard;
            default -> throw new IllegalArgumentException("Illegal eservice creation step name: " + stepName);
        };
    }
}
