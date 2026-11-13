package it.pagopa.send.web.notification_creation.infrastructure.page;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.AbstractPage;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.RadioButton;
import it.pagopa.infrastructure.suit.component.TextField;

/**
 * Selettori del form della pagina "Invia una nuova notifica" ({@link CreateNotificationPage}): i testi del passo
 * corrente e i campi di ogni passo, verificati da {@code WebNewNotificationPaContractTest}.
 */
public interface CreateNotificationForm extends AbstractPage {

    // testi del passo corrente

    // titolo del passo: il primo titolo del form con lo stile h6
    @XPath("(//form//*[contains(@class, 'MuiTypography-h6')])[1]")
    Readable<String> stepTitle();

    @XPath("(//form//*[contains(@class, 'MuiTypography-h6')])[1]/following::p[1]")
    Readable<String> requiredFieldsLabel();

    @XPath("//form//span[contains(@class, 'MuiTypography-sidenav')]")
    Readable<String> sectionTitles();

    // descrizioni sotto i titoli delle sezioni ("Campi obbligatori" è il primo elemento del form, quindi è escluso)
    @XPath("//form//p[contains(@class, 'MuiTypography-body2')][preceding-sibling::*]")
    Readable<String> sectionSubtitles();

    @XPath("//form//label[contains(@class, 'MuiInputLabel-root')]")
    Readable<String> fieldLabels();

    @XPath("//form//span[contains(@class, 'MuiFormControlLabel-label')]")
    Readable<String> optionLabels();

    // pulsanti del form diversi da quelli per cambiare passo
    @XPath("//form//button[not(@data-testid='step-submit') and not(@data-testid='previous-step')]")
    Readable<String> formButtons();

    @XPath("//form//*[contains(@class, 'MuiAlert-message')]")
    Readable<String> alertMessages();

    // "Continua", "Invia" nell'ultimo passo
    @XPath("//*[@data-testid=\"step-submit\"]")
    Button submitButton();

    @XPath("//*[@data-testid=\"previous-step\"]")
    Button previousStepButton();

    // passo "Informazioni preliminari"

    @XPath("//*[@id=\"senderDenomination\"]")
    TextField senderDenominationInput();

    @XPath("(//*[@data-testid=\"notificationLanguageRadio\"])[1]")
    RadioButton italianRadio();

    @XPath("(//*[@data-testid=\"notificationLanguageRadio\"])[2]")
    RadioButton italianAndOtherLanguageRadio();

    @XPath("//*[@id=\"subject\"]")
    TextField subjectInput();

    @XPath("//*[@id=\"abstract\"]")
    TextField abstractInput();

    @XPath("//*[@id=\"paProtocolNumber\"]")
    TextField protocolNumberInput();

    @XPath("//*[@id=\"taxonomyCode\"]")
    TextField taxonomyCodeInput();

    @XPath("(//*[@data-testid=\"comunicationTypeRadio\"])[1]")
    RadioButton registeredLetter890Radio();

    @XPath("(//*[@data-testid=\"comunicationTypeRadio\"])[2]")
    RadioButton registeredLetterRadio();

    // passo "Destinatari"

    @XPath("(//*[@data-testid=\"recipientType0\"])[1]")
    RadioButton naturalPersonRadio();

    @XPath("(//*[@data-testid=\"recipientType0\"])[2]")
    RadioButton legalPersonRadio();

    @XPath("//*[@id=\"recipients[0].taxId\"]")
    TextField recipientTaxIdInput();

    @XPath("//*[@id=\"recipients[0].firstName\"]")
    TextField recipientFirstNameInput();

    @XPath("//*[@id=\"recipients[0].lastName\"]")
    TextField recipientLastNameInput();

    @XPath("//*[@id=\"recipients[0].digitalDomicile\"]")
    TextField recipientPecInput();

    // passo "Posizione debitoria"

    @XPath("(//*[@data-testid=\"paymentModel\"])[1]")
    RadioButton pagoPaRadio();

    @XPath("(//*[@data-testid=\"paymentModel\"])[2]")
    RadioButton f24Radio();

    @XPath("(//*[@data-testid=\"paymentModel\"])[3]")
    RadioButton pagoPaAndF24Radio();

    @XPath("(//*[@data-testid=\"paymentModel\"])[4]")
    RadioButton noPaymentRadio();

    // passo "Dettaglio posizione debitoria"

    @XPath("(//*[@data-testid=\"notificationFeePolicy\"])[1]")
    RadioButton flatRateRadio();

    @XPath("(//*[@data-testid=\"notificationFeePolicy\"])[2]")
    RadioButton deliveryModeRadio();

    @XPath("(//*[@data-testid=\"pagoPaIntMode\"])[1]")
    RadioButton syncRadio();

    @XPath("(//*[@data-testid=\"pagoPaIntMode\"])[2]")
    RadioButton asyncRadio();

    // passo "Documentazione"

    @XPath("//*[@id=\"documents.0.name\"]")
    TextField documentTitleInput();
}
