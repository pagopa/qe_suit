package it.pagopa.send.web.destinatario_pf.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.send.web.login.infrastructure.page.component.OneTrustBanner;
import org.assertj.core.api.Assertions;

import java.util.List;
import java.util.Optional;

/**
 * {@code {baseUrl}/recapiti/domicilio-digitale/attivazione}
 * Pagina del wizard "Attiva domicilio digitale su SEND" del cittadino.
 * Si apre dalla card domicilio digitale di "I tuoi recapiti".
 * Il wizard ha tre passi: "Come funziona" (mostrato all'apertura), "Inserisci la tua email" e "Riepilogo".
 * L'assertLoaded verifica solo il primo passo. I componenti {@link EmailSection} e {@link SummarySection} mappano i passi
 * successivi e verificano solo i loro elementi fissi: il contenuto (email, cellulare, contatti del riepilogo) dipende
 * dai recapiti di cortesia già inseriti dall'utente. Il pulsante "Conferma" del riepilogo attiva il domicilio digitale.
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

    @XPath("//*[@data-testid=\"deliveredLink\"]")
    Button deliveredLink();

    @XPath("//*[@data-testid=\"continueButton\"]")
    Button continueButton();

    @XPath("//main//button[normalize-space()=\"Annulla\"]")
    Button cancelButton();

    @XPath("//*[@data-testid=\"emailSmsContactWizard\"]")
    EmailSection emailSection();

    @XPath("//*[@data-testid=\"sercqSendContactWizard\"]")
    SummarySection summarySection();

    // labels

    @XPath("//*[@data-testid=\"sercqSendContactWizard\"]/p[1]")
    Readable<String> howItWorksTitle();

    @XPath("//*[@data-testid=\"sercq-send-info-list\"]/li//p[2]/preceding-sibling::p[1]")
    Readable<String> infoTitles();

    @XPath("//*[@data-testid=\"sercq-send-info-list\"]/li//p[2]")
    Readable<String> infoDescriptions();

    interface EmailSection extends Component {
        @XPath("//*[@id=\"default_email-custom-label\"]")
        Readable<String> emailLabel();

        @XPath("//*[@id=\"default_email-typography\"]")
        Readable<String> emailValue();

        @XPath("//*[@id=\"modifyContact-default_email\"]")
        Button modifyEmailButton();

        @XPath("//*[@id=\"default_sms-custom-label\"]")
        Readable<String> smsLabel();

        @XPath("//*[@id=\"default_sms-typography\"]")
        Readable<String> smsValue();

        @XPath("//*[@id=\"modifyContact-default_sms\"]")
        Button modifySmsButton();

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
            backButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Indietro"));

            // labels
            title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("La tua email per ricevere avvisi sulle notifiche SEND"));
            description().readAndAssert(h -> Assertions.assertThat(h).endsWith("dove ti avviseremo quando ricevi una comunicazione a valore legale su SEND."));
        }
    }

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

        @XPath("//*[@data-testid=\"InfoRoundedIcon\"]/ancestor::div[contains(@class,'MuiAlert-root')]")
        Readable<String> monitorAlert();

        @Override
        default void assertLoaded() {
            disclaimer().readAndAssert(h -> Assertions.assertThat(h).startsWith("Premendo Conferma dichiari di aver letto"));
            privacyLink().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Informativa Privacy"));
            tosLink().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Termini del servizio"));
            activateButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Conferma"));
            backButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Indietro"));

            // labels
            title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Il tuo riepilogo"));
            legalDeliveryLabel().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Le tue comunicazioni a valore legale saranno recapitate solo su:"));
            digitalDomicileLabel().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Domicilio digitale"));
            digitalDomicileValue().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("SEND"));
            alertsLabel().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Riceverai un avviso via:"));
            monitorAlert().readAndAssert(h -> Assertions.assertThat(h).startsWith("Monitora i recapiti che hai scelto"));
        }
    }

    @Override
    default void assertLoaded() {
        oneTrustBanner().ifPresent(OneTrustBanner::accept);
        wizardTitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Attiva domicilio digitale su SEND"));
        howItWorksProgressLabel().readAndAssert(h -> Assertions.assertThat(h).contains("Come funziona"));
        insertEmailProgressLabel().readAndAssert(h -> Assertions.assertThat(h).contains("Inserisci la tua email"));
        summaryProgressLabel().readAndAssert(h -> Assertions.assertThat(h).contains("Riepilogo"));
        deliveredLink().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("consegnata"));
        continueButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Continua"));
        cancelButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Annulla"));

        // labels
        howItWorksTitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Come funziona"));
        infoTitles().readAllAndAssert(List.of("Un ente ti invia una notifica su SEND", "Ricevi un messaggio", "Accedi alla notifica"));
        infoDescriptions().readAllAndAssert(h -> Assertions.assertThat(h).hasSize(3).allSatisfy(d -> Assertions.assertThat(d).isNotBlank()));
    }
}
