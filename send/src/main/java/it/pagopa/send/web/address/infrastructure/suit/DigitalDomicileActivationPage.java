package it.pagopa.send.web.address.infrastructure.suit;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.TextField;
import it.pagopa.send.web.login.infrastructure.page.component.OneTrustBanner;
import org.assertj.core.api.Assertions;

import java.util.Optional;

/**
 * Wizard "Attiva domicilio digitale su SEND", con la stessa struttura nel portale del cittadino e in quello dell'impresa;
 * cambiano i testi.
 * Il wizard ha tre passi: "Come funziona" (mostrato all'apertura), l'inserimento dell'email ({@link EmailSection}) e
 * "Riepilogo" ({@link SummarySection}). Il contenuto del secondo passo e del riepilogo dipende dai recapiti di cortesia
 * dell'utente. Aggiungere un'email o un cellulare valido apre la finestra del codice di verifica ({@link CodeDialog}). Il
 * pulsante "Conferma" del riepilogo attiva il domicilio digitale e nei test non va mai premuto, come "Conferma" della
 * finestra del codice.
 * L'assertLoaded verifica che la pagina sia caricata, cioè il titolo del wizard, i tre passi, "Continua" e "Annulla";
 * testi e contenuto dei passi sono verificati dai contract test di ciascun portale.
 */
@Url("about:blank")
public interface DigitalDomicileActivationPage extends Page {

    Optional<OneTrustBanner> oneTrustBanner();

    @XPath("//*[@data-testid=\"wizard-title\"]")
    Readable<String> wizardTitle();

    @XPath("//*[@data-testid=\"step-0\"]")
    Readable<String> howItWorksProgressLabel();

    @XPath("//*[@data-testid=\"step-1\"]")
    Readable<String> insertEmailProgressLabel();

    @XPath("//*[@data-testid=\"step-2\"]")
    Readable<String> summaryProgressLabel();

    @XPath("//*[@data-testid=\"desktopWizardStepper\"]//*[@aria-current=\"step\"]")
    Readable<String> currentProgressLabel();

    @XPath("//*[@data-testid=\"deliveredLink\"]")
    Button deliveredLink();

    @XPath("//*[@data-testid=\"continueButton\"]")
    Button continueButton();

    // torna alla pagina precedente nella cronologia del browser
    @XPath("//main//button[normalize-space()=\"Annulla\"]")
    Button cancelButton();

    // contenuto del primo passo, per sapere se ci sono gli avvisi senza attenderli
    @XPath("//*[@data-testid=\"sercqSendContactWizard\"]")
    Readable<String> content();

    @XPath("//*[@role=\"dialog\"][.//*[@data-testid=\"understandButton\"]]")
    DeliveredDialog deliveredDialog();

    @XPath("//*[@data-testid=\"emailSmsContactWizard\"]")
    EmailSection emailSection();

    @XPath("//*[@role=\"dialog\"][.//*[@data-testid=\"codeConfirmButton\"]]")
    CodeDialog codeDialog();

    @XPath("//*[@data-testid=\"sercqSendContactWizard\"]")
    SummarySection summarySection();

    // labels

    @XPath("//*[@data-testid=\"sercqSendContactWizard\"]/p[1]")
    Readable<String> howItWorksTitle();

    @XPath("//*[@data-testid=\"sercq-send-info-list\"]/li//p[2]/preceding-sibling::p[1]")
    Readable<String> infoTitles();

    @XPath("//*[@data-testid=\"sercq-send-info-list\"]/li//p[2]")
    Readable<String> infoDescriptions();

    /**
     * Finestra aperta dal link "consegnata" del primo passo, con la spiegazione del valore giuridico della notifica.
     */
    interface DeliveredDialog extends Component {
        @XPath("//*[@id=\"dialog-title\"]")
        Readable<String> title();

        @XPath("//*[@id=\"dialog-description\"]")
        Readable<String> description();

        @XPath("//*[@data-testid=\"understandButton\"]")
        Button understandButton();

