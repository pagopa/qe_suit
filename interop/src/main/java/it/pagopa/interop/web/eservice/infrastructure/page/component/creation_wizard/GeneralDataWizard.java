package it.pagopa.interop.web.eservice.infrastructure.page.component.creation_wizard;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceMode;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTechnology;
import it.pagopa.interop.web.infrastructure.config.suit.component.Alert;
import it.pagopa.interop.web.infrastructure.config.suit.component.RadioGroup;
import it.pagopa.interop.web.infrastructure.config.suit.component.TextField;
import org.assertj.core.api.SoftAssertions;

public interface GeneralDataWizard extends Component {

    @XPath(".//*[@id=\"name\"]")
    TextField name();

    @XPath(".//*[@id=\"description\"]")
    TextField description();

    @XPath(".//div[contains(@class, 'MuiRadioGroup-root') and .//*[text()='Sincrono (standard)'] and .//*[text()='Asincrono / massivo (in differita)']]")
    RadioGroup asyncExchange();

    @XPath(".//div[contains(@class, 'MuiRadioGroup-root') and .//*[normalize-space()='REST'] and .//*[normalize-space()='SOAP']]")
    RadioGroup technology();

    @XPath(".//div[contains(@class, 'MuiRadioGroup-root') and .//*[text()='Eroga'] and .//*[text()='Riceve']]")
    RadioGroup mode();

    @XPath(".//div[contains(@class, 'MuiRadioGroup-root') and .//*[text()='Eroga dati personali'] and .//*[text()='Non eroga dati personali']]")
    RadioGroup personalData();

    @XPath(".//div[contains(@class, 'MuiAlert-root') and contains(., 'SOAP non permette di abilitare il download a blocchi')]")
    Alert soapAsyncAlert();

    @XPath(".//*[@id=\"root\"]/div/main/div/div/div[3]/form/section/div[2]/div[3]")
    Alert keychainAlert();


    default EServiceTechnology getTechnology() {
        String val = technology().getSelected();
        return (val != null && !val.isBlank()) ? EServiceTechnology.fromValue(val) : null;
    }

    default Boolean getAsyncExchange() {
        String val = asyncExchange().getSelected();
        return (val != null && !val.isBlank()) ? val.contains("Asincrono") : null;
    }

    default EServiceMode getMode() {
        String val = mode().getSelected();
        if (val == null || val.isBlank()) return null;
        if (val.contains("Eroga")) return EServiceMode.DELIVER;
        if (val.contains("Riceve")) return EServiceMode.RECEIVE;
        return null;
    }

    default Boolean getPersonalData() {
        String val1 = personalData().getSelected();
        if (val1 == null || val1.isBlank()) return null;
        return val1.contains("Eroga") && !val1.contains("Non eroga");
    }

    default GeneralDataWizard setName(String eserviceName) {
        name().writeAndAssert(eserviceName);
        return this;
    }

    default GeneralDataWizard setDescription(String eserviceDescription) {
        description().writeAndAssert(eserviceDescription);
        return this;
    }

    default GeneralDataWizard setAsyncExchange(Boolean isAsync) {
        if(isAsync == null) return this;

        if (isAsync) asyncExchange().selectLike("Asincrono");
        else asyncExchange().selectLike("Sincrono");
        return this;
    }

    default GeneralDataWizard setTechnology(EServiceTechnology eserviceTechnology) {
        technology().selectLike(eserviceTechnology.getValue());
        return this;
    }

    default GeneralDataWizard setMode(EServiceMode eserviceMode) {
        if (eserviceMode == null) return this;
        switch (eserviceMode) {
            case DELIVER -> mode().selectLike("Eroga");
            case RECEIVE -> mode().selectLike("Riceve");
        }
        return this;
    }

    default GeneralDataWizard setPersonalData(Boolean processingPersonalData) {
        if(processingPersonalData == null) return this;

        if (processingPersonalData) personalData().selectLike("Eroga");
        else personalData().selectLike("Non eroga");
        return this;
    }

    default String getNameHelperText() {
        return name().getHelperText("name-infoLabel");
    }

    default String getNameErrorText() {
        return name().getErrorMessage("name-error");
    }

    default String getDescriptionHelperText() {
        return description().getHelperText("description-infoLabel");
    }

    default String getDescriptionErrorText() {
        return description().getErrorMessage("description-error");
    }

    @Override
    default void assertLoaded() {
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(getNameHelperText())
                    .isEqualTo("Min 5 caratteri, max 60 caratteri");

            softly.assertThat(getDescriptionHelperText())
                    .isEqualTo("Descrivi quali dati il fruitore deve fornire e quali dati l’e-service restituisce. Min 10 caratteri, max 400 caratteri");
        });
    }
}
