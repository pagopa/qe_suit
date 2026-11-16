package it.pagopa.send.suite.contract;

import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.send.TestBootApp;
import it.pagopa.send.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.send.common.infrastructure.config.JunitContextConfig;
import it.pagopa.send.common.user.domain.UserAlias;
import it.pagopa.send.common.user.domain.UserProvider;
import it.pagopa.send.web.infrastructure.config.WebJUnitSuitConfig;
import it.pagopa.send.web.mittente.infrastructure.page.APIKeyPage;
import it.pagopa.send.web.mittente.infrastructure.page.NewAPIKeyPage;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.stream.Stream;

/**
 * Contract test della pagina "Genera una API Key" della PA ({@code {baseUrl}/api-keys/nuova-api-key}).
 * <p>
 * I test sono organizzati per:
 * <ul>
 *     <li>testi della pagina e del form;</li>
 *     <li>stato iniziale del form;</li>
 *     <li>messaggi di validazione del nome;</li>
 *     <li>richiesta di conferma "Vuoi uscire?" quando si lascia la pagina;</li>
 *     <li>accesso dalla pagina "API Key" con "Genera API Key".</li>
 * </ul>
 * Nessuno scenario genera realmente una API Key: nel nome si scrivono solo valori non validi e "Continua" non viene mai
 * premuto. Su desktop il breadcrumb non mostra "Indietro", che compare solo su
 * schermi piccoli al posto del breadcrumb.
 */
