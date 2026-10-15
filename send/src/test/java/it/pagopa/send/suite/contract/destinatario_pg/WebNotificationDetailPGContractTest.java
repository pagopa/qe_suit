package it.pagopa.send.suite.contract.destinatario_pg;

import it.frontend.e2e.framework.web.adapter.model.FindPolicy;
import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.send.TestBootApp;
import it.pagopa.send.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.send.common.infrastructure.config.JunitContextConfig;
import it.pagopa.send.common.user.domain.Recipient;
import it.pagopa.send.web.destinatario_pg.infrastructure.page.NotificationDetailPGPage;
import it.pagopa.send.web.destinatario_pg.infrastructure.page.NotificationPage;
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
 * Contract test della pagina di dettaglio di una notifica a valore legale della persona giuridica
 * ({@code {baseUrl}/notifiche/<IUN>/dettaglio}).
 * <p>
 * Ogni scenario apre "In arrivo per &lt;impresa&gt;" e il dettaglio dell'ultima notifica a valore legale della lista.
 * Si verifica quello che c'è in ogni notifica: breadcrumb, riepilogo e titoli delle sezioni. Banner, pagamenti, avvisi e
 * disservizi dipendono dalla notifica e dall'utente: non vengono verificati.
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
public class WebNotificationDetailPGContractTest {

    private static final Recipient RECIPIENT = Recipient.PETRARCA;

    // testi attesi

    private static final String NOTIFICATIONS_BREADCRUMB = NotificationPage.TITLE_PREFIX + RECIPIENT.getOrganization();
    private static final String LEGAL_VALUE = "Notifica a valore legale";
    // "Notifica depositata il giorno " seguito dalla data
    private static final String DEPOSIT_DATE_PATTERN = "Notifica depositata il giorno \\d{2}/\\d{2}/\\d{4}";
    // es. XYDV-UNLJ-TVND-202305-M-1
    private static final String IUN_PATTERN = "[A-Z]{4}-[A-Z]{4}-[A-Z]{4}-\\d{6}-[A-Z]-\\d";
    private static final String IUN_LABEL = "Codice IUN";
    private static final String LEGAL_DISCLAIMER = "Notifica inviata tramite SEND, la piattaforma disciplinata dall’art. 26 del D.L. 76/2020";
    private static final String DOCUMENTS_TITLE = "Documenti allegati";
    private static final String STATUS_TITLE = "Stato della notifica";
    private static final String TIMELINE_DETAILS = "Vai al dettaglio";
    private static final String NOTIFICATION_DETAILS_TITLE = "Dettaglio della notifica";

    private final WebBrowserContractValidator webContractValidator;

    /**
     * Testi del dettaglio dell'ultima notifica a valore legale della lista:
     * <ul>
     *     <li>breadcrumb: "In arrivo per &lt;impresa&gt;" e, come voce corrente, il titolo della notifica;</li>
     *     <li>riepilogo: etichetta "Notifica a valore legale", logo del mittente, mittente non vuoto, "Notifica
     *     depositata il giorno gg/mm/aaaa", "Codice IUN" con lo IUN nel suo formato e l'informativa su SEND;</li>
     *     <li>sezioni: "Documenti allegati", "Stato della notifica" con lo stato corrente non vuoto e "Vai al
     *     dettaglio" abilitato, "Dettaglio della notifica".</li>
     * </ul>
     */
    @TestFactory
    Stream<DynamicTest> shouldShowNotificationDetailTexts() {
        return webContractValidator.asRecipient(RECIPIENT)
                .on(NotificationPage.class)
                .tests(Stream.of(
                        new WebScenario<>(
                                "breadcrumb con il nome dell'impresa e l'oggetto della notifica",
                                WebNotificationDetailPGContractTest::openDetail,
                                page -> {
                                    NotificationDetailPGPage detail = loadedDetail(page);
                                    detail.notificationsBreadcrumb().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(NOTIFICATIONS_BREADCRUMB));
                                    String title = detail.title().read();
                                    detail.currentBreadcrumb().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(title));
                                }
                        ),

                        new WebScenario<>(
                                "riepilogo: valore legale, mittente, data di deposito, codice IUN e informativa",
                                WebNotificationDetailPGContractTest::openDetail,
                                page -> {
                                    NotificationDetailPGPage detail = loadedDetail(page);
                                    // l'etichetta è resa in maiuscolo dal CSS
                                    detail.legalValueChip().readAndAssert(h -> Assertions.assertThat(h).isEqualToIgnoringCase(LEGAL_VALUE));
                                    Assertions.assertThat(detail.senderLogo().get(FindPolicy.PRESENT)).as("logo del mittente").isPresent();
                                    detail.sender().readAndAssert(h -> Assertions.assertThat(h).isNotBlank());
                                    detail.depositDate().readAndAssert(h -> Assertions.assertThat(h).matches(DEPOSIT_DATE_PATTERN));
                                    detail.iunLabel().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(IUN_LABEL));
                                    detail.iun().readAndAssert(h -> Assertions.assertThat(h).matches(IUN_PATTERN));
                                    detail.legalDisclaimer().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(LEGAL_DISCLAIMER));
                                }
                        ),

                        new WebScenario<>(
                                "sezioni documenti, stato della notifica con stato corrente e vai al dettaglio, dettaglio della notifica",
                                WebNotificationDetailPGContractTest::openDetail,
                                page -> {
                                    NotificationDetailPGPage detail = loadedDetail(page);
                                    detail.documentsTitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(DOCUMENTS_TITLE));
                                    detail.statusTitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(STATUS_TITLE));
                                    detail.currentStatus().readAndAssert(h -> Assertions.assertThat(h).isNotBlank());
                                    detail.timelineDetailsButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(TIMELINE_DETAILS));
                                    // stato iniziale: "Vai al dettaglio" è abilitato
                                    Assertions.assertThat(detail.timelineDetailsButton().isDisabled()).isFalse();
                                    detail.notificationDetailsTitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(NOTIFICATION_DETAILS_TITLE));
                                }
                        )
                ));
    }

    // apre il dettaglio dell'ultima notifica a valore legale della lista
    private static void openDetail(NotificationPage page) {
        page.lastLegalNotificationOpenButton().click();
    }

    private static NotificationDetailPGPage loadedDetail(NotificationPage page) {
        NotificationDetailPGPage detail = page.notificationDetail();
        detail.assertLoaded();
        return detail;
    }
}
