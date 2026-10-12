package it.pagopa.send.suite.contract.destinatario_pg;

import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.send.TestBootApp;
import it.pagopa.send.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.send.common.infrastructure.config.JunitContextConfig;
import it.pagopa.send.common.user.domain.Recipient;
import it.pagopa.send.web.destinatario_pg.infrastructure.page.DelegationsPGPage;
import it.pagopa.send.web.destinatario_pg.infrastructure.page.OrganizationAuthorizedRepresentativesPage;
import it.pagopa.send.web.destinatario_pg.infrastructure.page.OrganizationDelegationsPage;
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
 * Contract test della pagina "Deleghe" della persona giuridica, nelle due schede "Deleghe a carico dell'impresa"
 * ({@code {baseUrl}/deleghe/a-carico}) e "Delegati dall'impresa" ({@code {baseUrl}/deleghe/delegati}).
 * <p>
 * I test sono organizzati per:
 * <ul>
 *     <li>testi della pagina e passaggio tra le schede;</li>
 *     <li>scheda "Deleghe a carico dell'impresa";</li>
 *     <li>scheda "Delegati dall'impresa" e "Aggiungi una delega".</li>
 * </ul>
 * Il contenuto delle schede dipende dalle deleghe dell'impresa: ogni scenario verifica il messaggio di scheda vuota se
 * presente. Nessuno scenario crea, accetta o revoca deleghe.
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
public class WebDelegationsPGContractTest {

    private static final Recipient RECIPIENT = Recipient.PETRARCA;

    // testi attesi

    private static final String TITLE = "Deleghe";
    private static final String SUBTITLE = "Qui si possono gestire i delegati dell’impresa e le deleghe a suo carico. I primi sono quelli che l’impresa delega alla lettura delle sue notifiche. Le seconde sono le deleghe affidate all’impresa.";
    private static final List<String> TABS = List.of(OrganizationDelegationsPage.TAB, OrganizationAuthorizedRepresentativesPage.TAB);
    private static final String ADD_DELEGATION = "Aggiungi una delega";

    // messaggi di scheda vuota, con il nome dell'impresa
    private static final String DELEGATORS_EMPTY_STATE = "Non c'è nessuna delega a carico di " + RECIPIENT.getOrganization() + ".";
    private static final String DELEGATES_EMPTY_STATE = RECIPIENT.getOrganization() + " non ha delegato nessuno alla lettura delle sue notifiche. Aggiungi una delega.";
    // parti dei messaggi che riconoscono la scheda vuota anche se il resto del testo cambia
    private static final String DELEGATORS_EMPTY_MARKER = "nessuna delega a carico";
    private static final String DELEGATES_EMPTY_MARKER = "non ha delegato nessuno";

    private final WebBrowserContractValidator webContractValidator;

    // testi della pagina e passaggio tra le schede

    @TestFactory
    Stream<DynamicTest> shouldShowDelegationsTexts() {
        return webContractValidator.asRecipient(RECIPIENT)
                .on(OrganizationDelegationsPage.class)
                .tests(Stream.of(
                        new WebScenario<>(
                                "intestazione della pagina e schede",
                                page -> {},
                                page -> {
                                    page.title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(TITLE));
                                    page.subtitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(SUBTITLE));
                                    page.tabs().readAllAndAssert(h -> Assertions.assertThat(h).containsExactlyElementsOf(TABS));
                                }
                        ),

                        new WebScenario<>(
                                "la scheda delegati dall'impresa apre la scheda e torna alle deleghe a carico",
                                page -> page.delegatesTab().click(),
                                page -> {
                                    page.delegatesByCompany().assertLoaded();
                                    page.delegatorsTab().click();
                                    page.delegationsOfTheCompany().assertLoaded();
                                }
                        )
                ));
    }

    // scheda "Deleghe a carico dell'impresa"

    @TestFactory
    Stream<DynamicTest> shouldShowDelegationsOfTheCompany() {
        return webContractValidator.asRecipient(RECIPIENT)
                .on(OrganizationDelegationsPage.class)
                .tests(Stream.of(
                        new WebScenario<>(
                                "titolo della sezione e, se vuota, messaggio con il nome dell'impresa",
                                page -> {},
                                page -> {
                                    page.sectionTitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(OrganizationDelegationsPage.TAB));
                                    assertEmptyStateIfPresent(page, DELEGATORS_EMPTY_MARKER, DELEGATORS_EMPTY_STATE);
                                }
                        )
                ));
    }

    // scheda "Delegati dall'impresa" e "Aggiungi una delega"

    @TestFactory
    Stream<DynamicTest> shouldShowDelegatesByCompany() {
        return webContractValidator.asRecipient(RECIPIENT)
                .on(OrganizationAuthorizedRepresentativesPage.class)
                .tests(Stream.of(
                        new WebScenario<>(
                                "titolo della sezione, aggiungi una delega e, se vuota, messaggio con il nome dell'impresa",
                                page -> {},
                                page -> {
                                    page.sectionTitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(OrganizationAuthorizedRepresentativesPage.TAB));
                                    page.addDelegationButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(ADD_DELEGATION));
                                    assertEmptyStateIfPresent(page, DELEGATES_EMPTY_MARKER, DELEGATES_EMPTY_STATE);
                                }
                        ),

                        new WebScenario<>(
                                "il pulsante aggiungi una delega apre la pagina aggiungi una delega",
                                page -> page.addDelegationButton().click(),
                                page -> page.newDelegation().assertLoaded()
                        ),

                        new WebScenario<>(
                                "se vuota, il link aggiungi una delega apre la pagina aggiungi una delega",
                                page -> {},
                                page -> {
                                    if (!page.content().read().contains(DELEGATES_EMPTY_MARKER)) {
                                        // L'impresa ha dei delegati: il link non c'è, quindi il test termina senza verificarlo
                                        return;
                                    }
                                    page.addDelegateLink().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(ADD_DELEGATION));
                                    page.addDelegateLink().click();
                                    page.newDelegation().assertLoaded();
                                }
                        )
                ));
    }

    // TODO verificare le tabelle delle deleghe (intestazioni e righe) quando un utente PG di test avrà deleghe:
    //  al 12/10/2026 nessun utente PG provato su test, dev e uat ne ha
    // se la scheda è vuota ne verifica il messaggio; con deleghe la scheda mostra una tabella, non verificata perché
    // l'impresa di test non ne ha
    private static void assertEmptyStateIfPresent(DelegationsPGPage page, String marker, String emptyState) {
        if (!page.content().read().contains(marker)) {
            return;
        }
        page.emptyState().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(emptyState));
    }
}
