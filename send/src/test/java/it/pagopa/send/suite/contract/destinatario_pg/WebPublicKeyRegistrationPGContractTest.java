package it.pagopa.send.suite.contract.destinatario_pg;

import it.frontend.e2e.framework.web.adapter.model.FindPolicy;
import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.send.TestBootApp;
import it.pagopa.send.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.send.common.infrastructure.config.JunitContextConfig;
import it.pagopa.send.common.user.domain.Recipient;
import it.pagopa.send.web.destinatario_pg.infrastructure.page.PublicKeyRegistrationPage;
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
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Contract test della pagina "Registra chiave pubblica" dell'impresa
 * ({@code {baseUrl}/integrazione-api/registra-chiave-pubblica}).
 * <p>
 * I test sono organizzati per:
 * <ul>
 *     <li>testi del primo passo, "Inserisci i dati", e stato iniziale del form;</li>
 *     <li>messaggi dei campi obbligatori lasciati vuoti.</li>
 * </ul>
 * Nessuno scenario preme "Registra", quindi il secondo passo, "Ottieni parametri", non viene raggiunto.
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
public class WebPublicKeyRegistrationPGContractTest {

    private static final Recipient RECIPIENT = Recipient.PETRARCA;

    // testi attesi
    private static final String TITLE = "Registra chiave pubblica";
    private static final String SUBTITLE = "Inserisci i dati richiesti per abilitare l'integrazione sicura dei gestionali della tua impresa.";
    private static final List<String> STEPS = List.of("Inserisci i dati", "Ottieni parametri");
    private static final String STEP_TITLE = "Inserisci i dati";
    // la durata della chiave dipende dalla configurazione: si controlla solo che sia un numero di giorni
    private static final String STEP_DESCRIPTION = Pattern.quote("La chiave pubblica (JWKS) è necessaria per autenticare in modo sicuro le richieste provenienti dai tuoi gestionali.")
            + " La chiave scade dopo \\d+ giorni\\.";
    private static final List<String> FORM_TEXTS = List.of(
            "Scegli un nome per la chiave o mantieni quello di default",
            "Inserisci il valore della chiave",
            "-----BEGIN PUBLIC KEY-----",
            "-----END PUBLIC KEY-----");
    private static final String NAME_LABEL = "Nome";
    // il nome di default è "Chiave-" con la data del giorno senza separatori
    private static final String DEFAULT_NAME = "Chiave-\\d{8}";
    private static final String REGISTER = "Registra";
    private static final String BACK = "Indietro";
    private static final String EXIT = "Esci";
    private static final String REQUIRED_MESSAGE = "Campo obbligatorio";

    private final WebBrowserContractValidator webContractValidator;

    // testi del primo passo e stato iniziale del form

    @TestFactory
    Stream<DynamicTest> shouldShowPublicKeyRegistrationTexts() {
        return webContractValidator.asRecipient(RECIPIENT)
                .on(PublicKeyRegistrationPage.class)
                .tests(Stream.of(
                        new WebScenario<>(
                                "titolo, sottotitolo, passi e pulsanti esci e indietro",
                                page -> {},
                                page -> {
                                    page.title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(TITLE));
                                    page.subtitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(SUBTITLE));
                                    page.stepLabels().readAllAndAssert(h -> Assertions.assertThat(h).containsExactlyElementsOf(STEPS));
                                    page.currentStepLabel().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(STEPS.get(0)));
                                    page.exitButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(EXIT));
                                    page.backButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(BACK));
                                }
                        ),

                        new WebScenario<>(
                                "passo inserisci i dati: testi, nome di default, chiave vuota e registra disabilitato",
                                page -> {},
                                page -> {
                                    page.stepTitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(STEP_TITLE));
                                    page.stepDescription().readAndAssert(h -> Assertions.assertThat(h).matches(STEP_DESCRIPTION));
                                    page.formTexts().readAllAndAssert(h -> Assertions.assertThat(h).containsExactlyElementsOf(FORM_TEXTS));
                                    page.nameLabel().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(NAME_LABEL));
                                    Assertions.assertThat(page.nameInput().read()).matches(DEFAULT_NAME);
                                    Assertions.assertThat(page.publicKeyInput().read()).isEmpty();
                                    Assertions.assertThat(page.registerButton().get(FindPolicy.PRESENT).map(b -> b.getText())).hasValue(REGISTER);
                                    Assertions.assertThat(page.registerButton().isDisabled()).isTrue();
                                }
                        )
                ));
    }

    // campi obbligatori: il messaggio compare quando si esce dal campo vuoto; "Registra" non viene mai premuto

    @TestFactory
    Stream<DynamicTest> shouldValidatePublicKeyRegistrationForm() {
        return webContractValidator.asRecipient(RECIPIENT)
                .on(PublicKeyRegistrationPage.class)
                .tests(Stream.of(
                        new WebScenario<>(
                                "valore della chiave vuoto: campo obbligatorio e registra disabilitato",
                                page -> page.publicKeyInput().write("x" + Keys.chord(Keys.CONTROL, "a") + Keys.DELETE + Keys.TAB),
                                page -> {
                                    page.publicKeyError().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(REQUIRED_MESSAGE));
                                    Assertions.assertThat(page.registerButton().isDisabled()).isTrue();
                                }
                        ),

                        new WebScenario<>(
                                "nome cancellato: campo obbligatorio e registra disabilitato",
                                page -> page.nameInput().write(Keys.chord(Keys.CONTROL, "a") + Keys.DELETE + Keys.TAB),
                                page -> {
                                    page.nameError().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(REQUIRED_MESSAGE));
                                    Assertions.assertThat(page.registerButton().isDisabled()).isTrue();
                                }
                        )
                ));
    }
}
