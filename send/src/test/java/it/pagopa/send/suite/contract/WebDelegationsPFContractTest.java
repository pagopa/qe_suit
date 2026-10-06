package it.pagopa.send.suite.contract;

import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.send.TestBootApp;
import it.pagopa.send.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.send.common.infrastructure.config.JunitContextConfig;
import it.pagopa.send.common.user.domain.Recipient;
import it.pagopa.send.web.destinatario_pf.infrastructure.page.DelegationsPFPage;
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

/**
 * Contract test della pagina "Deleghe" del cittadino ({@code {baseUrl}/deleghe}).
 * <p>
 * I test sono organizzati per:
 * <ul>
 *     <li>testi della pagina;</li>
 *     <li>presenza dei pulsanti;</li>
 *     <li>contenuto delle sezioni "I tuoi delegati" e "Deleghe a tuo carico".</li>
 * </ul>
 * Il contenuto delle sezioni dipende dalle deleghe dell'utente: ogni scenario verifica lo stato presente (sezione vuota
 * oppure tabella). Nessuno scenario crea, accetta o revoca deleghe.
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
public class WebDelegationsPFContractTest {

    // testi attesi

    private static final String TITLE = "Deleghe";
    private static final String SUBTITLE = "Qui puoi gestire i tuoi delegati e le deleghe a tuo carico. I primi sono le persone fisiche o giuridiche che hai autorizzato alla visualizzazione e gestione delle tue notifiche, le seconde sono coloro che hanno autorizzato te.";
    private static final String DELEGATES_TITLE = "I tuoi delegati";
    private static final String DELEGATORS_TITLE = "Deleghe a tuo carico";
    private static final String ADD_DELEGATION = "Aggiungi una delega";

    private static final String DELEGATES_EMPTY_STATE = "Non hai delegato nessuno alla visualizzazione delle tue notifiche.";
    private static final String DELEGATORS_EMPTY_STATE = "Non hai deleghe a tuo carico.";
    private static final List<String> TABLE_HEADERS = List.of("Nome", "Inizio delega", "Fine delega", "Permessi", "Stato", "");
    private static final String DATE_PATTERN = "\\d{2}/\\d{2}/\\d{4}";

    private final WebBrowserContractValidator webContractValidator;

    // testi della pagina

    @TestFactory
    Stream<DynamicTest> shouldShowDelegationsTexts() {
        return webContractValidator.asRecipient(Recipient.LUCREZIA)
                .on(DelegationsPFPage.class)
                .tests(textScenarios());
    }

    private Stream<WebScenario<DelegationsPFPage>> textScenarios() {
        return Stream.of(
                new WebScenario<>(
                        "intestazione della pagina",
                        page -> {},
                        page -> {
                            page.title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(TITLE));
                            page.subtitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(SUBTITLE));
                        }
                ),

                new WebScenario<>(
                        "titoli delle sezioni",
                        page -> {},
                        page -> {
                            page.delegatesTitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(DELEGATES_TITLE));
                            page.delegatorsTitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(DELEGATORS_TITLE));
                        }
                )
        );
    }

    // presenza dei pulsanti

    @TestFactory
    Stream<DynamicTest> shouldShowDelegationsButtons() {
        return webContractValidator.asRecipient(Recipient.LUCREZIA)
                .on(DelegationsPFPage.class)
                .tests(buttonScenarios());
    }

    private Stream<WebScenario<DelegationsPFPage>> buttonScenarios() {
        return Stream.of(
                new WebScenario<>(
                        "pulsante aggiungi una delega",
                        page -> {},
                        page -> page.addDelegationButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(ADD_DELEGATION))
                ),

                new WebScenario<>(
                        "il pulsante aggiungi una delega apre la pagina aggiungi una delega",
                        page -> page.addDelegationButton().click(),
                        page -> page.newDelegation().assertLoaded()
                )
        );
    }

    // contenuto delle sezioni: dipende dalle deleghe dell'utente, quindi si verifica lo stato presente

    @TestFactory
    Stream<DynamicTest> shouldShowDelegationsSections() {
        return webContractValidator.asRecipient(Recipient.LUCREZIA)
                .on(DelegationsPFPage.class)
                .tests(sectionScenarios());
    }

    private Stream<WebScenario<DelegationsPFPage>> sectionScenarios() {
        return Stream.of(
                new WebScenario<>(
                        "i tuoi delegati: messaggio di sezione vuota oppure tabella delle deleghe",
                        page -> {},
                        page -> {
                            if (page.delegatesSection().read().contains(DELEGATES_EMPTY_STATE)) {
                                page.delegatesEmptyState().readAndAssert(h -> Assertions.assertThat(h).startsWith(DELEGATES_EMPTY_STATE));
                                page.addDelegateLink().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(ADD_DELEGATION));
                                return;
                            }
                            assertDelegationsTable(page.delegatesTable());
                        }
                ),

                new WebScenario<>(
                        "se vuota, il link della sezione i tuoi delegati apre la pagina aggiungi una delega",
                        page -> {},
                        page -> {
                            if (!page.delegatesSection().read().contains(DELEGATES_EMPTY_STATE)) {
                                // L'utente ha dei delegati: il link non c'è, quindi il test termina senza verificarlo
                                return;
                            }
                            page.addDelegateLink().click();
                            page.newDelegation().assertLoaded();
                        }
                ),

                new WebScenario<>(
                        "deleghe a tuo carico: messaggio di sezione vuota oppure tabella delle deleghe",
                        page -> {},
                        page -> {
                            if (page.delegatorsSection().read().contains(DELEGATORS_EMPTY_STATE)) {
                                page.delegatorsEmptyState().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(DELEGATORS_EMPTY_STATE));
                                return;
                            }
                            assertDelegationsTable(page.delegatorsTable());
                        }
                )
        );
    }

    // intestazioni e, per ogni riga, nome, date, permessi, stato e menu delle azioni
    private void assertDelegationsTable(DelegationsPFPage.DelegationsTable table) {
        table.headers().readAllAndAssert(h -> Assertions.assertThat(h).containsExactlyElementsOf(TABLE_HEADERS));
        table.names().readAllAndAssert(h -> Assertions.assertThat(h).isNotEmpty().allSatisfy(name -> Assertions.assertThat(name).isNotBlank()));
        table.startDates().readAllAndAssert(h -> Assertions.assertThat(h).allSatisfy(date -> Assertions.assertThat(date).matches(DATE_PATTERN)));
        table.endDates().readAllAndAssert(h -> Assertions.assertThat(h).allSatisfy(date -> Assertions.assertThat(date).matches(DATE_PATTERN)));
        table.permissions().readAllAndAssert(h -> Assertions.assertThat(h).allSatisfy(permission -> Assertions.assertThat(permission).isNotBlank()));
        table.states().readAllAndAssert(h -> Assertions.assertThat(h).allSatisfy(state -> Assertions.assertThat(state).isNotBlank()));
        table.menuButtons().readAllAndAssert(h -> Assertions.assertThat(h).hasSameSizeAs(table.names().readAll()));
    }
}
