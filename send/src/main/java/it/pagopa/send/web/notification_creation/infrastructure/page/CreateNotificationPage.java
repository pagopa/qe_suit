package it.pagopa.send.web.notification_creation.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.core.capability.core.Uploadable;
import it.frontend.e2e.framework.core.model.selector.XPathSelector;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.capability.core.Writable;
import it.frontend.e2e.framework.web.domain.Component;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.RadioButton;
import it.pagopa.infrastructure.suit.component.TextField;
import it.pagopa.send.web.login.infrastructure.page.component.OneTrustBanner;
import it.pagopa.send.web.mittente.infrastructure.page.DashboardPage;
import it.pagopa.send.web.notification_creation.application.NotificationData;
import it.pagopa.utils.async.PollingUtils;
import org.assertj.core.api.Assertions;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

/**
 * {@code {baseUrl}/nuova-notifica}
 * Pagina "Invia una nuova notifica" della PA: un wizard con i passi "Informazioni preliminari", "Destinatari",
 * "Posizione debitoria", "Dettaglio posizione debitoria" e "Documentazione".
 * Si apre dal pulsante "Invia una nuova notifica" della dashboard; uscire dalla pagina verso un'altra pagina del portale
 * apre la richiesta di conferma "Vuoi uscire?".
 * I testi del passo corrente si leggono con gli elementi comuni al form (titolo, sezioni, etichette, opzioni, pulsanti);
 * testi e stato iniziale dei passi sono verificati da {@code WebNewNotificationPaContractTest}.
 */
@Url("${url.notifiche.mittente.new-notification}")
public interface CreateNotificationPage extends Page {

    // ultima voce delle breadcrumb ("Notifiche > Nuova notifica"): l'id "title-of-page" non esiste più nel portale
    @XPath("(//nav[@aria-label=\"breadcrumbs\"]//li[contains(@class, \"MuiBreadcrumbs-li\")])[last()]")
    Readable<String> breadcrumbs();

    @XPath("//*[@data-testid='step-submit']")
    Clickable continueButton();

    // il banner dei cookie copre i campi del form: va accettato prima di compilarlo
    Optional<OneTrustBanner> oneTrustBanner();

    interface InformazioniPreliminariStep extends Component{
        @XPath(".//*[@id=\"subject\"]")
        Writable<String> subject();

        @XPath(".//input[@id='abstract']")
        Writable<String> description();

        @XPath(".//input[@id='paProtocolNumber']")
        Writable<String> protocolNumber();

        @XPath(".//input[@id='taxonomyCode']")
        Writable<String> taxonomyCode();

        // Group combobox — clicks to open the dropdown
        @XPath(".//*[@id='group']")
        Clickable openGroupDropdown();

        default void fillFields(NotificationData data) {
            //selectLanguageItalian().click();
            subject().write(data.getSubject());
            protocolNumber().write(data.getProtocolNumber());
            taxonomyCode().write(data.getTaxonomyCode());
            //selectModel890().click();
        }

        default XPathSelector groupOptionSelector(String groupName) {
            return XPathSelector.of(
                    "//*[@role='option'][normalize-space()='" + groupName + "']"
            );
        }
    }

    @XPath("//*[@id=\"root\"]/div[1]/div/main/div/div/div/form")
    InformazioniPreliminariStep informazioniPreliminariStep();

    interface DestinatariStep extends Component{
        // Recipient type
        @XPath(".//*[@id='recipient-pf']")
        Clickable selectNaturalPerson();

        @XPath(".//*[@id='recipient-pg']")
        Clickable selectLegalPerson();

        // Main fields
        @XPath(".//input[@id='recipients[0].taxId']")
        Writable<String> taxId();

        @XPath(".//input[@id='recipients[0].firstName']")
        Writable<String> firstName();

        @XPath(".//input[@id='recipients[0].lastName']")
        Writable<String> lastName();

        // Address lookup
        @XPath(".//label[@data-testid='physicalAddressLookupRadio.0'][1]")
        Clickable selectNationalRegistry();

        @XPath(".//label[@data-testid='physicalAddressLookupRadio.0'][2]")
        Clickable selectManualAddress();

        // PEC — optional
        @XPath(".//input[@id='recipients[0].digitalDomicile']")
        Writable<String> pecAddress();

        default void fillFields(NotificationData data) {
            selectNaturalPerson().click();
            taxId().write(data.getTaxId());
            firstName().write(data.getFirstName());
            lastName().write(data.getLastName());
            selectNationalRegistry().click();
            if (data.getPec() != null && !data.getPec().isEmpty()) {
                pecAddress().write(data.getPec());
            }
        }
    }

    @XPath("//*[@id=\"root\"]/div[1]/div/main/div/div/div/form")
    DestinatariStep destinatariStep();

    interface DettaglioPosizioneDebitoriaStep extends Component{

        @XPath(".//label[@data-testid='paymentModel'][1]")
        Clickable selectPagoPa();

        @XPath(".//label[@data-testid='paymentModel'][2]")
        Clickable selectF24();

        @XPath(".//label[@data-testid='paymentModel'][3]")
        Clickable selectPagoPaAndF24();

        @XPath(".//label[@data-testid='paymentModel'][4]")
        Clickable selectNoPayment();

