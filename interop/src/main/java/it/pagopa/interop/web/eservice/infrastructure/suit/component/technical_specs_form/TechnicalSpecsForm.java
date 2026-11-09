package it.pagopa.interop.web.eservice.infrastructure.suit.component.technical_specs_form;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Uploadable;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.TextField;

public interface TechnicalSpecsForm extends Component {

    @XPath(".//input[@type='file']")
    Uploadable interfaceDocumentInput();

    @XPath(".//*[contains(text(), 'Puoi caricare solo file con estensione')]")
    Readable<String> formatsHint();

    @XPath(".//button[@name='uploadInterfaceDocBtn']")
    Button uploadInterfaceDocumentButton();

    @XPath(".//input[@name='prettyName']")
    TextField uploadedInterfaceDocument();

    @XPath(".//input[@name='audience']")
    TextField audienceInput();

    @XPath(".//input[@name='voucherLifespan']")
    TextField voucherLifespanInput();

    @XPath(".//form//button[contains(., 'Salva bozza e prosegui')]")
    Button saveDraftButton();

    default void uploadInterface(String absolutePath) {
        interfaceDocumentInput().upload(absolutePath);
        uploadInterfaceDocumentButton().click();
        uploadedInterfaceDocument().get(FindPolicy.PRESENT)
                .orElseThrow(() -> new IllegalStateException("Caricamento interfaccia non completato"));
    }

    @Override
    default void assertLoaded() {
        audienceInput().get(FindPolicy.PRESENT)
                .orElseThrow(() -> new IllegalStateException("Specifiche tecniche non caricate"));
    }
}
