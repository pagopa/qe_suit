package it.pagopa.interop.new_arch.web.eservice.infrastructure.suit.component.creation_wizard;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.interop.new_arch.web.infrastructure.component.TextField;
import org.assertj.core.api.SoftAssertions;

public interface AdditionalDataWizard extends Component {

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

    default AdditionalDataWizard setVersionDescription(String description) {
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
