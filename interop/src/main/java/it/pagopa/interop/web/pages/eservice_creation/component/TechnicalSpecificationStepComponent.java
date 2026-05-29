package it.pagopa.interop.web.pages.eservice_creation.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Uploadable;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.interop.web.component.Button;
import it.pagopa.interop.web.component.TextField;
import org.assertj.core.api.SoftAssertions;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

public interface TechnicalSpecificationStepComponent extends Component {

    @XPath(".//button[contains(., 'Carica il file')]/..//input[@type='file']")
    Uploadable apiInterfaceAttachment();

    @XPath(".//button[contains(., 'Salva documento')]")
    Button saveAttachmentButton();

    @XPath("//*[@id=\"audience\"]")
    TextField audience();

    record TechnicalSpecificationStepSeed(String aud, String interfaceAttachmentPath) {
        public static TechnicalSpecificationStepSeed buildDefault() {
            try {
                return new TechnicalSpecificationStepSeed("quality-assurance", new ClassPathResource("assets/origin-interface.yaml").getFilePath().toAbsolutePath().toString());
            } catch(IOException e){
                return new TechnicalSpecificationStepSeed("quality-assurance", null);
            }
        }
    }

    default void fillTechnicalSpecification(TechnicalSpecificationStepSeed seed) {
        setAudience(seed.aud);
        uploadApiInterface(seed.interfaceAttachmentPath);
    }

    default TechnicalSpecificationStepComponent uploadApiInterface(String interfacePath) {
        apiInterfaceAttachment().upload(interfacePath);
        saveAttachmentButton().click();
        return this;
    }

    default TechnicalSpecificationStepComponent setAudience(String aud) {
        audience().writeAndAssert(aud);
        return this;
    }

    default String getAudienceHelperText() {
        return audience().getHelperText("audience-infoLabel");
    }

    @Override
    default void assertLoaded() {
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(getAudienceHelperText())
                    .isEqualTo("L’audience rappresenta la tua risorsa di destinazione. Per tutte le informazioni, consulta la guida");
        });
    }
}
