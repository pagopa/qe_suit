package it.pagopa.send.suite.contract.destinatario_pg;

import it.frontend.e2e.framework.web.adapter.model.FindPolicy;
import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.send.TestBootApp;
import it.pagopa.send.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.send.common.infrastructure.config.JunitContextConfig;
import it.pagopa.send.common.user.domain.Recipient;
import it.pagopa.send.web.destinatario_pg.infrastructure.page.ApiIntegrationPage;
import it.pagopa.send.web.infrastructure.config.WebJUnitSuitConfig;
import it.pagopa.utils.async.PollingUtils;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;

import java.time.Duration;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Contract test della pagina "Integrazione API" dell'impresa ({@code {baseUrl}/integrazione-api}).
 * <p>
 * I test sono organizzati per:
 * <ul>
 *     <li>testi della pagina;</li>
 *     <li>tabelle "Chiave pubblica" e "Chiave personale": colonne, formato delle righe e "Copia";</li>
 *     <li>menu delle chiavi, che cambia con lo stato;</li>
 *     <li>finestra di "Visualizza codice" delle chiavi attive.</li>
 * </ul>
 * Le chiavi dell'impresa possono cambiare: ogni scenario verifica le righe che trova e termina se manca la tabella o la
 * chiave nello stato che cerca; senza chiavi pubbliche verifica il messaggio al posto della tabella. Nessuno scenario
 * preme "Elimina" o "Registra chiave pubblica".
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
public class WebApiIntegrationPGContractTest {

    private static final Recipient RECIPIENT = Recipient.PETRARCA;

    // testi attesi
    private static final String TITLE = "Integrazione API";
    private static final String SUBTITLE = "Genera e gestisci le chiavi per abilitare l'integrazione sicura tra i gestionali della tua impresa e SEND.";
    private static final String PUBLIC_KEYS_TITLE = "Chiave pubblica";
    private static final String PERSONAL_KEYS_TITLE = "Chiave personale";
    private static final String PUBLIC_KEYS_EMPTY = "Ancora nessuna chiave pubblica. Registra una chiave pubblica per abilitare l’integrazione.";
    private static final String REGISTER_PUBLIC_KEY = "Registra chiave pubblica";
    // l'ultima colonna, quella del menu, non ha intestazione
    private static final List<String> PUBLIC_KEYS_HEADERS = List.of("Nome", "Valore", "Scadenza", "Stato", "");
    private static final List<String> PERSONAL_KEYS_HEADERS = List.of("Nome", "Valore", "Ultima modifica", "Stato", "");
    private static final String ACTIVE = "Attiva";
    private static final String ROTATED = "Ruotata";
    private static final String BLOCKED = "Bloccata";
    private static final String VIEW_CODE = "Visualizza codice";
    private static final String DELETE = "Elimina";

    // finestra di "Visualizza codice"
    private static final String PUBLIC_CODE_TITLE = "Visualizza codice";
    private static final String PUBLIC_CODE_SUBTITLE = "Copia i codici di cui hai bisogno.";
    private static final List<String> PUBLIC_CODE_LABELS = List.of("Chiave personale", "Kid", "Issuer");
    private static final String PUBLIC_CODE_CLOSE = "Chiudi";
    private static final String PERSONAL_CODE_TITLE = "La tua chiave personale";
    private static final String PERSONAL_CODE_SUBTITLE = "Puoi usarla per autenticarti in piattaforma e integrare SEND. Puoi copiare la tua chiave personale in ogni momento nella sezione “Integrazione API”.";
    private static final List<String> PERSONAL_CODE_LABELS = List.of("Chiave personale");
    private static final String PERSONAL_CODE_CLOSE = "Ok, ho capito";

