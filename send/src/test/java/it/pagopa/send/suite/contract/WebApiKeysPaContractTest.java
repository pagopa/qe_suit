package it.pagopa.send.suite.contract;

import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.send.TestBootApp;
import it.pagopa.send.common.api_key.application.ApiKeyUseCase;
import it.pagopa.send.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.send.common.infrastructure.config.JunitContextConfig;
import it.pagopa.send.common.user.domain.UserAlias;
import it.pagopa.send.common.user.domain.UserProvider;
import it.pagopa.send.web.infrastructure.config.WebJUnitSuitConfig;
import it.pagopa.send.web.mittente.infrastructure.page.APIKeyPage;
import it.pagopa.send.web.mittente.infrastructure.page.DashboardPage;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

/**
 * Contract test della pagina "API Key" della PA ({@code {baseUrl}/api-keys}).
 * <p>
 * I test sono organizzati per:
 * <ul>
 *     <li>testi della pagina e pulsante "Genera API Key", con la pagina aperta direttamente dalla rotta;</li>
 *     <li>accesso dalla voce "API Key" del menu laterale;</li>
 *     <li>tabella delle API Key, con una API Key generata dal test.</li>
 * </ul>
 * La tabella compare solo se l'ente ha almeno una API Key: per non dipendere dai dati dell'utenza, il test ne genera
 * una via BFF con un nome univoco, attiva e senza gruppi. Nessuno scenario legge il valore delle API Key, che è un
 * segreto, né usa le azioni del menu. Il messaggio mostrato all'ente senza API Key non è verificato, perché dipende
 * dai dati dell'utenza.
 */
@SpringBootTest(classes = {
        TestBootApp.class,
        JunitContextConfig.class,
        WebJUnitSuitConfig.class
}, properties = "spring.profiles.include=junit")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class WebApiKeysPaContractTest {

    // testi attesi

    private static final String TITLE = "API Key";
    private static final String SUBTITLE = "SEND offre API di invio notifiche e accesso alle informazioni sul loro stato. Qui puoi generare e gestire le API Key da utilizzare per autenticare le richieste dell’ente.";
    private static final String B2B_API_LINK = "invio notifiche e accesso alle informazioni";
    private static final String GENERATED_API_KEYS = "API Key generate";
    private static final String GENERATE_API_KEY = "Genera API Key";
    private static final String[] TABLE_HEADERS = {"Nome", "API Key", "Ultima modifica", "Gruppi", "Stato"};
    private static final String ENABLED = "Attiva";
    // voci del menu azioni di una API Key attiva e senza gruppi
    private static final String[] ENABLED_API_KEY_ACTIONS = {"Visualizza codice", "Ruota", "Blocca"};

    private static final String API_KEY_NAME = "SUIT contract test "
            + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

    private final WebBrowserContractValidator webContractValidator;
    private final UserProvider userProvider;
    private final ApiKeyUseCase apiKeyUseCase;

    /**
     * Testi della pagina aperta direttamente dalla rotta: titolo, sottotitolo con il link alle API B2B, titolo "API Key
     * generate" e pulsante "Genera API Key" abilitato.
     */
    @TestFactory
    Stream<DynamicTest> shouldShowApiKeysTexts() {
        return webContractValidator.as(userProvider.resolve(UserAlias.COMUNE_1))
                .on(APIKeyPage.class)
                .tests(Stream.of(
                        new WebScenario<>(
                                "intestazione della pagina",
                                page -> {},
                                page -> {
                                    page.header().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(TITLE));
                                    page.subtitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(SUBTITLE));
                                    page.b2bApiLink().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(B2B_API_LINK));
                                }
                        ),

                        new WebScenario<>(
                                "api key generate e pulsante genera api key abilitato",
                                page -> {},
                                page -> {
                                    page.generatedApiKeysTitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(GENERATED_API_KEYS));
                                    page.generateApiKeyButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(GENERATE_API_KEY));
                                    Assertions.assertThat(page.generateApiKeyButton().isDisabled()).isFalse();
                                }
                        )
                ));
    }

    /**
     * Accesso dalla voce "API Key" del menu laterale della dashboard.
     */
    @TestFactory
    Stream<DynamicTest> shouldOpenApiKeysFromMenu() {
        return webContractValidator.as(userProvider.resolve(UserAlias.COMUNE_1))
                .on(DashboardPage.class)
                .tests(Stream.of(
                        new WebScenario<>(
                                "api key aperta dalla voce api key del menu",
                                dashboard -> {
                                    dashboard.apiKeysMenuItem().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(TITLE));
                                    dashboard.apiKeysMenuItem().click();
                                },
                                dashboard -> dashboard.apiKeys().assertLoaded()
                        )
                ));
    }

    /**
     * Tabella delle API Key: intestazioni delle colonne e riga dell'API Key generata dal test, con stato "Attiva" e le
     * voci del menu azioni di una API Key attiva.
     */
    @TestFactory
    Stream<DynamicTest> shouldShowApiKeysTable() {
        apiKeyUseCase.createApiKey(API_KEY_NAME);

        return webContractValidator.as(userProvider.resolve(UserAlias.COMUNE_1))
                .on(APIKeyPage.class)
                .tests(Stream.of(
                        new WebScenario<>(
                                "intestazioni della tabella",
                                page -> {},
                                page -> page.tableHeaders().readAllAndAssert(h -> Assertions.assertThat(h).containsExactly(TABLE_HEADERS))
                        ),

                        new WebScenario<>(
                                "api key generata con stato attiva",
                                page -> {},
                                page -> page.apiKeyRow(API_KEY_NAME).status().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(ENABLED))
                        ),

                        new WebScenario<>(
                                "menu azioni di una api key attiva",
                                page -> page.apiKeyRow(API_KEY_NAME).contextMenuButton().click(),
                                page -> page.contextMenuItems().readAllAndAssert(h -> Assertions.assertThat(h).containsExactly(ENABLED_API_KEY_ACTIONS))
                        )
                ));
    }
}
