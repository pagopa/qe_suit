package it.pagopa.send.suite.contract.destinatario_pg;

import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.send.TestBootApp;
import it.pagopa.send.common.delegation.domain.Delegation;
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
import it.pagopa.send.web.destinatario_pg.infrastructure.page.DelegatedNotificationDetailPGPage;
import it.pagopa.send.web.destinatario_pg.infrastructure.page.DelegatedNotificationPage;
import it.pagopa.send.web.destinatario_pg.infrastructure.page.DelegatedNotificationTimelinePGPage;
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
 * Contract test della pagina "Stato della notifica" di una notifica a valore legale ricevuta tramite delega dalla
 * persona giuridica ({@code {baseUrl}/notifiche-delegato/<id delega>/<IUN>/dettaglio/timeline}).
 * <p>
 * Prima degli scenari l'impresa {@code IMPRESA_1} ha una delega attiva della persona fisica {@code USER_1}: si riusa
 * quella già presente, altrimenti {@code USER_1} la crea via BFF e l'impresa la accetta. Poi il mittente
 * {@code COMUNE_1} invia via BFF una notifica a valore legale a {@code USER_1}; gli scenari ne aprono la pagina
 * "Stato della notifica" come impresa delegata, direttamente dall'URL con l'id della delega e lo IUN, e dal dettaglio
 * della notifica delegata con "Vai al dettaglio".
 * Si verifica quello che c'è in ogni notifica: titolo, breadcrumb e presenza degli eventi con il loro stato. Stati,
 * descrizioni, date e attestazioni dipendono dall'avanzamento della notifica: non vengono verificati.
 */
@SpringBootTest(classes = {
        TestBootApp.class,
        JunitContextConfig.class,
        WebJUnitSuitConfig.class
}, properties = "spring.profiles.include=junit")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class WebNotificationTimelineDelegatoContractTest {

    // testi attesi

    private static final String SUBJECT = "Notifica di test per lo stato della notifica PG da delega";
    private static final String DEPOSITED_STATUS = "Depositata";
    private static final String DEPOSITED_DESCRIPTION = "L'ente ha depositato la notifica in piattaforma.";
    // data dell'evento, es. "22 OTT, 14:16"
    private static final String EVENT_DATE_PATTERN = "(?i)\\d{1,2} [a-z]{3}, \\d{2}:\\d{2}";
    private static final String DEPOSITED_LEGAL_FACT = "Attestazione opponibile a terzi: notifica presa in carico";

    private final WebBrowserContractValidator webContractValidator;
    private final SendJourney sendJourney;
    private final RecipientSpecFactory recipientSpecFactory;
    private final LegalNotificationCreationRequest creationRequest;
    private final UserProvider userProvider;

    /**
     * Testi della pagina "Stato della notifica" della notifica inviata al delegante e aperta dall'impresa delegata:
     * <ul>
     *     <li>titolo "Stato della notifica"; breadcrumb: "In arrivo dalle deleghe", l'oggetto della notifica e, come
     *     voce corrente, "Stato della notifica";</li>
     *     <li>almeno un evento, ognuno con il suo stato non vuoto;</li>
     *     <li>l'evento più vecchio, in fondo alla cronologia, è il deposito: "Depositata", "L'ente ha depositato la
     *     notifica in piattaforma." con la data nel formato "gg MMM, hh:mm" e il link "Attestazione opponibile a terzi:
     *     notifica presa in carico";</li>
     *     <li>la stessa pagina si apre anche dal dettaglio della notifica delegata con "Vai al dettaglio".</li>
     * </ul>
     * Gli eventi successivi al deposito (es. "Avvenuto accesso", con messaggi di cortesia e PEC) dipendono
     * dall'avanzamento della notifica e dai recapiti del delegante: non vengono verificati.
     */
    @TestFactory
    Stream<DynamicTest> shouldShowDelegatedNotificationTimelineTexts() {
        User delegator = userProvider.resolve(UserAlias.USER_1);
        User impresa = userProvider.resolve(UserAlias.IMPRESA_1);
        // la delega non riguarda una notifica: vale per tutte quelle del delegante, quindi si riusa tra un giro e l'altro
        Delegation delegation = sendJourney.withActiveDelegation(delegator, impresa).get(Delegation.class);
        // la notifica va al delegante: l'impresa la vede tra quelle "In arrivo dalle deleghe"
        LegalNotificationDomain createdNotification = sendLegalNotification(delegator);

        return Stream.concat(
                directAccessScenarios(impresa, delegation.getMandateId(), createdNotification.getIun()),
                detailAccessScenarios(impresa, delegation.getMandateId(), createdNotification.getIun()));
    }

    // pagina aperta direttamente dall'URL con l'id della delega e lo IUN
    private Stream<DynamicTest> directAccessScenarios(User impresa, String mandateId, String iun) {
        return webContractValidator.asRecipient(impresa)
                .on(DelegatedNotificationTimelinePGPage.class, mandateId, iun)
                .tests(Stream.of(
                        new WebScenario<>(
                                "titolo e breadcrumb con in arrivo dalle deleghe, l'oggetto della notifica e stato della notifica",
                                timeline -> {},
                                timeline -> {
                                    timeline.title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(NotificationTimelinePGPage.TITLE));
                                    timeline.notificationsBreadcrumb().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(DelegatedNotificationPage.TITLE));
                                    timeline.notificationBreadcrumb().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(SUBJECT));
                                    timeline.currentBreadcrumb().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(NotificationTimelinePGPage.TITLE));
                                }
                        ),

                        new WebScenario<>(
                                "eventi della notifica, ognuno con il suo stato",
                                timeline -> {},
                                timeline -> timeline.eventStatuses().readAllAndAssert(h ->
                                        Assertions.assertThat(h).isNotEmpty().allSatisfy(v -> Assertions.assertThat(v).isNotBlank()))
                        ),

                        new WebScenario<>(
                                "evento più vecchio: depositata, con descrizione, data e attestazione di presa in carico",
                                timeline -> {},
                                timeline -> {
                                    timeline.firstEventStatus().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(DEPOSITED_STATUS));
                                    timeline.firstEventDescription().readAndAssert(h -> Assertions.assertThat(h).startsWith(DEPOSITED_DESCRIPTION));
                                    timeline.firstEventDate().readAndAssert(h -> Assertions.assertThat(h).matches(EVENT_DATE_PATTERN));
                                    timeline.firstEventLegalFact().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(DEPOSITED_LEGAL_FACT));
                                }
                        )
                ));
    }

    // pagina aperta dal dettaglio della notifica delegata con "Vai al dettaglio"
    private Stream<DynamicTest> detailAccessScenarios(User impresa, String mandateId, String iun) {
        return webContractValidator.asRecipient(impresa)
                .on(DelegatedNotificationDetailPGPage.class, mandateId, iun)
                .tests(Stream.of(
                        new WebScenario<>(
                                "stato della notifica aperto dal dettaglio della notifica delegata con vai al dettaglio",
                                detail -> detail.timelineDetailsButton().click(),
                                detail -> {
                                    NotificationTimelinePGPage timeline = detail.notificationTimeline();
                                    timeline.assertLoaded();
                                    timeline.currentBreadcrumb().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(NotificationTimelinePGPage.TITLE));
                                }
                        )
                ));
    }

    private LegalNotificationDomain sendLegalNotification(User delegator) {
        User sender = userProvider.resolve(UserAlias.COMUNE_1);
        RecipientSpec recipient = recipientSpecFactory.build(delegator, Map.of(
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
