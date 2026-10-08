package it.pagopa.send.suite.contract.destinatario_pf;

import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.send.TestBootApp;
import it.pagopa.send.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.send.common.infrastructure.config.JunitContextConfig;
import it.pagopa.send.common.user.domain.Recipient;
import it.pagopa.send.web.destinatario_pf.infrastructure.page.OnboardingIoPFPage;
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

import java.util.stream.Stream;

import static it.pagopa.send.suite.contract.destinatario_pf.OnboardingIoExpectedTexts.DOWNLOAD_IO_APP;
import static it.pagopa.send.suite.contract.destinatario_pf.OnboardingIoExpectedTexts.IO_ALREADY_INSTALLED;
import static it.pagopa.send.suite.contract.destinatario_pf.OnboardingIoExpectedTexts.IO_DESCRIPTION;
import static it.pagopa.send.suite.contract.destinatario_pf.OnboardingIoExpectedTexts.IO_TITLE;

/**
 * Contract test della pagina di onboarding "Tutto, sull'app IO" del cittadino ({@code {baseUrl}/onboarding/io}).
 * <p>
 * I test sono organizzati per:
 * <ul>
 *     <li>testi della pagina;</li>
 *     <li>pulsanti, compreso dove porta "Esci".</li>
 * </ul>
 * La pagina si apre dalla terza card di "Configura SEND", mostrata solo agli utenti senza recapiti di cortesia: il test
 * la apre dall'indirizzo, così è verificata con qualunque utente. Nessuno scenario preme i pulsanti della sezione
 * dell'app IO.
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
public class WebOnboardingIoPFContractTest {

    // testi attesi

    private static final String TITLE = "Tutto, sull'app IO";
    private static final String EXIT = "Esci";

    private final WebBrowserContractValidator webContractValidator;

    // testi della pagina

    @TestFactory
    Stream<DynamicTest> shouldShowOnboardingIoTexts() {
        return webContractValidator.asRecipient(Recipient.LUCREZIA)
                .on(OnboardingIoPFPage.class)
                .tests(textScenarios());
    }

    private Stream<WebScenario<OnboardingIoPFPage>> textScenarios() {
        return Stream.of(
                new WebScenario<>(
                        "titolo della pagina",
                        page -> {},
                        page -> page.wizardTitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(TITLE))
                ),

                new WebScenario<>(
                        "sezione dell'app IO",
                        page -> {},
                        page -> {
                            page.ioSectionTitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(IO_TITLE));
                            page.ioSectionDescription().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(IO_DESCRIPTION));
                        }
                )
        );
    }

    // pulsanti

    @TestFactory
    Stream<DynamicTest> shouldShowOnboardingIoButtons() {
        return webContractValidator.asRecipient(Recipient.LUCREZIA)
                .on(OnboardingIoPFPage.class)
                .tests(buttonScenarios());
    }

    private Stream<WebScenario<OnboardingIoPFPage>> buttonScenarios() {
        return Stream.of(
                new WebScenario<>(
                        "pulsante per uscire",
                        page -> {},
                        page -> page.exitButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(EXIT))
                ),

                new WebScenario<>(
                        "pulsanti della sezione dell'app IO",
                        page -> {},
                        page -> {
                            page.downloadIoAppButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(DOWNLOAD_IO_APP));
                            page.ioAlreadyInstalledButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(IO_ALREADY_INSTALLED));
                        }
                ),

                new WebScenario<>(
                        "esci riporta alla pagina configura SEND",
                        page -> page.exitButton().click(),
                        page -> page.configureSend().assertLoaded()
                )
        );
    }
}