        @Override
        default void assertLoaded() {
            title().readAndAssert(h -> Assertions.assertThat(h).isNotBlank());
        }
    }

    /**
     * Secondo passo: email e cellulare per gli avvisi. Se l'utente li ha già, il passo mostra i recapiti con "Modifica",
     * altrimenti il campo per aggiungere l'email e il pulsante per aggiungere il cellulare; senza email "Continua" non
     * porta al riepilogo. "Modifica", "Aggiungi email" e "Aggiungi numero" con un valore valido avviano l'invio del codice
     * di verifica ({@link CodeDialog}).
     */
    interface EmailSection extends Component {
        @XPath("//*[@data-testid=\"emailSmsContactWizard\"]")
        Readable<String> content();

        // recapiti da aggiungere

        @XPath("//*[@id=\"default_email-label\"]")
        Readable<String> emailInputLabel();

        @XPath("//*[@id=\"default_email\"]")
        TextField emailInput();

        @XPath("//*[@id=\"default_email-button\"]")
        Button addEmailButton();

        @XPath("//*[@data-testid=\"emailSmsContactWizard\"]//button[normalize-space()=\"Aggiungi numero di cellulare\"]")
        Button addSmsButton();

        @XPath("//*[@id=\"default_sms\"]")
        TextField smsInput();

        @XPath("//*[@id=\"default_sms-button\"]")
        Button addSmsSaveButton();

        @XPath("//*[@data-testid=\"emailSmsContactWizard\"]//button[normalize-space()=\"Aggiungi numero di cellulare\"]/preceding-sibling::p[1]")
        Readable<String> smsQuestion();

        // campo del cellulare aperto da "Aggiungi numero di cellulare"

        @XPath("//*[@data-testid=\"default_smsContact\"]/p[1]")
        Readable<String> smsTitle();

        @XPath("//*[@data-testid=\"default_smsContact\"]/p[2]")
        Readable<String> smsDescription();

        @XPath("//*[@id=\"default_sms-label\"]")
        Readable<String> smsInputLabel();

        @XPath("//*[@data-testid=\"default_smsContact\"]//button[normalize-space()=\"Annulla\"]")
        Button cancelSmsButton();

        // recapiti già attivi

        @XPath("//*[@id=\"default_email-custom-label\"]")
        Readable<String> emailLabel();

        @XPath("//*[@id=\"default_email-typography\"]")
        Readable<String> emailValue();

        @XPath("//*[@id=\"modifyContact-default_email\"]")
        Button modifyEmailButton();

        @XPath("//*[@data-testid=\"default_emailContact\"]//input")
        TextField editEmailInput();

        @XPath("//*[@id=\"saveContact-default_email\"]")
        Button saveEmailButton();

        @XPath("//*[@id=\"default_email-helper-text\"]")
        Readable<String> emailErrorMessage();

        @XPath("//*[@id=\"default_sms-custom-label\"]")
        Readable<String> smsLabel();

        @XPath("//*[@id=\"default_sms-typography\"]")
        Readable<String> smsValue();

        @XPath("//*[@id=\"modifyContact-default_sms\"]")
        Button modifySmsButton();

        @XPath("//*[@data-testid=\"default_smsContact\"]//input")
        TextField editSmsInput();

        @XPath("//*[@id=\"saveContact-default_sms\"]")
        Button saveSmsButton();

        @XPath("//*[@id=\"default_sms-helper-text\"]")
        Readable<String> smsErrorMessage();

        @XPath("//*[@data-testid=\"prev-button\"]")
        Button backButton();

        @XPath("//*[@data-testid=\"prev-button\"]/following-sibling::button")
        Button continueButton();

        // labels

        @XPath("//*[@data-testid=\"emailSmsContactWizard\"]/p[1]")
        Readable<String> title();

        @XPath("//*[@data-testid=\"emailSmsContactWizard\"]/p[2]")
        Readable<String> description();

        @Override
        default void assertLoaded() {
            title().readAndAssert(h -> Assertions.assertThat(h).isNotBlank());
        }
    }

