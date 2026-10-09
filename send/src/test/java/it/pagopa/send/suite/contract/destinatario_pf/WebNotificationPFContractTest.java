package it.pagopa.send.suite.contract.destinatario_pf;

import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.send.TestBootApp;
import it.pagopa.send.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.send.common.infrastructure.config.JunitContextConfig;
import it.pagopa.send.common.user.domain.Recipient;
import it.pagopa.send.suite.contract.RecipientNotificationsScenarios;
import it.pagopa.send.web.destinatario_pf.infrastructure.page.NotificationPFPage;
import it.pagopa.send.web.infrastructure.config.WebJUnitSuitConfig;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;

import java.util.stream.Stream;

/**
 * Contract test della pagina "In arrivo" del cittadino ({@code {baseUrl}/notifiche}).
 * <p>
 * Gli scenari comuni alla pagina dell'impresa sono in {@link RecipientNotificationsScenarios}; i test sono organizzati per:
 * <ul>
 *     <li>testi della pagina e dei filtri;</li>
 *     <li>tabella delle notifiche, paginazione e filtro per tipologia;</li>
 *     <li>banner per attivare il domicilio digitale, mostrato solo a chi non ce l'ha, e wizard che apre;</li>
 *     <li>messaggi di validazione dei filtri.</li>
 * </ul>
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
public class WebNotificationPFContractTest {

    private static final String TITLE = "In arrivo";

    private final WebBrowserContractValidator webContractValidator;

    // testi della pagina e dei filtri

    @TestFactory
    Stream<DynamicTest> shouldShowNotificationListTexts() {
        return webContractValidator.asRecipient(Recipient.LUCREZIA)
                .on(NotificationPFPage.class)
                .tests(RecipientNotificationsScenarios.texts(TITLE));
    }

    // tabella delle notifiche

    @TestFactory
    Stream<DynamicTest> shouldShowNotificationsTable() {
        return webContractValidator.asRecipient(Recipient.LUCREZIA)
                .on(NotificationPFPage.class)
                .tests(RecipientNotificationsScenarios.table());
    }

    // banner per attivare il domicilio digitale ("Chiudi" non viene premuto)

    @TestFactory
    Stream<DynamicTest> shouldShowAddDomicileBanner() {
        return webContractValidator.asRecipient(Recipient.LUCREZIA)
                .on(NotificationPFPage.class)
                .tests(Stream.concat(
                        RecipientNotificationsScenarios.<NotificationPFPage>addDomicileBanner(),
                        Stream.of(new WebScenario<>(
                                "se presente, attiva domicilio digitale apre il wizard di attivazione",
                                page -> {},
                                page -> {
                                    if (!page.content().read().contains(RecipientNotificationsScenarios.BANNER_TITLE)) {
                                        // L'utente ha un domicilio digitale: il banner non c'è, quindi il test termina
                                        return;
                                    }
                                    page.addDomicileBanner().activateButton().click();
                                    page.digitalDomicileActivation().assertLoaded();
                                }
                        ))
                ));
    }

    // messaggi di validazione dei filtri

    @TestFactory
    Stream<DynamicTest> shouldValidateNotificationFilters() {
        return webContractValidator.asRecipient(Recipient.LUCREZIA)
                .on(NotificationPFPage.class)
                .tests(RecipientNotificationsScenarios.filterValidations());
    }
}
