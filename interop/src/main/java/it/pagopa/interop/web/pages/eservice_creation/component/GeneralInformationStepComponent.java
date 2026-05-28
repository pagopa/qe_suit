package it.pagopa.interop.web.pages.eservice_creation.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceMode;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTechnology;
import it.pagopa.interop.web.component.RadioGroup;
import it.pagopa.interop.web.component.TextField;
import org.assertj.core.api.SoftAssertions;

public interface GeneralInformationStepComponent extends Component {

    @XPath(".//*[@id=\"name\"]")
    TextField name();

    @XPath(".//*[@id=\"description\"]")
    TextField description();

    @XPath(".//*[@id=\"root\"]/div/main/div/div/div[3]/form/section/div[2]/div[2]/div")
    RadioGroup asyncExchange();

    @XPath(".//*[@id=\"root\"]/div/main/div/div/div[3]/form/section/div[2]/div[3]/div")
    RadioGroup technology();

    @XPath(".//*[@id=\"root\"]/div/main/div/div/div[3]/form/section/div[2]/div[4]/div")
    RadioGroup mode();

    @XPath(".//*[@id=\"root\"]/div/main/div/div/div[3]/form/section/div[2]/div[5]/div")
    RadioGroup personalData();

    default void fillGeneralInformation(EServiceSeed eservice) {
        setName(eservice.getName());
        setDescription(eservice.getDescription());
        setAsyncExchange(Boolean.TRUE.equals(eservice.getAsyncExchange()));
        setTechnology(eservice.getTechnology());
        setMode(eservice.getMode());
        setProcessingPersonalData(Boolean.TRUE.equals(eservice.getPersonalData()));
    }

    default void setName(String eserviceName) {
        name().writeAndAssert(eserviceName);
    }

    default String getNameHelperText() {
        return name().getHelperText("name-infoLabel");
    }

    default void setDescription(String eserviceDescription) {
        description().writeAndAssert(eserviceDescription);
    }

    default void setAsyncExchange(boolean isAsync) {
        if (isAsync) asyncExchange().selectLike("Asincrono");
        else asyncExchange().selectLike("Sincrono");
    }

    default void setTechnology(EServiceTechnology eserviceTechnology) {
        technology().selectLike(eserviceTechnology.getValue());
    }

    default void setMode(EServiceMode eserviceMode) {
        switch (eserviceMode) {
            case DELIVER -> mode().selectLike("Eroga");
            case RECEIVE -> mode().selectLike("Riceve");
        }
    }

    default void setProcessingPersonalData(boolean processingPersonalData) {
        if (processingPersonalData) personalData().selectLike("Eroga");
        else personalData().selectLike("Non eroga");
    }

    @Override
    default void assertLoaded() {
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(getNameHelperText())
                    .isEqualTo("Min 5 caratteri, max 60 caratteri");
        });
    }
}
