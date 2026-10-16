package it.pagopa.send.suite.contract.destinatario_pg;

import it.frontend.e2e.framework.web.adapter.model.FindPolicy;
import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.send.TestBootApp;
import it.pagopa.send.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.send.common.infrastructure.config.JunitContextConfig;
import it.pagopa.send.common.journey.application.SendJourney;
import it.pagopa.send.common.kernel.context.CurrentUserSession;
import it.pagopa.send.common.legal_notification.domain.LegalNotificationDomain;
import it.pagopa.send.common.legal_notification.domain.NotificationStatus;
import it.pagopa.send.common.legal_notification.domain.RecipientSpec;
import it.pagopa.send.common.legal_notification.infrastructure.factory.RecipientSpecFactory;
import it.pagopa.send.common.user.domain.Recipient;
import it.pagopa.send.common.user.domain.Tenant;
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

import java.util.Map;
import java.util.stream.Stream;

/**
 * Contract test della pagina di dettaglio di una notifica a valore legale della persona giuridica
 * ({@code {baseUrl}/notifiche/<IUN>/dettaglio}).
 * <p>
 * Ogni scenario apre "In arrivo per &lt;impresa&gt;" e il dettaglio dell'ultima notifica a valore legale della lista.
 * Se nella prima pagina della lista non ci sono notifiche a valore legale, ne viene inviata una all'impresa (mittente
 * Grossini) e si apre quella, attendendo che compaia nella lista: la notifica è creata una sola volta per esecuzione,
 * anche con gli scenari in parallelo.
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

    private static final Object LOCK = new Object();
    private static String createdIun;

    private final WebBrowserContractValidator webContractValidator;
    private final SendJourney sendJourney;
    private final RecipientSpecFactory recipientSpecFactory;
    private final CurrentUserSession currentUserSession;

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
                                this::openDetail,
                                page -> {
                                    NotificationDetailPGPage detail = loadedDetail(page);
                                    detail.notificationsBreadcrumb().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(NOTIFICATIONS_BREADCRUMB));
                                    String title = detail.title().read();
                                    detail.currentBreadcrumb().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(title));
                                }
                        ),

                        new WebScenario<>(
                                "riepilogo: valore legale, mittente, data di deposito, codice IUN e informativa",
                                this::openDetail,
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
                                this::openDetail,
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

    // apre il dettaglio dell'ultima notifica a valore legale della lista; se non ce n'è, la crea e apre quella
    private void openDetail(NotificationPage page) {
        if (page.hasLegalNotification()) {
            page.lastLegalNotificationOpenButton().click();
        } else {
            page.openNewLegalNotification(ensureLegalNotification());
        }
    }

    // gli scenari girano in parallelo: la notifica viene creata una sola volta per esecuzione e ne viene restituito lo IUN
    private String ensureLegalNotification() {
        synchronized (LOCK) {
            if (createdIun != null) {
                return createdIun;
            }
            // destinatario: l'impresa, con la sua denominazione invece del nome della persona che accede
            RecipientSpec recipient = recipientSpecFactory.build(RECIPIENT, Map.of(
                    "denomination", RECIPIENT.getOrganization(),
                    "physicalAddress_address", "Via@ok_890",
                    "physicalAddress_municipality", "COLLELUNGO",
                    "physicalAddress_province", "TR",
                    "physicalAddress_zip", "05010",
                    "pagoPA_number", "0",
                    "F24_number", "0"
            ));
            try {
                createdIun = sendJourney
                        .withSender(Tenant.GROSSINI)
                        .prepareNotification(Map.of(
                                "subject", "Notifica di test per il dettaglio PG",
                                "physicalCommunication", "REGISTERED_LETTER_890",
                                "feePolicy", "FLAT_RATE"
                        ))
                        .withRecipient(recipient)
                        .sendNotification(Tenant.GROSSINI, NotificationStatus.ACCEPTED)
                        .get(LegalNotificationDomain.class)
                        .getIun();
                return createdIun;
            } finally {
                // l'invio imposta il mittente sul thread: senza azzerarlo, gli scenari successivi sullo stesso thread
                // aprirebbero il portale come Grossini invece che come l'impresa
                currentUserSession.setSender(null);
            }
        }
    }

    private static NotificationDetailPGPage loadedDetail(NotificationPage page) {
        NotificationDetailPGPage detail = page.notificationDetail();
        detail.assertLoaded();
        return detail;
    }
}
