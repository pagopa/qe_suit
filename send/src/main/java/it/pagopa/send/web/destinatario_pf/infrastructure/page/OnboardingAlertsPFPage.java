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
 * {@code {baseUrl}/onboarding/avvisi}
 * Pagina del wizard di onboarding "Attivazione avvisi" del cittadino.
 * Si apre dalla card "Voglio solo gli avvisi" della pagina {@code {baseUrl}/onboarding}.
 * Il wizard ha due passi: avvisi su IO ({@link OnboardingWizardPFPage.IoSection}) e avvisi via email e SMS
 * ({@link EmailSmsSection}), il cui contenuto dipende dai recapiti di cortesia dell'utente.
 * Il wizard si apre sul primo passo; con "Indietro" e "Avanti" ci si sposta tra i passi senza salvare nulla, finché non si
 * preme "Conferma" sull'ultimo.
 * L'assertLoaded verifica che la pagina sia caricata, cioè il titolo "Attivazione avvisi", "Esci" e i due passi
 * dell'indicatore; i testi di ogni passo sono verificati da {@code WebOnboardingAlertsPFContractTest}.
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

    /**
     * Secondo passo: avvisi via email e SMS. Mostra il banner e un blocco per l'email e uno per il cellulare: se il recapito
     * di cortesia è già attivo il blocco mostra il recapito e "Modifica", altrimenti il campo per inserirlo e il pulsante per
     * verificarlo.
     */
    interface EmailSmsSection extends Component {
        @XPath("//*[@data-testid=\"email-sms-step\"]")
        Readable<String> content();

        @XPath("//*[@data-testid=\"courtesy-banner\"]")
        Readable<String> courtesyBanner();

        // recapiti già attivi

        @XPath("//*[@id=\"default_email-typography\"]")
        Readable<String> courtesyEmail();

        @XPath("//*[@id=\"modifyContact-default_email\"]")
        Button modifyCourtesyEmailButton();

        @XPath("//*[@id=\"default_sms-typography\"]")
        Readable<String> courtesySms();

        @XPath("//*[@id=\"modifyContact-default_sms\"]")
        Button modifyCourtesySmsButton();

        // modifica di un recapito attivo: "Modifica" apre il campo con il recapito e "Conferma"; un valore valido avvia
        // l'invio del codice di verifica, quindi nei test si usano solo valori non validi

        @XPath("//*[@data-testid=\"default_emailContact\"]//input")
        TextField editEmailInput();

        @XPath("//*[@id=\"saveContact-default_email\"]")
        Button saveEmailButton();

        @XPath("//*[@id=\"default_email-helper-text\"]")
        Readable<String> editEmailErrorMessage();

        @XPath("//*[@data-testid=\"default_smsContact\"]//input")
        TextField editSmsInput();

        @XPath("//*[@id=\"saveContact-default_sms\"]")
        Button saveSmsButton();

        @XPath("//*[@id=\"default_sms-helper-text\"]")
        Readable<String> editSmsErrorMessage();

        // recapiti da inserire: i campi hanno id generati, quindi si individuano dalla loro icona

        @XPath("//*[@data-testid=\"email-sms-step\"]//*[@data-testid=\"EmailOutlinedIcon\"]/ancestor::div[contains(@class,\"MuiFormControl-root\")]/label")
        Readable<String> emailInputLabel();

        @XPath("//*[@data-testid=\"email-sms-step\"]//*[@data-testid=\"EmailOutlinedIcon\"]/ancestor::div[contains(@class,\"MuiFormControl-root\")]//input")
        TextField emailInput();

        @XPath("//*[@data-testid=\"email-sms-step\"]//*[@data-testid=\"EmailOutlinedIcon\"]/ancestor::div[contains(@class,\"MuiFormControl-root\")]/following-sibling::button[1]")
        Button verifyEmailButton();

        @XPath("//*[@data-testid=\"email-sms-step\"]//*[@data-testid=\"EmailOutlinedIcon\"]/ancestor::div[contains(@class,\"MuiFormControl-root\")]//p[contains(@id,\"-helper-text\")]")
        Readable<String> emailErrorMessage();

        @XPath("//*[@data-testid=\"email-sms-step\"]//*[@data-testid=\"PhoneOutlinedIcon\"]/ancestor::div[contains(@class,\"MuiFormControl-root\")]/label")
        Readable<String> smsInputLabel();

        @XPath("//*[@data-testid=\"email-sms-step\"]//*[@data-testid=\"PhoneOutlinedIcon\"]/ancestor::div[contains(@class,\"MuiFormControl-root\")]//input")
        TextField smsInput();

        @XPath("//*[@data-testid=\"email-sms-step\"]//*[@data-testid=\"PhoneOutlinedIcon\"]/ancestor::div[contains(@class,\"MuiFormControl-root\")]/following-sibling::button[1]")
        Button verifySmsButton();

        @XPath("//*[@data-testid=\"email-sms-step\"]//*[@data-testid=\"PhoneOutlinedIcon\"]/ancestor::div[contains(@class,\"MuiFormControl-root\")]//p[contains(@id,\"-helper-text\")]")
        Readable<String> smsErrorMessage();

        @XPath("//*[@data-testid=\"email-sms-step\"]//*[@data-testid=\"PhoneOutlinedIcon\"]/ancestor::div[contains(@class,\"MuiFormControl-root\")]/../following-sibling::button[1]")
        Button cancelSmsButton();

        // labels: titolo e frase di ogni blocco, nella stessa posizione con il recapito attivo o da inserire

        @XPath("(//*[@data-testid=\"email-sms-step\"]//p)[1]")
        Readable<String> emailTitle();

        @XPath("(//*[@data-testid=\"email-sms-step\"]//p)[2]")
        Readable<String> emailLabel();

        @XPath("(//*[@data-testid=\"email-sms-step\"]//p)[3]")
        Readable<String> smsTitle();

        @XPath("(//*[@data-testid=\"email-sms-step\"]//p)[4]")
        Readable<String> smsLabel();

        @Override
        default void assertLoaded() {
            content().readAndAssert(h -> Assertions.assertThat(h).isNotBlank());
        }
    }

    @Override
    default void assertLoaded() {
        oneTrustBanner().ifPresent(OneTrustBanner::accept);
        wizardTitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Attivazione avvisi"));
        exitButton().assertLoaded();
        progressItems().readAllAndAssert(h -> Assertions.assertThat(h).hasSize(2));
    }
}
