package it.pagopa.send.suite.contract.destinatario_pg;

import it.pagopa.send.TestBootApp;
import it.pagopa.send.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.send.common.infrastructure.config.JunitContextConfig;
import it.pagopa.send.common.user.domain.Recipient;
import it.pagopa.send.suite.contract.RecipientNotificationsScenarios;
import it.pagopa.send.web.destinatario_pg.infrastructure.page.NotificationPage;
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
 * Contract test della pagina "In arrivo per &lt;impresa&gt;" della persona giuridica ({@code {baseUrl}/notifiche}).
 * <p>
 * È la stessa pagina "In arrivo" del cittadino, con il nome dell'impresa nel titolo: gli scenari sono quelli comuni di
 * {@link RecipientNotificationsScenarios}, organizzati per:
 * <ul>
 *     <li>testi della pagina e dei filtri;</li>
 *     <li>tabella delle notifiche, paginazione e filtro per tipologia;</li>
 *     <li>banner per attivare il domicilio digitale, mostrato solo se l'impresa non ce l'ha;</li>
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
public class WebNotificationPGContractTest {

    private static final Recipient RECIPIENT = Recipient.PETRARCA;

    // il titolo contiene il nome dell'impresa
    private static final String TITLE = NotificationPage.TITLE_PREFIX + RECIPIENT.getOrganization();

    private final WebBrowserContractValidator webContractValidator;

    // testi della pagina e dei filtri

    @TestFactory
    Stream<DynamicTest> shouldShowNotificationListTexts() {
        return webContractValidator.asRecipient(RECIPIENT)
                .on(NotificationPage.class)
                .tests(RecipientNotificationsScenarios.texts(TITLE));
    }

    // tabella delle notifiche

    @TestFactory
    Stream<DynamicTest> shouldShowNotificationsTable() {
        return webContractValidator.asRecipient(RECIPIENT)
                .on(NotificationPage.class)
                .tests(RecipientNotificationsScenarios.table());
    }

    // banner per attivare il domicilio digitale ("Chiudi" non viene premuto)

    @TestFactory
    Stream<DynamicTest> shouldShowAddDomicileBanner() {
        return webContractValidator.asRecipient(RECIPIENT)
                .on(NotificationPage.class)
                .tests(RecipientNotificationsScenarios.addDomicileBanner());
    }

    // messaggi di validazione dei filtri

    @TestFactory
    Stream<DynamicTest> shouldValidateNotificationFilters() {
        return webContractValidator.asRecipient(RECIPIENT)
                .on(NotificationPage.class)
                .tests(RecipientNotificationsScenarios.filterValidations());
    }
}
