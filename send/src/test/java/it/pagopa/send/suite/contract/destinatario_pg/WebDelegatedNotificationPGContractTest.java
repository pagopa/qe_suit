package it.pagopa.send.suite.contract.destinatario_pg;

import it.frontend.e2e.framework.web.adapter.model.FindPolicy;
import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.send.TestBootApp;
import it.pagopa.send.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.send.common.infrastructure.config.JunitContextConfig;
import it.pagopa.send.common.user.domain.Recipient;
import it.pagopa.send.suite.contract.RecipientNotificationsScenarios;
import it.pagopa.send.web.destinatario_pg.infrastructure.page.DelegatedNotificationPage;
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
 * Contract test della pagina "In arrivo dalle deleghe" della persona giuridica ({@code {baseUrl}/notifiche-delegato}).
 * <p>
 * I test sono organizzati per:
 * <ul>
 *     <li>testi della pagina e dei filtri, comuni alle altre liste ({@link RecipientNotificationsScenarios});</li>
 *     <li>tabella delle notifiche delegate e paginazione;</li>
 *     <li>messaggi di validazione dei filtri, comuni alle altre liste.</li>
 * </ul>
 * La tabella dipende dalle deleghe affidate all'impresa: ogni scenario la verifica se presente. Gli scenari non aprono
 * le notifiche, perché aprirle le segna come lette.
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
public class WebDelegatedNotificationPGContractTest {

    private static final Recipient RECIPIENT = Recipient.PETRARCA;

    // tabella
    private static final List<String> HEADERS = List.of("Data", "Mittente", "Oggetto", "Destinatario", "Codice IUN", "");
    // codice fiscale di un destinatario: 16 caratteri, o 11 cifre per un'impresa
    private static final String TAX_ID_PATTERN = "[A-Z0-9]{16}|\\d{11}";

    private final WebBrowserContractValidator webContractValidator;

    // testi della pagina e dei filtri

    @TestFactory
    Stream<DynamicTest> shouldShowDelegatedNotificationListTexts() {
        return webContractValidator.asRecipient(RECIPIENT)
                .on(DelegatedNotificationPage.class)
                .tests(RecipientNotificationsScenarios.filterTexts(DelegatedNotificationPage.TITLE));
    }

    // tabella delle notifiche delegate

    @TestFactory
    Stream<DynamicTest> shouldShowDelegatedNotificationsTable() {
        return webContractValidator.asRecipient(RECIPIENT)
                .on(DelegatedNotificationPage.class)
                .tests(Stream.of(
                        new WebScenario<>(
                                "se presenti, intestazioni e righe con data, mittente, oggetto, destinatario, IUN e apri",
                                page -> {},
                                page -> {
                                    if (!page.content().read().contains(RecipientNotificationsScenarios.OPEN)) {
                                        // Nessuna notifica delegata: la tabella non c'è, quindi il test termina
                                        return;
                                    }
                                    DelegatedNotificationPage.DelegatedNotificationsTable table = page.notificationsTable();
                                    table.headers().readAllAndAssert(h -> Assertions.assertThat(h).containsExactlyElementsOf(HEADERS));
                                    int rows = table.openButtons().readAll().size();
                                    table.dates().readAllAndAssert(h -> Assertions.assertThat(h).hasSize(rows).allSatisfy(v -> Assertions.assertThat(v).matches(RecipientNotificationsScenarios.DATE_PATTERN)));
                                    table.senders().readAllAndAssert(h -> Assertions.assertThat(h).hasSize(rows).allSatisfy(v -> Assertions.assertThat(v).isNotBlank()));
                                    table.subjects().readAllAndAssert(h -> Assertions.assertThat(h).hasSize(rows).allSatisfy(v -> Assertions.assertThat(v).isNotBlank()));
                                    // una notifica con più destinatari ha un codice fiscale per riga nella stessa cella
                                    table.recipients().readAllAndAssert(h -> Assertions.assertThat(h).hasSize(rows).allSatisfy(v ->
                                            Assertions.assertThat(v.split("\\R")).allSatisfy(taxId -> Assertions.assertThat(taxId.trim()).matches(TAX_ID_PATTERN))));
                                    table.iuns().readAllAndAssert(h -> Assertions.assertThat(h).hasSize(rows).allSatisfy(v -> Assertions.assertThat(v).matches(RecipientNotificationsScenarios.IUN_PATTERN)));
                                    table.openButtons().readAllAndAssert(h -> Assertions.assertThat(h).allSatisfy(v -> Assertions.assertThat(v).isEqualTo(RecipientNotificationsScenarios.OPEN)));
                                }
                        ),

                        new WebScenario<>(
                                "se presenti, righe per pagina vale 10 e propone 10, 20 e 50",
                                page -> {},
                                page -> {
                                    if (!page.content().read().contains(RecipientNotificationsScenarios.OPEN)) {
                                        // Nessuna notifica delegata: la paginazione non c'è, quindi il test termina
                                        return;
                                    }
                                    DelegatedNotificationPage.DelegatedNotificationsTable table = page.notificationsTable();
                                    table.rowsPerPageButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(RecipientNotificationsScenarios.ROWS_PER_PAGE.get(0)));
                                    Assertions.assertThat(table.openButtons().readAll()).hasSizeLessThanOrEqualTo(10);
                                    table.rowsPerPageButton().click();
                                    page.rowsPerPageOptions().readAllAndAssert(h -> Assertions.assertThat(h).containsExactlyElementsOf(RecipientNotificationsScenarios.ROWS_PER_PAGE));
                                }
                        ),

                        new WebScenario<>(
                                "se ci sono più pagine, pulsanti di pagina sulla prima con indietro disabilitato",
                                page -> {},
                                page -> {
                                    DelegatedNotificationPage.DelegatedNotificationsTable table = page.notificationsTable();
                                    if (!page.content().read().contains(RecipientNotificationsScenarios.OPEN) || table.nextPageButton().get(FindPolicy.PRESENT).isEmpty()) {
                                        // Le notifiche stanno in una pagina: i pulsanti di pagina non ci sono, quindi il test termina
                                        return;
                                    }
                                    Assertions.assertThat(table.previousPageButton().isDisabled()).isTrue();
                                    table.firstPageButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("1"));
                                }
                        )
                ));
    }

    // messaggi di validazione dei filtri

    @TestFactory
    Stream<DynamicTest> shouldValidateDelegatedNotificationFilters() {
        return webContractValidator.asRecipient(RECIPIENT)
                .on(DelegatedNotificationPage.class)
                .tests(RecipientNotificationsScenarios.filterValidations());
    }
}
