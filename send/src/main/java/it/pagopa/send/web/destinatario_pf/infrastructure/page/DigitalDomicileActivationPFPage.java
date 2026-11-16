package it.pagopa.send.web.destinatario_pf.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.send.web.destinatario_pf.infrastructure.page.component.DigitalDomicileEmailSection;
import it.pagopa.send.web.login.infrastructure.page.component.OneTrustBanner;
import org.assertj.core.api.Assertions;

import java.util.Optional;

/**
 * {@code {baseUrl}/recapiti/domicilio-digitale/attivazione}
 * Pagina del wizard "Attiva domicilio digitale su SEND" del cittadino.
 * Si apre dalla card domicilio digitale di "I tuoi recapiti".
 * Il wizard ha tre passi: "Come funziona" (mostrato all'apertura), "Inserisci la tua email"
 * ({@link DigitalDomicileEmailSection}) e "Riepilogo" ({@link SummarySection}). Il contenuto del secondo passo e del
 * riepilogo dipende dai recapiti di cortesia dell'utente. Il pulsante "Conferma" del riepilogo attiva il domicilio digitale e nei test non va mai premuto.
 * L'assertLoaded verifica che la pagina sia caricata, cioè il titolo del wizard, i tre passi, "Continua" e "Annulla";
 * i testi comuni a tutti gli utenti dei primi due passi sono verificati da
 * {@code WebDigitalDomicileActivationPFContractTest}.
 */
@Url("${url.notifiche.cittadino.recapiti-domicilio-digitale-attivazione}")
public interface DigitalDomicileActivationPFPage extends Page {

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

    // avviso mostrato solo a chi ha già una PEC come domicilio digitale
    @XPath("//*[@data-testid=\"default-pec-info\"]")
    Readable<String> pecReplacementInfo();

    // contenuto del primo passo, per sapere se c'è l'avviso sulla PEC senza attenderlo
    @XPath("//*[@data-testid=\"sercqSendContactWizard\"]")
    Readable<String> content();

    @XPath("//*[@role=\"dialog\"][.//*[@id=\"dialog-title\"]]")
    DeliveredDialog deliveredDialog();

    @XPath("//*[@data-testid=\"emailSmsContactWizard\"]")
    DigitalDomicileEmailSection emailSection();

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
