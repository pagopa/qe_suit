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
 * {@code {baseUrl}/onboarding/avvisi}
 * Pagina del wizard di onboarding "Attivazione avvisi" del cittadino.
 * Si apre dalla card "Voglio solo gli avvisi" della pagina {@code {baseUrl}/onboarding}.
 * Il wizard ha due passi: avvisi su IO ({@link OnboardingWizardPFPage.IoSection}) e avvisi via email e SMS
 * ({@link EmailSmsSection}), il cui contenuto dipende dai recapiti di cortesia dell'utente.
 * L'assertLoaded verifica solo gli elementi comuni a tutti i passi.
 */
@Url("${url.notifiche.cittadino.onboarding-domicilio-avvisi}")
public interface OnboardingAlertsPFPage extends OnboardingWizardPFPage, Page {

    @XPath("//*[@data-testid=\"email-sms-step\"]")
    EmailSmsSection emailSmsSection();

    /**
     * Sezione del passo corrente, riconosciuta dall'etichetta dell'indicatore di avanzamento.
     */
    default Component currentSection() {
        String current = currentProgressItem().read();
        if (current.contains("Attiva gli avvisi su IO")) {
            return ioSection();
        }
        return emailSmsSection();
    }

    interface EmailSmsSection extends Component {
        @XPath("//*[@data-testid=\"courtesy-banner\"]")
        Readable<String> courtesyBanner();

        @XPath("//*[@id=\"default_email-typography\"]")
        Readable<String> courtesyEmail();

        @XPath("//*[@id=\"modifyContact-default_email\"]")
        Button modifyCourtesyEmailButton();

        @XPath("//*[@id=\"default_sms-typography\"]")
        Readable<String> courtesySms();

        @XPath("//*[@id=\"modifyContact-default_sms\"]")
        Button modifyCourtesySmsButton();

        @Override
        default void assertLoaded() {
            courtesyBanner().readAndAssert(h -> Assertions.assertThat(h).contains("avvisi attivi"));
        }
    }

    @Override
    default void assertLoaded() {
        oneTrustBanner().ifPresent(OneTrustBanner::accept);
        exitButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Esci"));
        wizardTitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Attivazione avvisi"));
        progressItems().readAllAndAssert(h -> Assertions.assertThat(h).satisfiesExactly(
                p -> Assertions.assertThat(p).contains("Attiva gli avvisi su IO"),
                p -> Assertions.assertThat(p).contains("Email e SMS")));
    }
}
