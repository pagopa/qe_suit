package it.pagopa.interop.web.eservice.infrastructure.cucumber;

import io.cucumber.java.ParameterType;
import it.pagopa.interop.web.eservice.infrastructure.page.EServiceCreationPage;
import it.pagopa.interop.web.eservice.infrastructure.page.component.creation_wizard.GeneralDataWizard;
import it.pagopa.interop.web.infrastructure.config.suit.component.Alert;
import org.springframework.beans.factory.annotation.Autowired;

public class WebEServiceGeneralDataParameterType {

    private final GeneralDataWizard generalInformationStep;

    @Autowired
    public WebEServiceGeneralDataParameterType(EServiceCreationPage eServiceCreationPage) {
        this.generalInformationStep = eServiceCreationPage.generalDataStep();
    }

    @ParameterType("Nome|Descrizione|L’e-service eroga dati personali\\?")
    public String generalInformationErrorMessage(String fieldName) {
        return switch (fieldName) {
            case "Nome" -> generalInformationStep.getNameErrorText();
            case "Descrizione" -> generalInformationStep.getDescriptionErrorText();
            case "L’e-service eroga dati personali?" -> generalInformationStep.personalData().getErrorMessage();
            default ->
                    throw new IllegalArgumentException("Campo non riconosciuto nello step Dati Generali: " + fieldName);
        };
    }

    @ParameterType("L'e-service eroga o riceve dati\\?")
    public Boolean generalInformationRadioGroup(String radioGroupName) {
        return switch (radioGroupName) {
            case "L'e-service eroga o riceve dati?" -> generalInformationStep.mode().isDisabled();
            default -> throw new IllegalArgumentException("Radio group non riconosciuto: " + radioGroupName);
        };
    }

    @ParameterType("keychain|Keychain|soap|Soap|SOAP")
    public Alert generalInformationAlert(String alertType) {
        return switch (alertType.toLowerCase()) {
            case "keychain" -> generalInformationStep.keychainAlert();
            case "soap" -> generalInformationStep.soapAsyncAlert();
            default -> throw new IllegalArgumentException("Tipo di alert non riconosciuto: " + alertType);
        };
    }
}
