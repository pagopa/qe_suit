package it.pagopa.send.web.destinatario_pf.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.send.web.login.infrastructure.page.component.OneTrustBanner;
import org.assertj.core.api.Assertions;

/**
 * {@code {baseUrl}/onboarding/domicilio-digitale}
 * Pagina del wizard di onboarding "Il meglio di SEND" del cittadino.
 * Si apre dalla card "Scelgo il meglio di SEND" della pagina {@code {baseUrl}/onboarding}.
 * Il wizard ha quattro passi: scelta del domicilio digitale ({@link ChooseDigitalDomicileSection}), casella di posta
 * ({@link PecSection} se l'utente ha già una PEC), avvisi su IO ({@link OnboardingWizardPFPage.IoSection}) e riepilogo
 * ({@link SummarySection}). Il passo mostrato all'apertura dipende dai recapiti dell'utente.
 * L'assertLoaded verifica solo gli elementi comuni a tutti i passi.
 */
@Url("${url.notifiche.cittadino.onboarding-domicilio-digitale}")
public interface OnboardingDigitalDomicilePFPage extends OnboardingWizardPFPage, Page {

    @XPath("//*[@data-testid=\"chose-digital-domicile-step\"]")
    ChooseDigitalDomicileSection chooseDigitalDomicileSection();

    @XPath("//*[@data-testid=\"pec-step\"]")
    PecSection pecSection();

    @XPath("//*[@data-testid=\"summary-step\"]")
    SummarySection summarySection();

    /**
     * Sezione del passo corrente, riconosciuta dall'etichetta dell'indicatore di avanzamento.
     * Il secondo passo è mappato per l'utente che ha già una PEC ({@link PecSection}).
     */
    default Component currentSection() {
        String current = currentProgressItem().read();
        if (current.contains("Scegli un domicilio digitale")) {
            return chooseDigitalDomicileSection();
        }
        if (current.contains("Associa una casella di posta")) {
            return pecSection();
        }
        if (current.contains("Attiva gli avvisi su IO")) {
            return ioSection();
        }
        return summarySection();
    }

    interface ChooseDigitalDomicileSection extends Component {
        @XPath("//*[@data-testid=\"select-send-button\"]")
        Button selectSendButton();

        @XPath("//*[@data-testid=\"select-pec-button\"]")
        Button selectPecButton();

        // labels

        @XPath("//*[@data-testid=\"select-send-button\"]/preceding-sibling::p[2]")
        Readable<String> title();

        @XPath("//*[@data-testid=\"select-send-button\"]/preceding-sibling::p[1]")
        Readable<String> sendDescription();

        @XPath("//*[@data-testid=\"select-pec-button\"]/preceding-sibling::p[1]")
        Readable<String> pecDescription();

        @Override
        default void assertLoaded() {
            selectSendButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Attiva su SEND"));
            selectPecButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Attiva su una PEC"));

            // labels
            title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Attiva il domicilio digitale su SEND"));
            sendDescription().readAndAssert(h -> Assertions.assertThat(h).startsWith("Riceverai le comunicazioni a valore legale su SEND"));
            pecDescription().readAndAssert(h -> Assertions.assertThat(h).startsWith("In alternativa, puoi decidere di ricevere le notifiche SEND"));
        }
    }

    interface PecSection extends Component {
        @XPath("//*[@id=\"default_email-typography\"]")
        Readable<String> courtesyEmail();

        @XPath("//*[@id=\"modifyContact-default_email\"]")
        Button modifyCourtesyEmailButton();

        // labels

        @XPath("//*[@data-testid=\"pec-step\"]/p[1]")
        Readable<String> title();

        @XPath("//*[@data-testid=\"pec-step\"]/p[2]")
        Readable<String> description();

        @XPath("//*[@data-testid=\"pec-step\"]//p[normalize-space()=\"Indirizzo PEC\"]")
        Readable<String> pecLabel();

        @XPath("//*[@data-testid=\"pec-step\"]//p[normalize-space()=\"Indirizzo PEC\"]/following-sibling::p[1]")
        Readable<String> pecValue();

        @XPath("//*[@data-testid=\"default_emailContact\"]/preceding-sibling::p[1]")
        Readable<String> courtesyEmailLabel();

        @Override
        default void assertLoaded() {
            // labels
            title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("La tua PEC come domicilio digitale SEND"));
            description().readAndAssert(h -> Assertions.assertThat(h).startsWith("Riceverai le comunicazioni a valore legale sulla tua PEC"));
        }
    }

    interface SummarySection extends Component {
        @XPath("//*[@data-testid=\"onboardingDDomAlert\"]")
        Readable<String> monitorAlert();

        // labels

        @XPath("//*[@data-testid=\"summary-step\"]/p[1]")
        Readable<String> title();

        @XPath("//*[@data-testid=\"summary-step\"]//p[starts-with(normalize-space(),\"Le comunicazioni a valore legale\")]")
        Readable<String> legalDeliveryLabel();

        @XPath("//*[@data-testid=\"summary-step\"]//p[starts-with(normalize-space(),\"Riceverai un avviso\")]")
        Readable<String> alertsLabel();

        @Override
        default void assertLoaded() {
            monitorAlert().readAndAssert(h -> Assertions.assertThat(h).startsWith("Monitora i recapiti che hai scelto"));

            // labels
            title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Il tuo riepilogo"));
            legalDeliveryLabel().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Le comunicazioni a valore legale verranno recapitate su:"));
            alertsLabel().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Riceverai un avviso via:"));
        }
    }

    @Override
    default void assertLoaded() {
        oneTrustBanner().ifPresent(OneTrustBanner::accept);
        exitButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Esci"));
        wizardTitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Il meglio di SEND"));
        progressItems().readAllAndAssert(h -> Assertions.assertThat(h).satisfiesExactly(
                p -> Assertions.assertThat(p).contains("Scegli un domicilio digitale"),
                p -> Assertions.assertThat(p).contains("Associa una casella di posta"),
                p -> Assertions.assertThat(p).contains("Attiva gli avvisi su IO"),
                p -> Assertions.assertThat(p).contains("Controlla le opzioni scelte")));
    }
}