@SpringBootTest(classes = {
        TestBootApp.class,
        JunitContextConfig.class,
        WebJUnitSuitConfig.class
}, properties = "spring.profiles.include=junit")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class WebNewApiKeyPaContractTest {

    // testi attesi

    private static final String API_KEYS = "API Key";
    private static final String SUBTITLE = "SEND ha generato il codice della API Key. Ora, inserisci un nome identificativo e assegnala a uno o più gruppi.";
    private static final String REQUIRED_FIELDS = "* Campi obbligatori";
    private static final String SECTION_TITLE = "Informazioni";
    private static final String NAME_TITLE = "Dai un nome alla API Key";
    // MUI aggiunge al campo obbligatorio uno spazio sottile e l'asterisco
    private static final String NAME_LABEL = "Nome API Key *";
    private static final String GROUPS_TITLE = "Scegli i gruppi a cui rendere visibile l’API Key";
    private static final String GROUPS_LABEL = "Cerca un gruppo";
    private static final String CONTINUE = "Continua";

    private static final String NAME_REQUIRED_MESSAGE = "Definire un nome per l'API Key";
    private static final String SPACES_AT_EDGES_MESSAGE = "Elimina gli spazi all'inizio o alla fine";

    private static final String EXIT_TITLE = "Vuoi uscire?";
    private static final String EXIT_MESSAGE = "Se esci, perderai tutti i dati inseriti finora. Per generare una nuova API Key, dovrai ricominciare da capo.";
    private static final String CANCEL = "Annulla";
    private static final String EXIT = "Esci";

    // dati di prova: solo valori non validi, che lasciano "Continua" disabilitato

    private static final String ANY_CHARACTER = "a";
    private static final String ONLY_SPACE = " ";

    private final WebBrowserContractValidator webContractValidator;
    private final UserProvider userProvider;

    /**
     * Testi della pagina e del form: breadcrumb, titolo, sottotitolo, campi obbligatori, sezione
     * "Informazioni" con il nome della API Key e i gruppi, pulsante "Continua".
     */
    @TestFactory
    Stream<DynamicTest> shouldShowNewApiKeyTexts() {
        return webContractValidator.as(userProvider.resolve(UserAlias.COMUNE_1))
                .on(NewAPIKeyPage.class)
                .tests(textScenarios());
    }

    private Stream<WebScenario<NewAPIKeyPage>> textScenarios() {
        return Stream.of(
                new WebScenario<>(
                        "breadcrumb con api key e genera una api key",
                        page -> {},
                        page -> {
                            page.apiKeysBreadcrumb().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(API_KEYS));
                            page.currentBreadcrumb().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(NewAPIKeyPage.TITLE));
                        }
                ),

                new WebScenario<>(
                        "intestazione della pagina",
                        page -> {},
                        page -> {
                            page.header().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(NewAPIKeyPage.TITLE));
                            page.subtitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(SUBTITLE));
                            page.requiredFieldsLabel().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(REQUIRED_FIELDS));
                        }
                ),

                new WebScenario<>(
                        "sezione informazioni con il nome della api key e i gruppi",
                        page -> {},
                        page -> {
                            page.sectionTitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(SECTION_TITLE));
                            page.nameTitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(NAME_TITLE));
                            page.nameLabel().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(NAME_LABEL));
                            page.groupsTitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(GROUPS_TITLE));
                            page.groupsLabel().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(GROUPS_LABEL));
                        }
                ),

                new WebScenario<>(
                        "pulsante continua",
                        page -> {},
                        page -> page.continueButtonText().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(CONTINUE))
                )
        );
    }

    /**
     * Stato del form all'apertura della pagina: nome vuoto, nessun gruppo selezionato e "Continua" disabilitato, perché
     * il nome è obbligatorio.
     */
    @TestFactory
    Stream<DynamicTest> shouldStartWithEmptyForm() {
        return webContractValidator.as(userProvider.resolve(UserAlias.COMUNE_1))
                .on(NewAPIKeyPage.class)
                .tests(Stream.of(
                        new WebScenario<>(
                                "nome della api key vuoto",
                                page -> {},
                                page -> page.nameInput().readAndAssert(h -> Assertions.assertThat(h).isEmpty())
                        ),

                        new WebScenario<>(
                                "nessun gruppo selezionato",
                                page -> {},
                                page -> page.groupsField().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(GROUPS_LABEL))
                        ),

                        new WebScenario<>(
                                "continua disabilitato",
                                page -> {},
                                page -> Assertions.assertThat(page.continueButton().isDisabled()).isTrue()
                        )
                ));
    }

    /**
     * Messaggi di validazione del nome: obbligatorio quando il campo viene svuotato, e senza spazi all'inizio o alla
     * fine. In entrambi i casi "Continua" resta disabilitato.
     */
    @TestFactory
    Stream<DynamicTest> shouldShowNameValidationMessages() {
        return webContractValidator.as(userProvider.resolve(UserAlias.COMUNE_1))
                .on(NewAPIKeyPage.class)
                .tests(Stream.of(
                        new WebScenario<>(
                                "nome obbligatorio",
                                page -> {
                                    page.nameInput().fill(ANY_CHARACTER);
                                    page.nameInput().fill("");
                                },
                                page -> {
                                    page.nameHelperText().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(NAME_REQUIRED_MESSAGE));
                                    Assertions.assertThat(page.continueButton().isDisabled()).isTrue();
                                }
                        ),

                        new WebScenario<>(
                                "nome con spazi all'inizio o alla fine",
                                page -> page.nameInput().fill(ONLY_SPACE),
                                page -> {
                                    page.nameHelperText().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(SPACES_AT_EDGES_MESSAGE));
                                    Assertions.assertThat(page.continueButton().isDisabled()).isTrue();
                                }
                        )
                ));
    }

    /**
     * Lasciare la pagina con il breadcrumb "API Key" apre la richiesta di conferma "Vuoi uscire?": "Annulla" chiude la
     * richiesta e lascia il form utilizzabile, "Esci" apre la pagina "API Key".
     */
    @TestFactory
    Stream<DynamicTest> shouldAskConfirmationBeforeLeaving() {
        return webContractValidator.as(userProvider.resolve(UserAlias.COMUNE_1))
                .on(NewAPIKeyPage.class)
                .tests(Stream.of(
                        new WebScenario<>(
                                "richiesta di conferma dell'uscita",
                                page -> page.apiKeysBreadcrumb().click(),
                                page -> {
                                    NewAPIKeyPage.ExitDialog dialog = page.exitDialog();
                                    dialog.title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(EXIT_TITLE));
                                    dialog.message().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(EXIT_MESSAGE));
                                    dialog.cancelButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(CANCEL));
                                    dialog.exitButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(EXIT));
                                }
                        ),

                        new WebScenario<>(
                                "annulla chiude la richiesta e lascia il form utilizzabile",
                                page -> {
                                    page.apiKeysBreadcrumb().click();
                                    page.exitDialog().cancelButton().click();
                                },
                                page -> {
                                    // con la richiesta ancora aperta il campo non sarebbe raggiungibile
                                    page.nameInput().fill(ONLY_SPACE);
                                    page.nameHelperText().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(SPACES_AT_EDGES_MESSAGE));
                                }
                        ),

                        new WebScenario<>(
                                "esci apre la pagina api key",
                                page -> {
                                    page.apiKeysBreadcrumb().click();
                                    page.exitDialog().exitButton().click();
                                },
                                page -> page.apiKeys().assertLoaded()
                        )
                ));
    }

    /**
     * La pagina si apre dalla pagina "API Key" con il pulsante "Genera API Key".
     */
    @TestFactory
    Stream<DynamicTest> shouldOpenFromApiKeys() {
        return webContractValidator.as(userProvider.resolve(UserAlias.COMUNE_1))
                .on(APIKeyPage.class)
                .tests(Stream.of(
                        new WebScenario<>(
                                "genera una api key aperta da api key con genera api key",
                                apiKeys -> apiKeys.generateApiKeyButton().click(),
                                apiKeys -> apiKeys.newApiKey().assertLoaded()
                        )
                ));
    }
}
