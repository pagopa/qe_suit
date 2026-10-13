package it.pagopa.send.suite.contract.destinatario_pf;

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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;

import java.util.List;
import java.util.stream.Stream;

import static it.pagopa.send.suite.contract.destinatario_pf.OnboardingIoExpectedTexts.DOWNLOAD_IO_APP;
import static it.pagopa.send.suite.contract.destinatario_pf.OnboardingIoExpectedTexts.IO_ALREADY_INSTALLED;
import static it.pagopa.send.suite.contract.destinatario_pf.OnboardingIoExpectedTexts.IO_DESCRIPTION;
import static it.pagopa.send.suite.contract.destinatario_pf.OnboardingIoExpectedTexts.IO_TITLE;

/**
 * Contract test del wizard di onboarding "Attivazione avvisi" del cittadino ({@code {baseUrl}/onboarding/avvisi}).
 * <p>
 * I test sono organizzati per:
 * <ul>
 *     <li>testi comuni a tutti i passi;</li>
 *     <li>un gruppo per ogni passo del wizard, con testi e pulsanti del passo.</li>
 * </ul>
 * I blocchi dell'email e del cellulare nel passo "Email e SMS" dipendono dai recapiti di cortesia dell'utente (recapito
 * attivo con "Modifica" oppure campo per inserirlo) e non vengono verificati. Nessuno scenario preme "Conferma" del
 * wizard.
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
                                "passo email e SMS con banner, indietro e conferma non premuto",
                                page -> Assertions.assertThat(page.goToSection(EMAIL_SMS_SECTION)).as("passo email e SMS raggiunto").isTrue(),
                                page -> {
                                    page.emailSmsSection().courtesyBanner().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(COURTESY_BANNER));
                                    page.backButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(BACK));
                                    assertNextButton(page, CONFIRM);
                                }
                        )
                ));
    }

    private void assertNextButton(OnboardingWizardPFPage page, String nextLabel) {
        Assertions.assertThat(page.nextButton().get(FindPolicy.PRESENT).map(WebPresentationElement::getText)).hasValue(nextLabel);
    }
}
