package it.pagopa.send.suite.contract;

import it.frontend.e2e.framework.web.adapter.model.FindPolicy;
import it.frontend.e2e.framework.web.model.WebPresentationElement;
import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.send.TestBootApp;
import it.pagopa.send.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.send.common.infrastructure.config.JunitContextConfig;
import it.pagopa.send.common.user.domain.Recipient;
import it.pagopa.send.web.destinatario_pf.infrastructure.page.OnboardingDigitalDomicilePFPage;
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
 * Contract test del wizard di onboarding "Il meglio di SEND" del cittadino ({@code {baseUrl}/onboarding/domicilio-digitale}).
 * <p>
 * I test sono organizzati per:
 * <ul>
 *     <li>testi comuni a tutti i passi;</li>
 *     <li>un gruppo per ogni passo del wizard, con testi e pulsanti del passo;</li>
 *     <li>messaggi di validazione della modifica dell'email al passo 2.</li>
 * </ul>
 * Il passo mostrato all'apertura e il contenuto dei passi dipendono dai recapiti dell'utente: ogni scenario si sposta
 * sul suo passo con "Indietro" e "Avanti" e lo verifica se è raggiungibile. Nessuno scenario fa scelte nei passi o preme
 * "Conferma".
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
public class WebOnboardingDigitalDomicilePFContractTest {

    // testi attesi

    private static final String TITLE = "Il meglio di SEND";
    private static final String EXIT = "Esci";
    private static final String CHOOSE_SECTION = "Scegli un domicilio digitale";
    private static final String PEC_SECTION = "Associa una casella di posta";
    private static final String IO_SECTION = "Attiva gli avvisi su IO";
    private static final String SUMMARY_SECTION = "Controlla le opzioni scelte";
    private static final List<String> SECTIONS = List.of(CHOOSE_SECTION, PEC_SECTION, IO_SECTION, SUMMARY_SECTION);

    private static final String BACK = "Indietro";
    private static final String CONTINUE = "Continua";
    private static final String CONTINUE_WITHOUT_IO = "Continua senza l’app IO";
    private static final String CONFIRM = "Conferma";

    // passo 1, variante con la scelta del domicilio digitale (utente senza PEC)
    private static final String CHOOSE_TITLE = "Attiva il domicilio digitale su SEND";
    private static final String CHOOSE_SEND_DESCRIPTION = "Riceverai le comunicazioni a valore legale su SEND, solo in digitale, ed eviterai l’invio di una raccomandata con i relativi costi aggiuntivi.";
    private static final String CHOOSE_SEND_BUTTON = "Attiva su SEND";
    private static final String CHOOSE_PEC_DESCRIPTION = "In alternativa, puoi decidere di ricevere le notifiche SEND al tuo indirizzo PEC. Potresti evitare così l’invio di una raccomandata con i relativi costi aggiuntivi.";
    private static final String CHOOSE_PEC_BUTTON = "Attiva su una PEC";
    // passo 1, variante con la PEC in attivazione
    private static final String ACTIVATION_TITLE = "L’attivazione del domicilio digitale su PEC è in corso";
    private static final String ACTIVATION_DESCRIPTION = "Dopo l’attivazione, riceverai le notifiche SEND al tuo indirizzo PEC. Potresti evitare così l’invio di una raccomandata con i relativi costi aggiuntivi.";
    private static final String ACTIVATION_LABEL = "Attivazione in corso";

    // passo 2
    private static final String PEC_TITLE = "La tua PEC come domicilio digitale SEND";
    private static final String PEC_DESCRIPTION = "Riceverai le comunicazioni a valore legale sulla tua PEC, solo in digitale, e potrai evitare l’invio di una raccomandata con i relativi costi aggiuntivi.";
    private static final String PEC_LABEL = "Indirizzo PEC";
    private static final String COURTESY_EMAIL_LABEL = "Alla ricezione di una notifica SEND, riceverai anche un avviso all’indirizzo:";
    private static final String MODIFY = "Modifica";