    /**
     * Finestra del codice di verifica, aperta da "Aggiungi email" o "Aggiungi numero" con un valore valido: il portale
     * invia un codice di 5 cifre al recapito. Il campo accetta solo cifre e con altri caratteri mostra subito un avviso,
     * senza premere "Conferma". "Conferma" salva il recapito e nei test non va mai premuto.
     */
    interface CodeDialog extends Component {
        @XPath("//*[@id=\"dialog-title\"]")
        Readable<String> title();

        @XPath("//*[@id=\"dialog-description\"]")
        Readable<String> description();

        @XPath("//*[@data-testid=\"dialog-content\"]//label")
        Readable<String> codeLabel();

        @XPath("//input[@autocomplete=\"one-time-code\"]")
        TextField codeInput();

        // "Non l'hai ricevuto? ... oppure generane uno nuovo."
        @XPath("//*[@data-testid=\"newCodeBtn\"]/parent::*")
        Readable<String> newCodeHint();

        @XPath("//*[@data-testid=\"newCodeBtn\"]")
        Button newCodeButton();

        @XPath("//*[@data-testid=\"errorAlert\"]")
        Readable<String> errorAlert();

        @XPath("//*[@data-testid=\"codeCancelButton\"]")
        Button cancelButton();

        @XPath("//*[@data-testid=\"codeConfirmButton\"]")
        Button confirmButton();

        @Override
        default void assertLoaded() {
            title().readAndAssert(h -> Assertions.assertThat(h).isNotBlank());
        }
    }

    /**
     * Terzo passo: riepilogo del domicilio digitale e dei recapiti per gli avvisi, con "Conferma" che attiva il domicilio.
     */
    interface SummarySection extends Component {
        @XPath("//*[@data-testid=\"sercq-send-disclaimer\"]")
        Readable<String> disclaimer();

        @XPath("//*[@data-testid=\"privacy-link\"]")
        Button privacyLink();

        @XPath("//*[@data-testid=\"tos-link\"]")
        Button tosLink();

        @XPath("//*[@data-testid=\"activateButton\"]")
        Button activateButton();

        @XPath("//*[@data-testid=\"prev-button\"]")
        Button backButton();

        // labels

        @XPath("//*[@data-testid=\"sercqSendContactWizard\"]//h6")
        Readable<String> title();

        @XPath("//*[@data-testid=\"sercqSendContactWizard\"]//h6/following-sibling::p[1]")
        Readable<String> legalDeliveryLabel();

        @XPath("//*[@data-testid=\"sercqSendContactWizard\"]//h6/following-sibling::p[2]")
        Readable<String> digitalDomicileLabel();

        @XPath("//*[@data-testid=\"sercqSendContactWizard\"]//h6/following-sibling::p[3]")
        Readable<String> digitalDomicileValue();

        @XPath("//*[@data-testid=\"sercq-send-contacts-list\"]/preceding-sibling::p[1]")
        Readable<String> alertsLabel();

        @XPath("//*[@data-testid=\"sercq-send-contacts-list\"]/li/div/p")
        Readable<String> contactTypes();

        @XPath("//*[@data-testid=\"sercq-send-contacts-list\"]/li/div/div/div/p")
        Readable<String> contactValues();

        @XPath("//*[@data-testid=\"sercqSendContactWizard\"]//*[contains(@class,'MuiAlert-message')]")
        Readable<String> monitorAlert();

        @Override
        default void assertLoaded() {
            title().readAndAssert(h -> Assertions.assertThat(h).isNotBlank());
        }
    }

    @Override
    default void assertLoaded() {
        oneTrustBanner().ifPresent(OneTrustBanner::acceptIfShown);
        wizardTitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Attiva domicilio digitale su SEND"));
        howItWorksProgressLabel().readAndAssert(h -> Assertions.assertThat(h).isNotBlank());
        insertEmailProgressLabel().readAndAssert(h -> Assertions.assertThat(h).isNotBlank());
        summaryProgressLabel().readAndAssert(h -> Assertions.assertThat(h).isNotBlank());
        continueButton().assertLoaded();
        cancelButton().assertLoaded();
    }
}
