package it.pagopa.send.web.destinatario_pf.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.TextField;
import it.pagopa.send.web.login.infrastructure.page.component.OneTrustBanner;
import org.assertj.core.api.Assertions;

/**
 * {@code {baseUrl}/onboarding/domicilio-digitale}
 * Pagina del wizard di onboarding "Il meglio di SEND" del cittadino.
 * Si apre dalla card "Scelgo il meglio di SEND" della pagina {@code {baseUrl}/onboarding}.
 * Il wizard ha quattro passi: scelta del domicilio digitale ({@link ChooseDigitalDomicileSection}), casella di posta
 * ({@link PecSection} se l'utente ha già una PEC), avvisi su IO ({@link OnboardingWizardPFPage.IoSection}) e riepilogo
 * ({@link SummarySection}).
 * Il passo mostrato all'apertura dipende dai recapiti dell'utente; con "Indietro" e "Avanti" ci si sposta tra i passi senza
 * salvare nulla, finché non si preme "Conferma" sull'ultimo.
 * L'assertLoaded verifica che la pagina sia caricata, cioè il titolo "Il meglio di SEND", "Esci" e i quattro passi
 * dell'indicatore; i testi di ogni passo sono verificati da {@code WebOnboardingDigitalDomicilePFContractTest}.
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

    /**
     * Primo passo. Ha due varianti: la scelta tra domicilio digitale su SEND e su PEC, per chi non ha una PEC, e l'avviso
     * di attivazione in corso, per chi ha una PEC in attivazione.
     */
    interface ChooseDigitalDomicileSection extends Component {
        @XPath("//*[@data-testid=\"chose-digital-domicile-step\"]")
        Readable<String> content();

        @XPath("//*[@data-testid=\"select-send-button\"]")
        Button selectSendButton();

        @XPath("//*[@data-testid=\"select-pec-button\"]")
        Button selectPecButton();

        // labels

        @XPath("(//*[@data-testid=\"chose-digital-domicile-step\"]//p)[1]")
        Readable<String> title();

        @XPath("(//*[@data-testid=\"chose-digital-domicile-step\"]//p)[2]")
        Readable<String> description();

        @XPath("//*[@data-testid=\"select-pec-button\"]/preceding-sibling::p[1]")
        Readable<String> pecDescription();

        @XPath("//*[@data-testid=\"chose-digital-domicile-step\"]//*[contains(@class,\"MuiChip-label\")]")
        Readable<String> activationInProgressLabel();

        @Override
        default void assertLoaded() {
            title().readAndAssert(h -> Assertions.assertThat(h).isNotBlank());
        }
    }

    interface PecSection extends Component {
        @XPath("//*[@id=\"default_email-typography\"]")
        Readable<String> courtesyEmail();

        @XPath("//*[@id=\"modifyContact-default_email\"]")
        Button modifyCourtesyEmailButton();

        // modifica dell'email: "Modifica" apre il campo con l'email e "Conferma"; un valore valido avvia l'invio del codice
        // di verifica, quindi nei test si usano solo valori non validi

        @XPath("//*[@data-testid=\"default_emailContact\"]//input")
        TextField editEmailInput();

        @XPath("//*[@id=\"saveContact-default_email\"]")
        Button saveEmailButton();

        @XPath("//*[@id=\"default_email-helper-text\"]")
        Readable<String> editEmailErrorMessage();

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
            title().readAndAssert(h -> Assertions.assertThat(h).isNotBlank());
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
            title().readAndAssert(h -> Assertions.assertThat(h).isNotBlank());
        }
    }

    @Override
    default void assertLoaded() {
        oneTrustBanner().ifPresent(OneTrustBanner::acceptIfShown);
        wizardTitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Il meglio di SEND"));
        exitButton().assertLoaded();
        progressItems().readAllAndAssert(h -> Assertions.assertThat(h).hasSize(4));
    }
}
