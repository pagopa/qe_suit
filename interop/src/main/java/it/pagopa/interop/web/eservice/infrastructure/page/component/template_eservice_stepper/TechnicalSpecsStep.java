package it.pagopa.interop.web.eservice.infrastructure.page.component.template_eservice_stepper;


import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.*;

@XPath(".//form")
public interface TechnicalSpecsStep extends Component {

    InterfaceCard interfaceCard();

    VoucherCard voucherCard();

    @XPath(".//button[@type='button' and normalize-space()='Indietro' and .//*[@data-testid='ArrowBackIcon']]")
    Button previousStepButton();

    @XPath(".//button[@type='submit' and normalize-space()='Salva bozza e prosegui' and .//*[@data-testid='SaveIcon']]")
    Button saveDraftButton();


    @XPath(".//section[contains(@class, 'MuiPaper-root') and .//h2[normalize-space()='Interfaccia']]")
    interface InterfaceCard extends Card {
        @XPath(".//h2[contains(@class, 'MuiTypography-root') and normalize-space()='Interfaccia']")
        Label cardTitle();

        @XPath(".//span[normalize-space()=\"Carica il file OpenAPI che descrive l'API.\"]")
        Label uploadDescription();

        @XPath(".//span[normalize-space()='Prima della pubblicazione, puoi usare questi strumenti per:']")
        Label toolsIntroLabel();

        @XPath(".//a[.//span[normalize-space()='OpenAPI Checker']]")
        Link openApiCheckerLink();

        @XPath(".//a[.//span[normalize-space()='Schema Editor']]")
        Link schemaEditorLink();

        @XPath(".//div[@role='alert' and contains(@class, 'MuiAlert-colorInfo')]")
        Alert infoAlert();

        @XPath(".//div[@role='alert' and contains(@class, 'MuiAlert-colorInfo')]//div[contains(@class, 'MuiAlert-message')]")
        Label infoAlertMessage();

        @XPath(".//button[@data-testid='loadFromPc']")
        Button dropzoneAreaButton();

        @XPath(".//button[@data-testid='loadFromPc']//p[contains(normalize-space(), 'Trascina qui il file OpenAPI')]")
        Label dropzoneLabel();

        @XPath(".//button[@data-testid='loadFromPc']//button[normalize-space()='Carica il file']")
        Button uploadFileButton();

        @XPath(".//span[contains(@class, 'MuiFormHelperText-root') and contains(normalize-space(), 'Puoi caricare solo file con estensione')]")
        Label allowedExtensionsHelperText();
    }

    @XPath(".//section[contains(@class, 'MuiPaper-root') and .//h2[normalize-space()='Voucher']]")
    interface VoucherCard extends Card {

        // 1. Titolo della Card
        @XPath(".//h2[contains(@class, 'MuiTypography-root') and normalize-space()='Voucher']")
        Label cardTitle();

        // 2. Campo "Durata validità (in minuti)*"
        @XPath(".//label[@id='voucherLifespan-label']")
        Label voucherLifespanLabel();

        @XPath(".//input[@id='voucherLifespan']")
        TextField voucherLifespanInput();

        // 3. Helper text sottostante ("Valore massimo: 1440 minuti (24 ore)")
        @XPath(".//span[@id='voucherLifespan-infoLabel']")
        Label voucherLifespanHelperText();
    }
}