        default void selectPaymentTypeAndSubmit(String paymentType) {
            switch (paymentType) {
                case "PAGO_PA"       -> selectPagoPa().click();
                case "F24"           -> selectF24().click();
                case "PAGO_PA_F24"   -> selectPagoPaAndF24().click();
                case "NOTHING"       -> selectNoPayment().click();
                default -> throw new IllegalArgumentException(
                        "Unknown payment type: " + paymentType
                );
            }
        }

        default void fillFields(NotificationData data) {
            selectPaymentTypeAndSubmit(data.getPaymentType());
        }
    }

    @XPath("//*[@id=\"root\"]/div[1]/div/main/div/div/div/form")
    DettaglioPosizioneDebitoriaStep dettaglioPosizioneDebitoriaStep();

    interface DocumentazioneStep extends Component{

        @XPath(".//input[@id='documents.0.name']")
        Writable<String> documentTitle();

        @XPath(".//*[@id=\"file-input\"]")
        Uploadable attachment();

        default void fillFields(NotificationData data) {
            documentTitle().write(data.getDocumentTitle());
            attachment().upload(data.getDocumentFilePath());
        }
    }

    @XPath("//*[@id=\"root\"]/div[1]/div/main/div/div/div/form")
    DocumentazioneStep documentazioneStep();

    // intestazione e passi del wizard

    @XPath("//*[@data-testid=\"breadcrumb-root-button\"]")
    Button notificationsBreadcrumb();

    @XPath("//*[@data-testid=\"titleBox\"]")
    Readable<String> header();

    @XPath("//*[@id=\"subtitle-page\"]")
    Readable<String> subtitle();

    @XPath("//*[@data-testid=\"api-how-it-works\"]")
    Readable<String> howItWorksLink();

    @XPath("//*[@data-testid=\"stepper\"]//*[contains(@class, 'MuiStepLabel-labelContainer')]")
    Readable<String> stepNames();

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

    // uscita dalla pagina

    @XPath("//*[@data-testid=\"promptDialog\"]")
    ExitDialog exitDialog();

    // dashboard aperta confermando l'uscita
    @XPath("//main")
    DashboardPage dashboard();

    /**
     * Richiesta di conferma "Vuoi uscire?", aperta quando si lascia la pagina verso un'altra pagina del portale:
     * "Annulla" resta sulla pagina, "Esci" prosegue verso la pagina scelta.
     */
    interface ExitDialog extends Component {
        @XPath("//*[@data-testid=\"promptDialog\"]//h2")
        Readable<String> title();

        @XPath("//*[@data-testid=\"promptDialog\"]//*[contains(@class, 'MuiDialogContentText-root')]")
        Readable<String> message();

        @XPath("//*[@data-testid=\"promptDialog\"]//button[not(@data-testid=\"confirmExitBtn\")]")
        Button cancelButton();

        @XPath("//*[@data-testid=\"confirmExitBtn\"]")
        Button exitButton();

        @Override
        default void assertLoaded() {
            title().readAndAssert(h -> Assertions.assertThat(h).isNotBlank());
        }
    }

    /**
     * Legge i testi di più elementi del passo. La lettura di più elementi non attende che siano visibili e, finché il
     * passo non è del tutto visibile, restituisce testi vuoti: la lettura si ripete finché nessun testo è vuoto.
     */
    default List<String> visibleTexts(Readable<String> texts) {
        return PollingUtils.pollUntil(
                texts::readAll,
                values -> !values.isEmpty() && values.stream().noneMatch(String::isEmpty),
                Duration.ofSeconds(10),
                Duration.ofSeconds(1),
                "Testi del passo non visibili");
    }

    // i passi dopo il primo si raggiungono compilando i precedenti con dati validi: la notifica parte solo con "Invia"

    default void goToRecipientsStep(NotificationData data) {
        compileInformazioniPreliminari(data);
        continueButton().click();
    }

    default void goToDebtPositionStep(NotificationData data) {
        goToRecipientsStep(data);
        compileDestinatari(data);
        continueButton().click();
    }

    default void goToDebtPositionDetailStep(NotificationData data) {
        goToDebtPositionStep(data);
        pagoPaRadio().select();
        continueButton().click();
    }

    default void goToDocumentsStep(NotificationData data) {
        goToDebtPositionStep(data);
        noPaymentRadio().select();
        continueButton().click();
    }

    @Override
    default void assertLoaded() {
        oneTrustBanner().ifPresent(OneTrustBanner::acceptIfShown);
        breadcrumbs().readAndAssert((h) -> {
            Assertions.assertThat(h).isNotNull();
            Assertions.assertThat(h).isIn("Nuova notifica", "New notification");
        });
    }

    default void compileInformazioniPreliminari(NotificationData data) {
       informazioniPreliminariStep().fillFields(data);
    }

    default void compileDestinatari(NotificationData data) {
        destinatariStep().fillFields(data);
    }

    default void compileDettaglioPosizioneDebitoria(NotificationData data) {
        dettaglioPosizioneDebitoriaStep().fillFields(data);
    }

    default void compileDocumentazione(NotificationData data) {
        documentazioneStep().fillFields(data);
    }
}
