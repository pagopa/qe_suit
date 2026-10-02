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
 * Questa pagina rappresenta il wizard di attivazione del domicilio digitale SERCQ per il cittadino,
 * accessibile dalla sezione I tuoi recapiti. Il wizard è composto da tre passi: come funziona, inserimento dell'email e riepilogo.
 * La pagina richiede un utente che abbia già inserito email e numero di cellulare come recapiti di cortesia.
 */
@Url("${url.notifiche.cittadino.recapiti-domicilio-digitale-attivazione}")
public interface DigitalDomicileActivationPFPage extends Page {

    Optional<OneTrustBanner> oneTrustBanner();

    @XPath("//*[@data-testid=\"wizard-title\"]")
    Readable<String> wizardTitle();

    @XPath("//*[@data-testid=\"step-0\"]")
    Readable<String> howItWorksStep();

    @XPath("//*[@data-testid=\"step-1\"]")
    Readable<String> insertEmailStep();

    @XPath("//*[@data-testid=\"step-2\"]")
    Readable<String> summaryStep();

    @XPath("//*[@data-testid=\"deliveredLink\"]")
    Button deliveredLink();

    @XPath("//*[@data-testid=\"continueButton\"]")
    Button continueButton();

    @XPath("//main//button[normalize-space()=\"Annulla\"]")
    Button cancelButton();

    @XPath("//*[@data-testid=\"emailSmsContactWizard\"]")
    EmailStep emailStep();

    @XPath("//*[@data-testid=\"sercqSendContactWizard\"]")
    SummaryStep summaryStepSection();

    // labels

    @XPath("//*[@data-testid=\"sercqSendContactWizard\"]/p[1]")
    Readable<String> howItWorksTitle();

    @XPath("//*[@data-testid=\"sercq-send-info-list\"]/li//p[2]/preceding-sibling::p[1]")
    Readable<String> infoTitles();

    @XPath("//*[@data-testid=\"sercq-send-info-list\"]/li//p[2]")
    Readable<String> infoDescriptions();

    interface EmailStep extends Component {
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
            emailLabel().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Indirizzo email"));
            emailValue().readAndAssert(h -> Assertions.assertThat(h).isNotBlank());
            modifyEmailButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Modifica"));
            smsLabel().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Numero di cellulare"));
            smsValue().readAndAssert(h -> Assertions.assertThat(h).isNotBlank());
            modifySmsButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Modifica"));
            backButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Indietro"));
            continueButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Continua"));

            // labels
            title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("La tua email per ricevere avvisi sulle notifiche SEND"));
            description().readAndAssert(h -> Assertions.assertThat(h).endsWith("dove ti avviseremo quando ricevi una comunicazione a valore legale su SEND."));
        }
    }

    interface SummaryStep extends Component {
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
            contactTypes().readAllAndAssert(List.of("Email", "Numero di cellulare"));
            contactValues().readAllAndAssert(h -> Assertions.assertThat(h).hasSize(2).allSatisfy(v -> Assertions.assertThat(v).isNotBlank()));
            monitorAlert().readAndAssert(h -> Assertions.assertThat(h).startsWith("Monitora i recapiti che hai scelto"));
        }
    }

    @Override
    default void assertLoaded() {
        oneTrustBanner().ifPresent(OneTrustBanner::accept);
        wizardTitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Attiva domicilio digitale su SEND"));
        howItWorksStep().readAndAssert(h -> Assertions.assertThat(h).contains("Come funziona"));
        insertEmailStep().readAndAssert(h -> Assertions.assertThat(h).contains("Inserisci la tua email"));
        summaryStep().readAndAssert(h -> Assertions.assertThat(h).contains("Riepilogo"));
        deliveredLink().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("consegnata"));
        continueButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Continua"));
        cancelButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Annulla"));

        // labels
        howItWorksTitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Come funziona"));
        infoTitles().readAllAndAssert(List.of("Un ente ti invia una notifica su SEND", "Ricevi un messaggio", "Accedi alla notifica"));
        infoDescriptions().readAllAndAssert(h -> Assertions.assertThat(h).hasSize(3).allSatisfy(d -> Assertions.assertThat(d).isNotBlank()));
    }
}
