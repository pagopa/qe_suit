package it.pagopa.interop.config.parameter_type.eservice_creation;

import io.cucumber.java.ParameterType;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.interop.web.pages.eservice_creation.EServiceCreationPage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CommonEServiceCreationParameterType {

    private final EServiceCreationPage eServiceCreationPage;

    @ParameterType("Informazioni generali|Specifiche tecniche")
    public Component eServiceCreationStep(String stepName){
        return switch (stepName.toLowerCase()){
            case "informazioni generali", "dati generali" -> eServiceCreationPage.generalInformationStep();
            case "specifiche tecniche" -> eServiceCreationPage.technicalSpecificationStep();
            default -> throw new IllegalArgumentException("Illegal eservice creation step name: " + stepName);
        };
    }
}
