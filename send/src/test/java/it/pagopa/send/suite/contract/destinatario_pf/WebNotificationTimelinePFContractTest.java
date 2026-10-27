package it.pagopa.send.suite.contract.destinatario_pf;

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
import it.pagopa.send.web.destinatario_pf.infrastructure.page.NotificationDetailsPFPage;
import it.pagopa.send.web.destinatario_pf.infrastructure.page.NotificationTimelinePFPage;
import it.pagopa.send.web.infrastructure.config.WebJUnitSuitConfig;
import it.pagopa.send.web.login.infrastructure.page.component.OneTrustBanner;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.Map;
import java.util.stream.Stream;

/**
 * Contract test della pagina "Stato della notifica" del cittadino ({@code {baseUrl}/notifiche/<IUN>/dettaglio/timeline}).
 * <p>
 * Prima degli scenari il mittente {@code COMUNE_1} invia via BFF una notifica a valore legale alla persona fisica
 * {@code USER_1}; gli scenari ne aprono la pagina "Stato della notifica" direttamente dall'URL, con il suo IUN, e dal
 * dettaglio della notifica con "Vai al dettaglio".
 * Si verifica quello che c'è in ogni notifica: titolo, breadcrumb, presenza degli eventi con il loro stato e
 * l'accettazione della notifica. Gli eventi successivi, le loro descrizioni e date dipendono dall'avanzamento della
 * notifica e dai recapiti del destinatario: non vengono verificati.
 */
@SpringBootTest(classes = {
        TestBootApp.class,
        JunitContextConfig.class,
        WebJUnitSuitConfig.class
}, properties = "spring.profiles.include=junit")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class WebNotificationTimelinePFContractTest {

    // testi attesi

    private static final String SUBJECT = "Notifica di test per lo stato della notifica PF";
    private static final String TITLE = "Stato della notifica";
    private static final String NOTIFICATIONS_BREADCRUMB = "In arrivo";
    // evento dell'accettazione della notifica: nel portale del cittadino ha testi diversi dal "Depositata" delle imprese
    private static final String ACCEPTED_STATUS = "Notifica pronta per l’invio";
    private static final String ACCEPTED_DESCRIPTION = "La notifica digitale ha superato le verifiche di conformità. Puoi effettuare il download dell’";
    // data dell'evento, es. "22 OTT, 14:16"
    private static final String EVENT_DATE_PATTERN = "(?i)\\d{1,2} [a-z]{3}, \\d{2}:\\d{2}";
    // link dell'attestazione, dentro la descrizione
    private static final String ACCEPTED_LEGAL_FACT = "attestazione";

    private final WebBrowserContractValidator webContractValidator;
    private final SendJourney sendJourney;
    private final RecipientSpecFactory recipientSpecFactory;
    private final LegalNotificationCreationRequest creationRequest;
    private final UserProvider userProvider;

    /**
     * Testi della pagina "Stato della notifica" della notifica inviata al cittadino:
     * <ul>
     *     <li>titolo "Stato della notifica"; breadcrumb: "In arrivo" e, come voce corrente, "Stato della notifica". La
     *     voce intermedia con l'oggetto non viene verificata: il portale lo legge dalla cronologia della notifica e, se
     *     non è ancora disponibile, mostra "Dettaglio notifica";</li>
     *     <li>almeno un evento, ognuno con il suo stato non vuoto;</li>
     *     <li>l'evento più vecchio, in fondo alla cronologia, è l'accettazione della notifica: "Notifica pronta per
     *     l’invio", "La notifica digitale ha superato le verifiche di conformità. Puoi effettuare il download
     *     dell’attestazione." con il link "attestazione" e la data nel formato "gg MMM, hh:mm";</li>
     *     <li>la stessa pagina si apre anche dal dettaglio della notifica con "Vai al dettaglio".</li>
     * </ul>
     */
    @TestFactory
    Stream<DynamicTest> shouldShowNotificationTimelineTexts() {
        User recipient = userProvider.resolve(UserAlias.USER_1);
        LegalNotificationDomain createdNotification = sendLegalNotification(recipient);

        return Stream.concat(
                directAccessScenarios(recipient, createdNotification.getIun()),
                detailAccessScenarios(recipient, createdNotification.getIun()));
    }

    // pagina aperta direttamente dall'URL con lo IUN
    private Stream<DynamicTest> directAccessScenarios(User recipient, String iun) {
        return webContractValidator.asRecipient(recipient)
                .on(NotificationTimelinePFPage.class, iun)
                .tests(Stream.of(
                        new WebScenario<>(
                                "titolo e breadcrumb con in arrivo e stato della notifica",
                                timeline -> {},
                                timeline -> {
                                    timeline.title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(TITLE));
                                    timeline.notificationsBreadcrumb().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(NOTIFICATIONS_BREADCRUMB));
                                    timeline.currentBreadcrumb().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(TITLE));
                                }
                        ),

                        new WebScenario<>(
                                "eventi della notifica, ognuno con il suo stato",
                                timeline -> {},
                                timeline -> timeline.eventTitles().readAllAndAssert(h ->
                                        Assertions.assertThat(h).isNotEmpty().allSatisfy(v -> Assertions.assertThat(v).isNotBlank()))
                        ),

                        new WebScenario<>(
                                "evento più vecchio: notifica pronta per l'invio, con descrizione, data e attestazione",
                                timeline -> {},
                                timeline -> {
                                    timeline.firstEventStatus().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(ACCEPTED_STATUS));
                                    timeline.firstEventDescription().readAndAssert(h -> Assertions.assertThat(h).startsWith(ACCEPTED_DESCRIPTION));
                                    timeline.firstEventDate().readAndAssert(h -> Assertions.assertThat(h).matches(EVENT_DATE_PATTERN));
                                    timeline.firstEventLegalFact().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(ACCEPTED_LEGAL_FACT));
                                }
                        )
                ));
    }

    // pagina aperta dal dettaglio della notifica con "Vai al dettaglio"
    private Stream<DynamicTest> detailAccessScenarios(User recipient, String iun) {
        return webContractValidator.asRecipient(recipient)
                .on(NotificationDetailsPFPage.class, iun)
                .tests(Stream.of(
                        new WebScenario<>(
                                "stato della notifica aperto dal dettaglio con vai al dettaglio",
                                detail -> {
                                    detail.oneTrustBanner().ifPresent(OneTrustBanner::acceptIfShown);
                                    detail.timelineDetailsButton().click();
                                },
                                detail -> {
                                    NotificationTimelinePFPage timeline = detail.notificationTimeline();
                                    timeline.assertLoaded();
                                    timeline.currentBreadcrumb().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(TITLE));
                                }
                        )
                ));
    }

    private LegalNotificationDomain sendLegalNotification(User recipient) {
        User sender = userProvider.resolve(UserAlias.COMUNE_1);
        RecipientSpec recipientSpec = recipientSpecFactory.build(recipient, Map.of(
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
                    .withRecipient(recipientSpec)
                    .sendNotification(sender, NotificationStatus.ACCEPTED)
                    .get(LegalNotificationDomain.class);
        }
        finally {
            // anche dopo un invio fallito: i destinatari non restano per gli invii successivi
            creationRequest.getRecipients().clear();
        }
    }
}
