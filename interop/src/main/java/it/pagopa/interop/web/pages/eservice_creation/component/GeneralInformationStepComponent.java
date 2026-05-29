package it.pagopa.interop.web.pages.eservice_creation.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceMode;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTechnology;
import it.pagopa.interop.web.component.Alert;
import it.pagopa.interop.web.component.RadioGroup;
import it.pagopa.interop.web.component.TextField;
import org.assertj.core.api.SoftAssertions;

import java.util.UUID;

public interface GeneralInformationStepComponent extends Component {

    @XPath(".//*[@id=\"name\"]")
    TextField name();

    @XPath(".//*[@id=\"description\"]")
    TextField description();

    @XPath(".//div[contains(@class, 'MuiRadioGroup-root') and .//*[text()='Sincrono (standard)'] and .//*[text()='Asincrono / massivo (in differita)']]")
    RadioGroup asyncExchange();

    @XPath(".//div[contains(@class, 'MuiRadioGroup-root') and .//*[text()='REST'] and .//*[text()='SOAP']]")
    RadioGroup technology();

    @XPath(".//div[contains(@class, 'MuiRadioGroup-root') and .//*[text()='Eroga'] and .//*[text()='Riceve']]")
    RadioGroup mode();

    @XPath(".//div[contains(@class, 'MuiRadioGroup-root') and .//*[text()='Eroga dati personali'] and .//*[text()='Non eroga dati personali']]")
    RadioGroup personalData();

    @XPath(".//div[contains(@class, 'MuiAlert-root') and contains(., 'SOAP non permette di abilitare il download a blocchi')]")
    Alert soapAsyncAlert();

    @XPath(".//*[@id=\"root\"]/div/main/div/div/div[3]/form/section/div[2]/div[3]")
    Alert keychainAlert();

    record GeneralInformationStepSeed(EServiceSeed eservice) {

        public static GeneralInformationStepSeed buildDefault() {
            return new GeneralInformationStepSeed(
                    new EServiceSeed()
                            .name("Test eService " + UUID.randomUUID().toString().substring(0, 8))
                            .description("Test eService description")
                            .asyncExchange(false)
                            .personalData(false)
                            .technology(EServiceTechnology.REST)
                            .mode(EServiceMode.DELIVER)
            );
        }
    }

    default void fillGeneralInformation(GeneralInformationStepSeed seed) {
        boolean isAsync = Boolean.TRUE.equals(seed.eservice.getAsyncExchange());

        setName(seed.eservice.getName());
        setDescription(seed.eservice.getDescription());
        setAsyncExchange(isAsync);
        setTechnology(seed.eservice.getTechnology());
        if(!isAsync) setMode(seed.eservice.getMode());
        setProcessingPersonalData(Boolean.TRUE.equals(seed.eservice.getPersonalData()));
    }

    default GeneralInformationStepComponent setName(String eserviceName) {
        name().writeAndAssert(eserviceName);
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

    default GeneralInformationStepComponent setDescription(String eserviceDescription) {
        description().writeAndAssert(eserviceDescription);
        return this;
    }

    default GeneralInformationStepComponent setAsyncExchange(boolean isAsync) {
        if (isAsync) asyncExchange().selectLike("Asincrono");
        else asyncExchange().selectLike("Sincrono");
        return this;
    }

    default GeneralInformationStepComponent setTechnology(EServiceTechnology eserviceTechnology) {
        technology().selectLike(eserviceTechnology.getValue());
        return this;
    }

    default GeneralInformationStepComponent setMode(EServiceMode eserviceMode) {
        switch (eserviceMode) {
            case DELIVER -> mode().selectLike("Eroga");
            case RECEIVE -> mode().selectLike("Riceve");
        }
        return this;
    }

    default GeneralInformationStepComponent setProcessingPersonalData(boolean processingPersonalData) {
        if (processingPersonalData) personalData().selectLike("Eroga");
        else personalData().selectLike("Non eroga");
        return this;
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
