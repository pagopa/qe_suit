package it.pagopa.send.suite.contract.destinatario_pg;

import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.send.TestBootApp;
import it.pagopa.send.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.send.common.infrastructure.config.JunitContextConfig;
import it.pagopa.send.common.journey.application.SendJourney;
import it.pagopa.send.common.legal_notification.domain.LegalNotificationCreationRequest;
import it.pagopa.send.common.legal_notification.domain.LegalNotificationDomain;
import it.pagopa.send.common.legal_notification.domain.NotificationStatus;
import it.pagopa.send.common.legal_notification.domain.RecipientSpec;
import it.pagopa.send.common.legal_notification.infrastructure.factory.RecipientSpecFactory;
import it.pagopa.send.common.user.domain.User;
import it.pagopa.send.common.user.domain.UserAlias;
import it.pagopa.send.common.user.domain.UserProvider;
import it.pagopa.send.web.destinatario_pg.infrastructure.page.NotificationDetailPGPage;
import it.pagopa.send.web.destinatario_pg.infrastructure.page.NotificationPage;
import it.pagopa.send.web.destinatario_pg.infrastructure.page.NotificationTimelinePGPage;
import it.pagopa.send.web.infrastructure.config.WebJUnitSuitConfig;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.Map;
import java.util.stream.Stream;

/**
 * Contract test della pagina "Stato della notifica" della persona giuridica
 * ({@code {baseUrl}/notifiche/<IUN>/dettaglio/timeline}).
 * <p>
 * Prima degli scenari il mittente {@code COMUNE_1} invia via BFF una notifica a valore legale all'impresa
 * {@code IMPRESA_1}; gli scenari ne aprono la pagina "Stato della notifica" direttamente dall'URL, con il suo IUN, e dal
 * dettaglio della notifica con "Vai al dettaglio".
 * Si verifica quello che c'è in ogni notifica: breadcrumb e presenza degli eventi con il loro stato. Stati, descrizioni,
 * date e attestazioni dipendono dall'avanzamento della notifica: non vengono verificati.
 */
@SpringBootTest(classes = {
        TestBootApp.class,
        JunitContextConfig.class,
        WebJUnitSuitConfig.class
}, properties = "spring.profiles.include=junit")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class WebNotificationTimelinePGContractTest {

    // testi attesi

    private static final String SUBJECT = "Notifica di test per lo stato della notifica PG";

    private final WebBrowserContractValidator webContractValidator;
    private final SendJourney sendJourney;
    private final RecipientSpecFactory recipientSpecFactory;
    private final LegalNotificationCreationRequest creationRequest;
    private final UserProvider userProvider;

    /**
     * Testi della pagina "Stato della notifica" della notifica inviata all'impresa:
     * <ul>
     *     <li>breadcrumb: "In arrivo per &lt;impresa&gt;", l'oggetto della notifica e, come voce corrente,
     *     "Stato della notifica";</li>
     *     <li>almeno un evento, ognuno con il suo stato non vuoto;</li>
     *     <li>la stessa pagina si apre anche dal dettaglio della notifica con "Vai al dettaglio".</li>
     * </ul>
     */
    @TestFactory
    Stream<DynamicTest> shouldShowNotificationTimelineTexts() {
        // l'impresa deve avere un codice fiscale numerico: SEND non accetta altri codici per i destinatari PG
        User impresa = userProvider.resolve(UserAlias.IMPRESA_1);
        String notificationsBreadcrumb = NotificationPage.TITLE_PREFIX + impresa.getOrganization();
        LegalNotificationDomain createdNotification = sendLegalNotification(impresa);

        return Stream.concat(
                directAccessScenarios(impresa, createdNotification.getIun(), notificationsBreadcrumb),
                detailAccessScenarios(impresa, createdNotification.getIun()));
    }

    // pagina aperta direttamente dall'URL con lo IUN
    private Stream<DynamicTest> directAccessScenarios(User impresa, String iun, String notificationsBreadcrumb) {
        return webContractValidator.asRecipient(impresa)
                .on(NotificationTimelinePGPage.class, iun)
                .tests(Stream.of(
                        new WebScenario<>(
                                "breadcrumb con il nome dell'impresa, l'oggetto della notifica e stato della notifica",
                                timeline -> {},
                                timeline -> {
                                    timeline.notificationsBreadcrumb().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(notificationsBreadcrumb));
                                    timeline.notificationBreadcrumb().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(SUBJECT));
                                    timeline.currentBreadcrumb().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(NotificationTimelinePGPage.TITLE));
                                }
                        ),

                        new WebScenario<>(
                                "eventi della notifica, ognuno con il suo stato",
                                timeline -> {},
                                timeline -> timeline.eventStatuses().readAllAndAssert(h ->
                                        Assertions.assertThat(h).isNotEmpty().allSatisfy(v -> Assertions.assertThat(v).isNotBlank()))
                        )
                ));
    }

    // pagina aperta dal dettaglio della notifica con "Vai al dettaglio"
    private Stream<DynamicTest> detailAccessScenarios(User impresa, String iun) {
        return webContractValidator.asRecipient(impresa)
                .on(NotificationDetailPGPage.class, iun)
                .tests(Stream.of(
                        new WebScenario<>(
                                "stato della notifica aperto dal dettaglio con vai al dettaglio",
                                detail -> detail.timelineDetailsButton().click(),
                                detail -> {
                                    NotificationTimelinePGPage timeline = detail.notificationTimeline();
                                    timeline.assertLoaded();
                                    timeline.currentBreadcrumb().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(NotificationTimelinePGPage.TITLE));
                                }
                        )
                ));
    }

    private LegalNotificationDomain sendLegalNotification(User impresa) {
        User sender = userProvider.resolve(UserAlias.COMUNE_1);
        // destinatario: l'impresa, con la sua denominazione invece del nome della persona che accede
        RecipientSpec recipient = recipientSpecFactory.build(impresa, Map.of(
                "denomination", impresa.getOrganization(),
                "physicalAddress_address", "Via@ok_890",
                "physicalAddress_municipality", "COLLELUNGO",
                "physicalAddress_province", "TR",
                "physicalAddress_zip", "05010",
                "pagoPA_number", "0",
                "F24_number", "0"
        ));
        // la richiesta di creazione è condivisa (singleton) e non viene svuotata tra un invio e l'altro: senza
        // svuotarla, la notifica includerebbe anche i destinatari rimasti da invii precedenti
        creationRequest.getRecipients().clear();
        try {
            return sendJourney
                    .withSender(sender)
                    .prepareNotification(Map.of(
                            "subject", SUBJECT,
                            "physicalCommunication", "REGISTERED_LETTER_890",
                            "feePolicy", "FLAT_RATE"
                    ))
                    .withRecipient(recipient)
                    .sendNotification(sender, NotificationStatus.ACCEPTED)
                    .get(LegalNotificationDomain.class);
        }
        finally {
            // anche dopo un invio fallito: i destinatari non restano per gli invii successivi
            creationRequest.getRecipients().clear();
        }
    }
}
