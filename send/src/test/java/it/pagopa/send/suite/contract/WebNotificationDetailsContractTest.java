package it.pagopa.send.suite.contract;

import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.send.TestBootApp;
import it.pagopa.send.common.user.domain.Recipient;
import it.pagopa.send.common.user.domain.Tenant;
import it.pagopa.send.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.send.common.infrastructure.config.JunitContextConfig;
import it.pagopa.send.common.journey.application.SendJourney;
import it.pagopa.send.common.legal_notification.domain.LegalNotificationDomain;
import it.pagopa.send.common.legal_notification.domain.NotificationStatus;
import it.pagopa.send.web.notification_details.infrastructure.page.MittenteNotificationDetailsPage;
import it.pagopa.send.web.notification_details.infrastructure.page.timeline.TimelineDetailsPage;
import it.pagopa.send.common.legal_notification.domain.RecipientSpec;
import it.pagopa.send.common.legal_notification.infrastructure.factory.RecipientSpecFactory;
import it.pagopa.send.web.infrastructure.config.WebJUnitSuitConfig;
import it.pagopa.send.web.notification_details.infrastructure.NotificationDetailsProxy;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@ActiveProfiles({"test", "junit"})
@Execution(ExecutionMode.CONCURRENT)
@SpringBootTest(classes = {
        TestBootApp.class,
        JunitContextConfig.class,
        WebJUnitSuitConfig.class
})
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class WebNotificationDetailsContractTest {

    private final SendJourney sendJourney;
    private final WebBrowserContractValidator webContractValidator;
    private final RecipientSpecFactory recipientSpecFactory;

    @TestFactory
    Stream<DynamicTest> shouldValidateNotificationTimeline() {
        RecipientSpec lucrezia = recipientSpecFactory.build(Recipient.LUCREZIA, Map.of(
                "physicalAddress_address", "Via@ok_890",
                "physicalAddress_municipality", "COLLELUNGO",
                "physicalAddress_province", "TR",
                "physicalAddress_zip", "05010",
                "pagoPA_number", "2",
                "F24_number", "0"
        ));

        LegalNotificationDomain createdNotification = sendJourney
                .withSender(Tenant.GROSSINI)
                .prepareNotification(Map.of(
                        "subject", "Comunicazione di test multi-destinatario",
                        "physicalCommunication", "REGISTERED_LETTER_890",
                        "feePolicy", "FLAT_RATE"
                ))
                .withRecipient(lucrezia)
                .sendNotification(Tenant.GROSSINI, NotificationStatus.ACCEPTED)
                .waitForNotificationStatus(NotificationStatus.EFFECTIVE_DATE)
                .get(LegalNotificationDomain.class);

        return webContractValidator.as(Tenant.GROSSINI, List.of(lucrezia.recipient()))
                .on(MittenteNotificationDetailsPage.class, createdNotification.getIun())
                .tests(scenarios());

    }

    private Stream<WebScenario<MittenteNotificationDetailsPage>> scenarios() {
        return Stream.of(
                new WebScenario<>(
                        "controllo caricamento sezioni pagina dettaglio notifica",
                        page -> {},
                        page -> {
                            page.notificationSummarySection().assertLoaded();
                            page.paymentSection().assertLoaded();
                            page.attachmentSection().assertLoaded();
                            page.notificationStatusSection().assertLoaded();
                        }
                ),
                new WebScenario<>(
                        "controllo numero pagamenti pagoPA",
                        page -> {},
                        page -> {
                            Assertions.assertEquals(2,
                                    page.paymentSection().paymentListButtons().size());
                        }
                )
        );
    }

}