    // formati
    private static final String UUID = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}";
    private static final String DATE = "\\d{2}/\\d{2}/\\d{4}";

    private final WebBrowserContractValidator webContractValidator;

    // testi della pagina

    @TestFactory
    Stream<DynamicTest> shouldShowApiIntegrationTexts() {
        return webContractValidator.asRecipient(RECIPIENT)
                .on(ApiIntegrationPage.class)
                .tests(Stream.of(
                        new WebScenario<>(
                                "titolo, sottotitolo e titoli delle sezioni",
                                page -> {},
                                page -> {
                                    page.title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(TITLE));
                                    page.subtitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(SUBTITLE));
                                    page.publicKeysTitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(PUBLIC_KEYS_TITLE));
                                    if (!page.content().read().contains(PERSONAL_KEYS_TITLE)) {
                                        // L'impresa non ha chiavi: la sezione della chiave personale non c'è, quindi il test termina
                                        return;
                                    }
                                    page.personalKeysTitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(PERSONAL_KEYS_TITLE));
                                }
                        ),

                        new WebScenario<>(
                                "se non ci sono chiavi pubbliche, messaggio e registra chiave pubblica non premuto",
                                page -> {},
                                page -> {
                                    if (hasTable(page, PUBLIC_KEYS_HEADERS)) {
                                        // L'impresa ha chiavi pubbliche: al posto del messaggio c'è la tabella, quindi il test termina
                                        return;
                                    }
                                    page.publicKeysEmptyState().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(PUBLIC_KEYS_EMPTY));
                                    page.registerPublicKeyButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(REGISTER_PUBLIC_KEY));
                                }
                        )
                ));
    }

    // tabelle delle chiavi

    @TestFactory
    Stream<DynamicTest> shouldShowKeysTables() {
        return webContractValidator.asRecipient(RECIPIENT)
                .on(ApiIntegrationPage.class)
                .tests(Stream.of(
                        tableScenario("chiavi pubbliche", ApiIntegrationPage::publicKeysTable, PUBLIC_KEYS_HEADERS),
                        tableScenario("chiavi personali", ApiIntegrationPage::personalKeysTable, PERSONAL_KEYS_HEADERS)
                ));
    }

    // menu delle chiavi: "Elimina" non viene mai premuto

    @TestFactory
    Stream<DynamicTest> shouldShowKeyMenus() {
        return webContractValidator.asRecipient(RECIPIENT)
                .on(ApiIntegrationPage.class)
                .tests(Stream.of(
                        menuScenario("chiave pubblica attiva", ApiIntegrationPage::publicKeysTable, PUBLIC_KEYS_HEADERS, ACTIVE,
                                ApiIntegrationPage.KeysTable::activeKeyMenuButton, List.of(VIEW_CODE)),
                        menuScenario("chiave pubblica ruotata", ApiIntegrationPage::publicKeysTable, PUBLIC_KEYS_HEADERS, ROTATED,
                                ApiIntegrationPage.KeysTable::rotatedKeyMenuButton, List.of(VIEW_CODE, DELETE)),
                        menuScenario("chiave pubblica bloccata", ApiIntegrationPage::publicKeysTable, PUBLIC_KEYS_HEADERS, BLOCKED,
                                ApiIntegrationPage.KeysTable::blockedKeyMenuButton, List.of(VIEW_CODE, DELETE)),
                        menuScenario("chiave personale attiva", ApiIntegrationPage::personalKeysTable, PERSONAL_KEYS_HEADERS, ACTIVE,
                                ApiIntegrationPage.KeysTable::activeKeyMenuButton, List.of(VIEW_CODE)),
                        menuScenario("chiave personale ruotata", ApiIntegrationPage::personalKeysTable, PERSONAL_KEYS_HEADERS, ROTATED,
                                ApiIntegrationPage.KeysTable::rotatedKeyMenuButton, List.of(VIEW_CODE, DELETE)),
                        menuScenario("chiave personale bloccata", ApiIntegrationPage::personalKeysTable, PERSONAL_KEYS_HEADERS, BLOCKED,
                                ApiIntegrationPage.KeysTable::blockedKeyMenuButton, List.of(VIEW_CODE, DELETE))
                ));
    }

    // finestra di "Visualizza codice" delle chiavi attive

    @TestFactory
    Stream<DynamicTest> shouldShowViewCodeDialog() {
        return webContractValidator.asRecipient(RECIPIENT)
                .on(ApiIntegrationPage.class)
                .tests(Stream.of(
                        new WebScenario<>(
                                "se c'è una chiave pubblica attiva, visualizza codice mostra codice, Kid e Issuer e chiudi chiude la finestra",
                                page -> {},
                                page -> {
                                    String value = openViewCode(page, ApiIntegrationPage::publicKeysTable, PUBLIC_KEYS_HEADERS);
                                    if (value == null) {
                                        // L'impresa non ha una chiave pubblica attiva: il test termina
                                        return;
                                    }
                                    ApiIntegrationPage.ViewCodeDialog dialog = page.viewCodeDialog();
                                    dialog.title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(PUBLIC_CODE_TITLE));
                                    dialog.subtitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(PUBLIC_CODE_SUBTITLE));
                                    dialog.fieldLabels().readAllAndAssert(h -> Assertions.assertThat(h).containsExactlyElementsOf(PUBLIC_CODE_LABELS));
                                    // il codice è il valore della riga
                                    Assertions.assertThat(dialog.valueField().read()).isEqualTo(value);
                                    Assertions.assertThat(dialog.kidField().read()).matches(UUID);
                                    // l'Issuer è "PG-" seguito dall'identificativo dell'impresa
                                    Assertions.assertThat(dialog.issuerField().read()).matches("PG-" + UUID);
                                    dialog.copyButtons().readAllAndAssert(h -> Assertions.assertThat(h).hasSize(PUBLIC_CODE_LABELS.size()));
                                    dialog.closeButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(PUBLIC_CODE_CLOSE));
                                    assertClosedBy(dialog);
                                }
                        ),

                        new WebScenario<>(
                                "se c'è una chiave personale attiva, visualizza codice mostra il codice e ok, ho capito chiude la finestra",
                                page -> {},
                                page -> {
                                    String value = openViewCode(page, ApiIntegrationPage::personalKeysTable, PERSONAL_KEYS_HEADERS);
                                    if (value == null) {
                                        // L'impresa non ha una chiave personale attiva: il test termina
                                        return;
                                    }
                                    ApiIntegrationPage.ViewCodeDialog dialog = page.viewCodeDialog();
                                    dialog.title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(PERSONAL_CODE_TITLE));
                                    dialog.subtitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(PERSONAL_CODE_SUBTITLE));
                                    dialog.fieldLabels().readAllAndAssert(h -> Assertions.assertThat(h).containsExactlyElementsOf(PERSONAL_CODE_LABELS));
                                    Assertions.assertThat(dialog.valueField().read()).isEqualTo(value);
                                    dialog.copyButtons().readAllAndAssert(h -> Assertions.assertThat(h).hasSize(PERSONAL_CODE_LABELS.size()));
                                    dialog.closeButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(PERSONAL_CODE_CLOSE));
                                    assertClosedBy(dialog);
                                }
                        )
                ));
    }

    /**
     * Colonne della tabella e, per ogni riga, formato di valore, data e stato; "Copia" è attivo solo per le chiavi
     * attive. Se la tabella non c'è lo scenario termina.
     */
    private WebScenario<ApiIntegrationPage> tableScenario(String name, Function<ApiIntegrationPage, ApiIntegrationPage.KeysTable> table,
                                                          List<String> headers) {
        return new WebScenario<>(
                "se presente, tabella delle " + name + ": colonne, formato delle righe e copia solo per le chiavi attive",
                page -> {},
                page -> {
                    if (!hasTable(page, headers)) {
                        // L'impresa non ha chiavi di questo tipo: la tabella non c'è, quindi il test termina
                        return;
                    }
                    ApiIntegrationPage.KeysTable keys = table.apply(page);
                    keys.headers().readAllAndAssert(h -> Assertions.assertThat(h).containsExactlyElementsOf(headers));
                    List<String> states = keys.states().readAll();
                    Assertions.assertThat(states).isNotEmpty().allSatisfy(s -> Assertions.assertThat(s).isIn(ACTIVE, ROTATED, BLOCKED));
                    keys.names().readAllAndAssert(h -> Assertions.assertThat(h).hasSize(states.size()).allSatisfy(n -> Assertions.assertThat(n).isNotBlank()));
                    keys.values().readAllAndAssert(h -> Assertions.assertThat(h).hasSize(states.size()).allSatisfy(v -> Assertions.assertThat(v).matches(UUID)));
                    keys.dates().readAllAndAssert(h -> Assertions.assertThat(h).hasSize(states.size()).allSatisfy(d -> Assertions.assertThat(d).matches(DATE)));
                    // "Copia" è attivo su ogni chiave attiva e disabilitato sulle altre
                    long active = states.stream().filter(ACTIVE::equals).count();
                    if (active > 0) {
                        keys.enabledCopyButtons().readAllAndAssert(h -> Assertions.assertThat(h).hasSize((int) active));
                    }
                    Assertions.assertThat(keys.inactiveKeyEnabledCopyButton().get(FindPolicy.PRESENT)).isEmpty();
                }
        );
    }

    /**
     * Apre il menu della prima chiave nello stato indicato e ne controlla le voci, senza premerle. Se la tabella o la
     * chiave non ci sono lo scenario termina.
     */
    private WebScenario<ApiIntegrationPage> menuScenario(String name, Function<ApiIntegrationPage, ApiIntegrationPage.KeysTable> table,
                                                         List<String> headers, String state,
                                                         Function<ApiIntegrationPage.KeysTable, Button> menuButton, List<String> items) {
        return new WebScenario<>(
                "se presente, menu della " + name + " con " + String.join(" e ", items).toLowerCase(),
                page -> {},
                page -> {
                    if (!hasTable(page, headers) || !table.apply(page).states().readAll().contains(state)) {
                        // L'impresa non ha una chiave in questo stato: il test termina
                        return;
                    }
                    menuButton.apply(table.apply(page)).click();
                    page.keyMenu().items().readAllAndAssert(h -> Assertions.assertThat(h).containsExactlyElementsOf(items));
                }
        );
    }

    // apre "Visualizza codice" della prima chiave attiva e ne restituisce il valore; null se la chiave non c'è
    private String openViewCode(ApiIntegrationPage page, Function<ApiIntegrationPage, ApiIntegrationPage.KeysTable> table, List<String> headers) {
        if (!hasTable(page, headers) || !table.apply(page).states().readAll().contains(ACTIVE)) {
            return null;
        }
        ApiIntegrationPage.KeysTable keys = table.apply(page);
        String value = keys.activeKeyValue().read();
        keys.activeKeyMenuButton().click();
        page.keyMenu().viewCodeItem().click();
        return value;
    }

    // la terza colonna distingue le due tabelle: la sua intestazione c'è solo se c'è la tabella
    private boolean hasTable(ApiIntegrationPage page, List<String> headers) {
        return page.content().read().contains(headers.get(2));
    }

    // preme il pulsante di chiusura e attende che la finestra si chiuda con l'animazione
    private void assertClosedBy(ApiIntegrationPage.ViewCodeDialog dialog) {
        dialog.closeButton().click();
        PollingUtils.pollUntil(() -> dialog.closeButton().get(FindPolicy.PRESENT).isEmpty(), closed -> closed,
                Duration.ofSeconds(5), Duration.ofMillis(500), "la finestra di Visualizza codice è ancora aperta");
    }
}
