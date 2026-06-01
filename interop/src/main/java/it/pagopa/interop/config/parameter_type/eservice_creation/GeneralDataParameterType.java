package it.pagopa.interop.config.parameter_type.eservice_creation;

import io.cucumber.java.ParameterType;
import it.pagopa.interop.web.component.Alert;
import it.pagopa.interop.web.pages.eservice_creation.EServiceCreationPage;
import it.pagopa.interop.web.pages.eservice_creation.step.GeneralInformationStepComponent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class GeneralDataParameterType {
    private final EServiceCreationPage eServiceCreationPage;
    private final GeneralInformationStepComponent generalInformationStep = eServiceCreationPage.generalInformationStep();

    @ParameterType("Nome|Descrizione|L’e-service eroga dati personali\\?")
    public String generalInformationErrorMessage(String fieldName){
        return switch (fieldName) {
            case "Nome" -> generalInformationStep.getNameErrorText();
            case "Descrizione" -> generalInformationStep.getDescriptionErrorText();
            case "L’e-service eroga dati personali?" -> generalInformationStep.personalData().getErrorMessage();
            default -> throw new IllegalArgumentException("Campo non riconosciuto nello step Dati Generali: " + fieldName);
        };
    }

    @ParameterType("L'e-service eroga o riceve dati\\?")
    public Boolean generalInformationRadioGroup(String radioGroupName){
        return switch (radioGroupName) {
            case "L'e-service eroga o riceve dati?" -> generalInformationStep.mode().isDisabled();
            default -> throw new IllegalArgumentException("Radio group non riconosciuto: " + radioGroupName);
        };
    }

    @ParameterType("keychain|Keychain|soap|Soap|SOAP")
    public Alert generalInformationAlert(String alertType){
        return switch (alertType.toLowerCase()) {
            case "keychain" -> generalInformationStep.keychainAlert();
            case "soap" -> generalInformationStep.soapAsyncAlert();
            default -> throw new IllegalArgumentException("Tipo di alert non riconosciuto: " + alertType);
        };
    }
}
