package it.pagopa.send.suite.contract;

import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.send.TestBootApp;
import it.pagopa.send.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.send.common.infrastructure.config.JunitContextConfig;
import it.pagopa.send.common.journey.application.SendJourney;
import it.pagopa.send.common.legal_notification.domain.LegalNotificationDomain;
import it.pagopa.send.common.legal_notification.domain.NotificationStatus;
import it.pagopa.send.common.legal_notification.domain.RecipientSpec;
import it.pagopa.send.common.legal_notification.infrastructure.factory.RecipientSpecFactory;
import it.pagopa.send.common.user.domain.User;
import it.pagopa.send.common.user.domain.UserAlias;
import it.pagopa.send.common.user.domain.UserProvider;
import it.pagopa.send.web.infrastructure.config.WebJUnitSuitConfig;
import it.pagopa.send.web.notification_details.infrastructure.page.MittenteNotificationDetailsPage;
import it.pagopa.send.web.notification_details.infrastructure.page.timeline.TimelineDetailsPage;
import it.pagopa.send.web.notification_details.infrastructure.page.timeline.TimelineItemComponent;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.Map;
import java.util.stream.Stream;

/**
 * Contract test della pagina "Stato della notifica" di una notifica inviata dalla PA
 * ({@code {baseUrl}/dashboard/:iun/dettaglio/timeline}), con una notifica senza pagamenti inviata dal test e appena
 * accettata. La pagina è aperta sia direttamente dalla rotta sia dal dettaglio con "Vai al dettaglio".
 * <p>
 * Gli eventi successivi al deposito non sono verificati, perché cambiano nel tempo. "Indietro" non è verificato: compare
 * solo su mobile, al posto del breadcrumb.
 */
@SpringBootTest(classes = {
        TestBootApp.class,
        JunitContextConfig.class,
        WebJUnitSuitConfig.class
}, properties = "spring.profiles.include=junit")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class WebNotificationTimelinePaContractTest {

    // testi attesi

    private static final String NOTIFICATIONS = "Notifiche";
    private static final String DEPOSITED = "Depositata";
    private static final String DEPOSITED_DESCRIPTION = "L'ente ha depositato la notifica in piattaforma";

    private static final String NOTIFICATION_SUBJECT = "SUIT contract test stato della notifica";

    private final SendJourney sendJourney;
    private final WebBrowserContractValidator webContractValidator;
    private final RecipientSpecFactory recipientSpecFactory;
    private final UserProvider userProvider;

    /**
     * Testi della pagina "Stato della notifica":
     * <ul>
     *     <li>breadcrumb: "Notifiche", lo IUN e, come voce corrente, "Stato della notifica";</li>
     *     <li>titolo "Stato della notifica";</li>
     *     <li>almeno un evento, ognuno con il suo titolo, tra cui "Depositata" con la sua descrizione;</li>
     *     <li>la stessa pagina si apre anche dal dettaglio della notifica con "Vai al dettaglio".</li>
     * </ul>
     */
    @TestFactory
    Stream<DynamicTest> shouldShowNotificationTimeline() {
        User sender = userProvider.resolve(UserAlias.COMUNE_1);
        String iun = sendLegalNotification(sender).getIun();

        return Stream.concat(directAccessScenarios(sender, iun), detailAccessScenarios(sender, iun));
    }

    // pagina aperta direttamente dall'URL con lo IUN
    private Stream<DynamicTest> directAccessScenarios(User sender, String iun) {
        return webContractValidator.as(sender)
                .on(TimelineDetailsPage.class, iun)
                .tests(Stream.of(
                        new WebScenario<>(
                                "breadcrumb notifiche, iun e stato della notifica",
                                page -> {},
                                page -> {
                                    page.notificationsBreadcrumb().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(NOTIFICATIONS));
                                    page.iunBreadcrumb().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(iun));
                                    page.currentBreadcrumb().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(TimelineDetailsPage.TITLE));
                                }
                        ),

                        new WebScenario<>(
                                "titolo stato della notifica",
                                page -> {},
                                page -> page.title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(TimelineDetailsPage.TITLE))
                        ),

                        new WebScenario<>(
                                "eventi della notifica, ognuno con il suo titolo",
                                page -> {},
                                page -> Assertions.assertThat(page.timeline().items())
                                        .isNotEmpty()
                                        .allSatisfy(item -> Assertions.assertThat(item.title().read()).isNotBlank())
                        ),

                        new WebScenario<>(
                                "evento depositata con la sua descrizione",
                                page -> {},
                                page -> {
                                    TimelineItemComponent deposited = page.timeline().itemTitled(DEPOSITED)
                                            .orElseThrow(() -> new AssertionError("Evento '" + DEPOSITED + "' non trovato in timeline"));
                                    Assertions.assertThat(deposited.containsPhrase(DEPOSITED_DESCRIPTION))
                                            .as("l'evento '%s' contiene '%s'", DEPOSITED, DEPOSITED_DESCRIPTION)
                                            .isTrue();
                                }
                        )
                ));
    }

    // pagina aperta dal dettaglio della notifica con "Vai al dettaglio"
    private Stream<DynamicTest> detailAccessScenarios(User sender, String iun) {
        return webContractValidator.as(sender)
                .on(MittenteNotificationDetailsPage.class, iun)
                .tests(Stream.of(
                        new WebScenario<>(
                                "stato della notifica aperto dal dettaglio con vai al dettaglio",
                                detail -> detail.notificationStatusSection().detailsButton().click(),
                                detail -> {
                                    TimelineDetailsPage timeline = detail.notificationTimeline();
                                    timeline.assertLoaded();
                                    timeline.currentBreadcrumb().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(TimelineDetailsPage.TITLE));
                                }
                        )
                ));
    }

    private LegalNotificationDomain sendLegalNotification(User sender) {
        RecipientSpec recipient = recipientSpecFactory.build(userProvider.resolve(UserAlias.USER_1), Map.of(
                "physicalAddress_address", "Via@ok_890",
                "physicalAddress_municipality", "COLLELUNGO",
                "physicalAddress_province", "TR",
                "physicalAddress_zip", "05010",
                "pagoPA_number", "0",
                "F24_number", "0"
        ));
        return sendJourney
                .withSender(sender)
                .prepareNotification(Map.of(
                        "subject", NOTIFICATION_SUBJECT,
                        "physicalCommunication", "REGISTERED_LETTER_890",
                        "feePolicy", "FLAT_RATE"
                ))
                .withRecipient(recipient)
                .sendNotification(sender, NotificationStatus.ACCEPTED)
                .get(LegalNotificationDomain.class);
    }
}