    // passo 4
    private static final String SUMMARY_TITLE = "Il tuo riepilogo";
    private static final String LEGAL_DELIVERY_LABEL = "Le comunicazioni a valore legale verranno recapitate su:";
    private static final String ALERTS_LABEL = "Riceverai un avviso via:";
    private static final String MONITOR_ALERT = "Monitora i recapiti che hai scelto: una notifica digitale SEND inizia a produrre effetti giuridici anche se non l’hai consultata.";

    // messaggi di validazione della modifica dell'email al passo 2
    private static final String INVALID_EMAIL_MESSAGE = "Indirizzo email non valido";

    // dati di prova: non valido anche senza spazi, perché un valore valido avvierebbe l'invio del codice di verifica
    private static final String INVALID_EMAIL = "abc";

    private final WebBrowserContractValidator webContractValidator;

    // testi comuni a tutti i passi

    @TestFactory
    Stream<DynamicTest> shouldShowDigitalDomicileWizardTexts() {
        return webContractValidator.asRecipient(Recipient.LUCREZIA)
                .on(OnboardingDigitalDomicilePFPage.class)
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

    // passo 1: scegli un domicilio digitale

    @TestFactory
    Stream<DynamicTest> shouldShowChooseDigitalDomicileSection() {
        return webContractValidator.asRecipient(Recipient.LUCREZIA)
                .on(OnboardingDigitalDomicilePFPage.class)
                .tests(Stream.of(
                        new WebScenario<>(
                                "passo scegli un domicilio digitale: scelta tra SEND e PEC oppure PEC in attivazione",
                                page -> page.goToSection(CHOOSE_SECTION),
                                page -> {
                                    OnboardingDigitalDomicilePFPage.ChooseDigitalDomicileSection section = page.chooseDigitalDomicileSection();
                                    if (section.content().read().contains(CHOOSE_SEND_BUTTON)) {
                                        // utente senza PEC: sceglie dove attivare il domicilio digitale e non c'è "Continua"
                                        section.title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(CHOOSE_TITLE));
                                        section.description().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(CHOOSE_SEND_DESCRIPTION));
                                        section.selectSendButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(CHOOSE_SEND_BUTTON));
                                        section.pecDescription().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(CHOOSE_PEC_DESCRIPTION));
                                        section.selectPecButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(CHOOSE_PEC_BUTTON));
                                        return;
                                    }
                                    // utente con una PEC in attivazione
                                    section.title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(ACTIVATION_TITLE));
                                    section.description().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(ACTIVATION_DESCRIPTION));
                                    section.activationInProgressLabel().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(ACTIVATION_LABEL));
                                    assertNextButton(page, CONTINUE);
                                }
                        )
                ));
    }

    // passo 2: associa una casella di posta

    @TestFactory
    Stream<DynamicTest> shouldShowPecSection() {
        return webContractValidator.asRecipient(Recipient.LUCREZIA)
                .on(OnboardingDigitalDomicilePFPage.class)
                .tests(Stream.of(
                        new WebScenario<>(
                                "se raggiungibile, passo associa una casella di posta con la PEC dell'utente",
                                page -> {},
                                page -> {
                                    if (!page.goToSection(PEC_SECTION)) {
                                        // Il passo richiede una scelta al passo precedente, quindi il test termina senza verificarlo
                                        return;
                                    }
                                    OnboardingDigitalDomicilePFPage.PecSection section = page.pecSection();
                                    section.title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(PEC_TITLE));
                                    section.description().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(PEC_DESCRIPTION));
                                    section.pecLabel().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(PEC_LABEL));
                                    section.pecValue().readAndAssert(h -> Assertions.assertThat(h).isNotBlank());
                                    section.courtesyEmailLabel().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(COURTESY_EMAIL_LABEL));
                                    section.courtesyEmail().readAndAssert(h -> Assertions.assertThat(h).isNotBlank());
                                    section.modifyCourtesyEmailButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(MODIFY));
                                    assertBackAndNextButtons(page, CONTINUE);
                                }
                        )
                ));
    }

    // passo 3: attiva gli avvisi su IO

    @TestFactory
    Stream<DynamicTest> shouldShowIoSection() {
        return webContractValidator.asRecipient(Recipient.LUCREZIA)
                .on(OnboardingDigitalDomicilePFPage.class)
                .tests(Stream.of(
                        new WebScenario<>(
                                "se raggiungibile, passo attiva gli avvisi su IO",
                                page -> {},
                                page -> {
                                    if (!page.goToSection(IO_SECTION)) {
                                        // Il passo richiede una scelta in un passo precedente, quindi il test termina senza verificarlo
                                        return;
                                    }
                                    OnboardingWizardPFPage.IoSection section = page.ioSection();
                                    section.title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(IO_TITLE));
                                    section.description().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(IO_DESCRIPTION));
                                    section.downloadIoAppButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(DOWNLOAD_IO_APP));
                                    section.ioAlreadyInstalledButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(IO_ALREADY_INSTALLED));
                                    assertBackAndNextButtons(page, CONTINUE_WITHOUT_IO);
                                }
                        )
                ));
    }

    // passo 4: controlla le opzioni scelte ("Conferma" non viene mai premuto)

    @TestFactory
    Stream<DynamicTest> shouldShowSummarySection() {
        return webContractValidator.asRecipient(Recipient.LUCREZIA)
                .on(OnboardingDigitalDomicilePFPage.class)
                .tests(Stream.of(
                        new WebScenario<>(
                                "se raggiungibile, passo riepilogo con conferma non premuta",
                                page -> {},
                                page -> {
                                    if (!page.goToSection(SUMMARY_SECTION)) {
                                        // Il passo richiede una scelta in un passo precedente, quindi il test termina senza verificarlo
                                        return;
                                    }
                                    OnboardingDigitalDomicilePFPage.SummarySection section = page.summarySection();
                                    section.title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(SUMMARY_TITLE));
                                    section.legalDeliveryLabel().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(LEGAL_DELIVERY_LABEL));
                                    section.alertsLabel().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(ALERTS_LABEL));
                                    section.monitorAlert().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(MONITOR_ALERT));
                                    assertBackAndNextButtons(page, CONFIRM);
                                }
                        )
                ));
    }

    // messaggi di validazione della modifica dell'email al passo 2: solo valori non validi, così non parte mai l'invio
    // del codice di verifica

    @TestFactory
    Stream<DynamicTest> shouldValidatePecSectionEmail() {
        return webContractValidator.asRecipient(Recipient.LUCREZIA)
                .on(OnboardingDigitalDomicilePFPage.class)
                .tests(Stream.of(
                        editEmailScenario("se raggiungibile, modifica email vuota", ""),
                        editEmailScenario("se raggiungibile, modifica email non valida", INVALID_EMAIL)
                ));
    }

    /**
     * Preme "Modifica" sull'email del passo 2, la sostituisce con il valore, preme "Conferma" e controlla il messaggio. Se il
     * passo non è raggiungibile senza una scelta lo scenario termina senza verificarlo.
     */
    private WebScenario<OnboardingDigitalDomicilePFPage> editEmailScenario(String name, String value) {
        return new WebScenario<>(
                name,
                page -> {},
                page -> {
                    if (!page.goToSection(PEC_SECTION)) {
                        // Il passo richiede una scelta al passo precedente, quindi il test termina senza verificarlo
                        return;
                    }
                    OnboardingDigitalDomicilePFPage.PecSection section = page.pecSection();
                    section.modifyCourtesyEmailButton().click();
                    section.editEmailInput().write(Keys.chord(Keys.CONTROL, "a") + Keys.DELETE + value);
                    section.saveEmailButton().click();
                    section.editEmailErrorMessage().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(INVALID_EMAIL_MESSAGE));
                }
        );
    }

    private void assertBackAndNextButtons(OnboardingWizardPFPage page, String nextLabel) {
        page.backButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(BACK));
        assertNextButton(page, nextLabel);
    }

    private void assertNextButton(OnboardingWizardPFPage page, String nextLabel) {
        Assertions.assertThat(page.nextButton().get(FindPolicy.PRESENT).map(WebPresentationElement::getText)).hasValue(nextLabel);
    }
}
