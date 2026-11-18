package it.pagopa.interop.web.eservice.infrastructure.suit.component.technical_specs_form;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Uploadable;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Alert;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.FormHelperText;
import it.pagopa.infrastructure.suit.component.Label;
import it.pagopa.infrastructure.suit.component.Link;
import it.pagopa.infrastructure.suit.component.TextField;
import it.pagopa.utils.async.PollingUtils;

@XPath(".//main")
public interface EServiceTemplateTechnicalSpecsForm extends Component {

    @XPath(".//input[@type='file']")
    Uploadable interfaceDocumentInput();

    @XPath(".//button[@name='uploadInterfaceDocBtn' or normalize-space(.)='Carica']")
    Button uploadInterfaceDocumentButton();

    @XPath(".//input[@name='prettyName']")
    TextField uploadedInterfaceDocument();


    @XPath(".//h2[normalize-space(.)='Interfaccia']")
    Label interfaceTitle();

    @XPath("//span[contains(normalize-space(.), 'Carica il file OpenAPI') or contains(normalize-space(.), 'Carica il file WSDL')]")
    Label interfaceDescription();

    @XPath("//a[contains(normalize-space(.), 'OpenAPI Checker')]")
    Link openApiCheckerLink();

    @XPath("//a[contains(normalize-space(.), 'Schema Editor')]")
    Link schemaEditorLink();

    @XPath("//div[contains(@class, 'MuiAlert-root')]")
    Alert interfaceAlert();

    @XPath(".//h2[normalize-space(.)='Voucher']")
    Label voucherTitle();

    @XPath(".//label[@id='voucherLifespan-label']")
    Label voucherLifespanLabel();

    @XPath(".//input[@name='voucherLifespan']")
    TextField voucherLifespanInput();

    @XPath(".//span[@id='voucherLifespan-infoLabel']")
    FormHelperText voucherLifespanHelperText();

    @XPath(".//form[.//button[contains(normalize-space(.), 'Salva bozza e prosegui')]]//button[normalize-space(.)='Indietro']")
    Button backButton();

    @XPath(".//form[.//button[contains(normalize-space(.), 'Salva bozza e prosegui')]]//button[normalize-space(.)='Salva bozza e prosegui']")
    Button saveDraftButton();

    default void uploadInterface(String absolutePath) {
        interfaceDocumentInput().upload(absolutePath);
        uploadInterfaceDocumentButton().click();
        PollingUtils.pollUntil(
                () -> uploadedInterfaceDocument().get(FindPolicy.PRESENT).isPresent(),
                Boolean::booleanValue,
                "Caricamento interfaccia non completato"
        );
    }

    @Override
    default void assertLoaded() {
        PollingUtils.pollUntil(
                () -> interfaceTitle().get(FindPolicy.VISIBLE).isPresent()
                        && voucherLifespanInput().get(FindPolicy.PRESENT).isPresent(),
                Boolean::booleanValue,
                "Specifiche tecniche template e-service non caricate"
        );
    }
}

