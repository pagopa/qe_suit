package it.pagopa.send.suite.contract.destinatario_pg;

import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.send.TestBootApp;
import it.pagopa.send.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.send.common.infrastructure.config.JunitContextConfig;
import it.pagopa.send.common.user.domain.Recipient;
import it.pagopa.send.web.destinatario_pg.infrastructure.page.NotificationPage;
import it.pagopa.send.web.destinatario_pg.infrastructure.page.NotificationTimelinePGPage;
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

/**
 * Contract test della pagina "Stato della notifica" della persona giuridica
 * ({@code {baseUrl}/notifiche/<IUN>/dettaglio/timeline}).
 * <p>
 * Ogni scenario apre "In arrivo per &lt;impresa&gt;", il dettaglio dell'ultima notifica a valore legale della lista e
 * da lì "Vai al dettaglio". Si verifica quello che c'è in ogni notifica: breadcrumb e presenza degli eventi con il loro
 * stato. Stati, descrizioni, date e attestazioni dipendono dalla notifica: non vengono verificati.
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
public class WebNotificationTimelinePGContractTest {

    private static final Recipient RECIPIENT = Recipient.PETRARCA;

    // testi attesi

    private static final String NOTIFICATIONS_BREADCRUMB = NotificationPage.TITLE_PREFIX + RECIPIENT.getOrganization();

    private final WebBrowserContractValidator webContractValidator;

    /**
     * Testi della pagina "Stato della notifica", aperta con "Vai al dettaglio" dal dettaglio dell'ultima notifica a
     * valore legale della lista:
     * <ul>
     *     <li>breadcrumb: "In arrivo per &lt;impresa&gt;", l'oggetto della notifica non vuoto e, come voce corrente,
     *     "Stato della notifica";</li>
     *     <li>almeno un evento, ognuno con il suo stato non vuoto.</li>
     * </ul>
     */
    @TestFactory
    Stream<DynamicTest> shouldShowNotificationTimelineTexts() {
        return webContractValidator.asRecipient(RECIPIENT)
                .on(NotificationPage.class)
                .tests(Stream.of(
                        new WebScenario<>(
                                "breadcrumb con il nome dell'impresa, l'oggetto della notifica e stato della notifica",
                                WebNotificationTimelinePGContractTest::openTimeline,
                                page -> {
                                    NotificationTimelinePGPage timeline = loadedTimeline(page);
                                    timeline.notificationsBreadcrumb().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(NOTIFICATIONS_BREADCRUMB));
                                    timeline.notificationBreadcrumb().readAndAssert(h -> Assertions.assertThat(h).isNotBlank());
                                    timeline.currentBreadcrumb().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(NotificationTimelinePGPage.TITLE));
                                }
                        ),

                        new WebScenario<>(
                                "eventi della notifica, ognuno con il suo stato",
                                WebNotificationTimelinePGContractTest::openTimeline,
                                page -> loadedTimeline(page).eventStatuses().readAllAndAssert(h ->
                                        Assertions.assertThat(h).isNotEmpty().allSatisfy(v -> Assertions.assertThat(v).isNotBlank()))
                        )
                ));
    }

    // apre il dettaglio dell'ultima notifica a valore legale della lista e da lì "Stato della notifica"
    private static void openTimeline(NotificationPage page) {
        page.lastLegalNotificationOpenButton().click();
        page.notificationDetail().timelineDetailsButton().click();
    }

    private static NotificationTimelinePGPage loadedTimeline(NotificationPage page) {
        NotificationTimelinePGPage timeline = page.notificationDetail().notificationTimeline();
        timeline.assertLoaded();
        return timeline;
    }
}
