package it.pagopa.interop.web.pages.eservice_creation.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Uploadable;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.interop.web.component.Button;
import it.pagopa.interop.web.component.TextField;
import org.assertj.core.api.SoftAssertions;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

public interface AdditionalInformationStepComponent extends Component {

    @XPath(".//*[@id=\"description\"]")
    TextField versionDescription();

    record AdditionalInformationStepSeed(String versionDescription){
        public static AdditionalInformationStepSeed buildDefault(){
            return new AdditionalInformationStepSeed("Test version description");
        }
    }

    default void fillAdditionalInformation(AdditionalInformationStepSeed generalInformationStepSeed) {
        setVersionDescription(generalInformationStepSeed.versionDescription);
    }

    default AdditionalInformationStepComponent setVersionDescription(String description) {
        versionDescription().writeAndAssert(description);
        return this;
    }

    default String getVersionDescriptionHelperText() {
        return versionDescription().getHelperText("description-infoLabel");
    }

    @Override
    default void assertLoaded() {
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(getVersionDescriptionHelperText())
                    .isEqualTo("Se è una nuova versione, indica cosa è cambiato rispetto alla precedente");
        });
    }
}
