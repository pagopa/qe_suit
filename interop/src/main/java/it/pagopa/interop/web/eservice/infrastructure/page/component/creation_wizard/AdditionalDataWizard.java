package it.pagopa.interop.web.eservice.infrastructure.page.component.creation_wizard;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Uploadable;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.TextField;
import org.assertj.core.api.SoftAssertions;

public interface AdditionalDataWizard extends Component {

    @XPath(".//*[@id=\"description\"]")
    TextField versionDescription();

    @XPath(".//button[contains(., 'Carica il file')]/..//input[@type='file']")
    Uploadable documentAttachment();

    @XPath(".//button[contains(., 'Salva documento')]")
    Button saveDocumentButton();

    @XPath(".//*[contains(text(), 'Puoi caricare solo file con estensione')]")
    Readable<String> documentFormatsHint();

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

    default void uploadDocument(String documentPath) {
        documentAttachment().upload(documentPath);
        saveDocumentButton().click();
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
