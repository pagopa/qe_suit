package it.pagopa.send.suite.contract;

import it.frontend.e2e.framework.web.adapter.model.FindPolicy;
import it.frontend.e2e.framework.web.model.WebPresentationElement;
import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.send.TestBootApp;
import it.pagopa.send.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.send.common.infrastructure.config.JunitContextConfig;
import it.pagopa.send.common.user.domain.Recipient;
import it.pagopa.send.web.destinatario_pf.infrastructure.page.OnboardingAlertsPFPage;
import it.pagopa.send.web.destinatario_pf.infrastructure.page.OnboardingWizardPFPage;
import it.pagopa.send.web.infrastructure.config.WebJUnitSuitConfig;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.openqa.selenium.Keys;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;

import java.util.List;
import java.util.stream.Stream;

import static it.pagopa.send.suite.contract.OnboardingIoExpectedTexts.DOWNLOAD_IO_APP;
import static it.pagopa.send.suite.contract.OnboardingIoExpectedTexts.IO_ALREADY_INSTALLED;
import static it.pagopa.send.suite.contract.OnboardingIoExpectedTexts.IO_DESCRIPTION;
import static it.pagopa.send.suite.contract.OnboardingIoExpectedTexts.IO_TITLE;

/**
 * Contract test del wizard di onboarding "Attivazione avvisi" del cittadino ({@code {baseUrl}/onboarding/avvisi}).
 * <p>
 * I test sono organizzati per:
 * <ul>
 *     <li>testi comuni a tutti i passi;</li>
 *     <li>un gruppo per ogni passo del wizard, con testi e pulsanti del passo;</li>
 *     <li>messaggi di validazione dei recapiti da inserire o da modificare.</li>
 * </ul>
 * Il contenuto del passo "Email e SMS" dipende dai recapiti di cortesia dell'utente: per l'email e per il cellulare si
 * verifica il blocco del recapito attivo oppure quello per inserirlo. Gli scenari di validazione usano solo valori non validi anche
 * senza spazi, così "Verifica" e "Conferma" della modifica non avviano mai l'invio del codice di verifica; nessuno scenario
 * preme "Conferma" del wizard.
 */
@ActiveProfiles({"test", "junit"})
@Execution(ExecutionMode.CONCURRENT)
@SpringBootTest(classes = {
        TestBootApp.class,
        JunitContextConfig.class,
        WebJUnitSuitConfig.class
})
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class WebOnboardingAlertsPFContractTest {

    // testi attesi

    private static final String TITLE = "Attivazione avvisi";
    private static final String EXIT = "Esci";
    private static final String IO_SECTION = "Attiva gli avvisi su IO";
    private static final String EMAIL_SMS_SECTION = "Email e SMS";
    private static final List<String> SECTIONS = List.of(IO_SECTION, EMAIL_SMS_SECTION);

    private static final String BACK = "Indietro";
    // a differenza del wizard "Il meglio di SEND", qui il portale usa l'apostrofo dritto
    private static final String CONTINUE_WITHOUT_IO = "Continua senza l'app IO";
    private static final String CONFIRM = "Conferma";

    // passo 2
    private static final String COURTESY_BANNER = "Più avvisi attivi, più possibilità hai di leggere la notifica digitale in tempo ed evitare la raccomandata!";
    // passo 2, recapiti già attivi
    private static final String EMAIL_TITLE = "Avvisi via email attivi";
    private static final String EMAIL_LABEL = "Riceverai un avviso all’indirizzo:";
    private static final String SMS_TITLE = "Avvisi via SMS attivi";
    private static final String SMS_LABEL = "Riceverai un avviso al numero:";
    private static final String MODIFY = "Modifica";
    // passo 2, recapiti da inserire
    private static final String NEW_EMAIL_TITLE = "Attiva gli avvisi via email";
    private static final String NEW_EMAIL_LABEL = "Scegli un indirizzo email:";
    private static final String EMAIL_INPUT = "Indirizzo email";
    private static final String VERIFY_EMAIL = "Verifica email";
    private static final String NEW_SMS_TITLE = "Attiva gli avvisi via SMS";
    private static final String NEW_SMS_LABEL = "Scegli un numero di cellulare:";
    private static final String SMS_INPUT = "Numero di cellulare";
    private static final String VERIFY_SMS = "Verifica numero";
    private static final String CANCEL_SMS = "Annulla attivazione SMS";
    // passo 2, messaggi di validazione
    private static final String INVALID_EMAIL_MESSAGE = "Indirizzo email non valido";
    private static final String INVALID_SMS_MESSAGE = "Numero di cellulare non valido";
    private static final String SPACES_AT_EDGES_MESSAGE = "Elimina gli spazi all'inizio o alla fine";

    // dati di prova: non validi anche senza spazi, perché un valore valido avvierebbe l'invio del codice di verifica

    private static final String INVALID_EMAIL = "abc";
    private static final String INVALID_SMS = "123";

    private final WebBrowserContractValidator webContractValidator;

    // testi comuni a tutti i passi

    @TestFactory
    Stream<DynamicTest> shouldShowAlertsWizardTexts() {
        return webContractValidator.asRecipient(Recipient.LUCREZIA)
                .on(OnboardingAlertsPFPage.class)
                .tests(Stream.of(
                        new WebScenario<>(
                                "titolo, pulsante per uscire e nomi dei passi",
                                page -> {},
                                page -> {
                                    page.wizardTitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(TITLE));
                                    page.exitButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(EXIT));
                                    // i passi già superati mostrano un'icona al posto del numero, quindi il nome si confronta senza il numero
                                    page.progressItems().readAllAndAssert(h -> Assertions.assertThat(h)
                                            .map(item -> item.replaceFirst("^\\d+\\s*", ""))
                                            .containsExactlyElementsOf(SECTIONS));
                                }
                        )
                ));
    }

    // passo 1: attiva gli avvisi su IO

    @TestFactory
    Stream<DynamicTest> shouldShowIoSection() {
        return webContractValidator.asRecipient(Recipient.LUCREZIA)
                .on(OnboardingAlertsPFPage.class)
                .tests(Stream.of(
                        new WebScenario<>(
                                "passo attiva gli avvisi su IO",
                                page -> page.goToSection(IO_SECTION),
                                page -> {
                                    OnboardingWizardPFPage.IoSection section = page.ioSection();
                                    section.title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(IO_TITLE));
                                    section.description().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(IO_DESCRIPTION));
                                    section.downloadIoAppButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(DOWNLOAD_IO_APP));
                                    section.ioAlreadyInstalledButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(IO_ALREADY_INSTALLED));
                                    page.backButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(BACK));
                                    assertNextButton(page, CONTINUE_WITHOUT_IO);
                                }
                        )
                ));
    }

    // passo 2: email e SMS ("Conferma" non viene mai premuto)

    @TestFactory
    Stream<DynamicTest> shouldShowEmailSmsSection() {
        return webContractValidator.asRecipient(Recipient.LUCREZIA)
                .on(OnboardingAlertsPFPage.class)
                .tests(Stream.of(
                        new WebScenario<>(
                                "se raggiungibile, passo email e SMS con i blocchi dei recapiti attivi o da inserire",
                                page -> {},
                                page -> {
                                    if (!page.goToSection(EMAIL_SMS_SECTION)) {
                                        // Il passo non è raggiungibile senza una scelta, quindi il test termina senza verificarlo
                                        return;
                                    }
                                    OnboardingAlertsPFPage.EmailSmsSection section = page.emailSmsSection();
                                    section.courtesyBanner().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(COURTESY_BANNER));
                                    String content = section.content().read();
                                    if (content.contains(EMAIL_TITLE)) {
                                        // l'utente ha un'email di cortesia attiva
                                        section.emailTitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(EMAIL_TITLE));
                                        section.emailLabel().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(EMAIL_LABEL));
                                        section.courtesyEmail().readAndAssert(h -> Assertions.assertThat(h).isNotBlank());
                                        section.modifyCourtesyEmailButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(MODIFY));
                                    } else {
                                        // l'utente non ha un'email di cortesia: il blocco chiede di inserirla
                                        section.emailTitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(NEW_EMAIL_TITLE));
                                        section.emailLabel().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(NEW_EMAIL_LABEL));
                                        section.emailInputLabel().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(EMAIL_INPUT));
                                        Assertions.assertThat(section.emailInput().read()).isEmpty();
                                        section.verifyEmailButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(VERIFY_EMAIL));
                                    }
                                    if (content.contains(SMS_TITLE)) {
                                        // l'utente ha un cellulare di cortesia attivo
                                        section.smsTitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(SMS_TITLE));
                                        section.smsLabel().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(SMS_LABEL));
                                        section.courtesySms().readAndAssert(h -> Assertions.assertThat(h).isNotBlank());
                                        section.modifyCourtesySmsButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(MODIFY));
                                    } else {
                                        // l'utente non ha un cellulare di cortesia: il blocco chiede di inserirlo
                                        section.smsTitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(NEW_SMS_TITLE));
                                        section.smsLabel().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(NEW_SMS_LABEL));
                                        section.smsInputLabel().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(SMS_INPUT));
                                        Assertions.assertThat(section.smsInput().read()).isEmpty();
                                        section.verifySmsButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(VERIFY_SMS));
                                        section.cancelSmsButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(CANCEL_SMS));
                                    }
                                    page.backButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(BACK));
                                    assertNextButton(page, CONFIRM);
                                }
                        )
                ));
    }

    // messaggi di validazione del passo email e SMS: solo valori non validi anche senza spazi, così non parte mai l'invio
    // del codice di verifica

    @TestFactory
    Stream<DynamicTest> shouldValidateEmailSmsSection() {
        return webContractValidator.asRecipient(Recipient.LUCREZIA)
                .on(OnboardingAlertsPFPage.class)
                .tests(Stream.of(
                        newContactScenario("se da inserire, email vuota", true, "", INVALID_EMAIL_MESSAGE),
                        newContactScenario("se da inserire, email non valida", true, INVALID_EMAIL, INVALID_EMAIL_MESSAGE),
                        newContactScenario("se da inserire, email con spazi all'inizio o alla fine", true, " " + INVALID_EMAIL + " ", SPACES_AT_EDGES_MESSAGE),
                        newContactScenario("se da inserire, cellulare vuoto", false, "", INVALID_SMS_MESSAGE),
                        newContactScenario("se da inserire, cellulare non valido", false, INVALID_SMS, INVALID_SMS_MESSAGE),
                        newContactScenario("se da inserire, cellulare con spazi all'inizio o alla fine", false, " " + INVALID_SMS + " ", SPACES_AT_EDGES_MESSAGE),
                        editContactScenario("se attiva, modifica email vuota", true, "", INVALID_EMAIL_MESSAGE),
                        editContactScenario("se attiva, modifica email non valida", true, INVALID_EMAIL, INVALID_EMAIL_MESSAGE),
                        editContactScenario("se attivo, modifica cellulare vuoto", false, "", INVALID_SMS_MESSAGE),
                        editContactScenario("se attivo, modifica cellulare non valido", false, INVALID_SMS, INVALID_SMS_MESSAGE)
                ));
    }

    /**
     * Recapito da inserire: scrive il valore, preme "Verifica" e controlla il messaggio. Se il recapito è già attivo il
     * campo non c'è e lo scenario termina senza verificarlo.
     */
    private WebScenario<OnboardingAlertsPFPage> newContactScenario(String name, boolean email, String value, String expectedMessage) {
        return new WebScenario<>(
                name,
                page -> {},
                page -> {
                    if (!page.goToSection(EMAIL_SMS_SECTION) || !page.emailSmsSection().content().read().contains(email ? NEW_EMAIL_TITLE : NEW_SMS_TITLE)) {
                        // Il recapito è già attivo: il campo per inserirlo non c'è, quindi il test termina senza verificarlo
                        return;
                    }
                    OnboardingAlertsPFPage.EmailSmsSection section = page.emailSmsSection();
                    (email ? section.emailInput() : section.smsInput()).write(value);
                    (email ? section.verifyEmailButton() : section.verifySmsButton()).click();
                    (email ? section.emailErrorMessage() : section.smsErrorMessage())
                            .readAndAssert(h -> Assertions.assertThat(h).isEqualTo(expectedMessage));
                }
        );
    }

    /**
     * Recapito attivo: preme "Modifica", sostituisce il recapito con il valore, preme "Conferma" e controlla il messaggio.
     * Se il recapito non è attivo "Modifica" non c'è e lo scenario termina senza verificarlo.
     */
    private WebScenario<OnboardingAlertsPFPage> editContactScenario(String name, boolean email, String value, String expectedMessage) {
        return new WebScenario<>(
                name,
                page -> {},
                page -> {
                    if (!page.goToSection(EMAIL_SMS_SECTION) || !page.emailSmsSection().content().read().contains(email ? EMAIL_TITLE : SMS_TITLE)) {
                        // Il recapito non è attivo: "Modifica" non c'è, quindi il test termina senza verificarlo
                        return;
                    }
                    OnboardingAlertsPFPage.EmailSmsSection section = page.emailSmsSection();
                    (email ? section.modifyCourtesyEmailButton() : section.modifyCourtesySmsButton()).click();
                    (email ? section.editEmailInput() : section.editSmsInput()).write(Keys.chord(Keys.CONTROL, "a") + Keys.DELETE + value);
                    (email ? section.saveEmailButton() : section.saveSmsButton()).click();
                    (email ? section.editEmailErrorMessage() : section.editSmsErrorMessage())
                            .readAndAssert(h -> Assertions.assertThat(h).isEqualTo(expectedMessage));
                }
        );
    }

    private void assertNextButton(OnboardingWizardPFPage page, String nextLabel) {
        Assertions.assertThat(page.nextButton().get(FindPolicy.PRESENT).map(WebPresentationElement::getText)).hasValue(nextLabel);
    }
}
