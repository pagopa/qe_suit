package it.pagopa.send.suite.contract;

import it.pagopa.infrastructure.contract.browser.WebContractValidator;
import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.send.TestBootApp;
import it.pagopa.send.common.domain.Recipient;
import it.pagopa.send.common.domain.Tenant;
import it.pagopa.send.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.send.common.infrastructure.config.JunitContextConfig;
import it.pagopa.send.web.infrastructure.config.WebJUnitSuitConfig;
import it.pagopa.send.common.journey.application.SendJourney;
import it.pagopa.send.common.notification.domain.LegalNotificationDomain;
import it.pagopa.send.common.notification.domain.NotificationStatus;
import it.pagopa.send.domain.web.pages.mittente.MittenteNotificationDetailsPage;
import it.pagopa.send.generated.openapi.clients.bff.model.BffNotificationStatus;
import it.pagopa.send.model.RecipientSpec;
import it.pagopa.send.utils.factory.RecipientSpecFactory;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
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
public class WebNotificationTimelineContractTest {

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
                "pagoPA_number", "0",
                "F24_number", "0"
        ));
        RecipientSpec petrarca = recipientSpecFactory.build(Recipient.PETRARCA, Map.of(
                "digitalDomicile", "test@pec.it",
                "pagoPA_number", "1",
                "F24_number", "0"
        ));

        // LegalNotificationJourneyImpl è un singleton semplice (non più @ScenarioScope): la
        // stessa istanza funziona sia dentro uno scenario Cucumber sia in un test JUnit puro.
        sendJourney
                .withSender(Tenant.GROSSINI)
                .prepareNotification(Map.of(
                        "subject", "Comunicazione di test multi-destinatario",
                        "physicalCommunication", "REGISTERED_LETTER_890",
                        "feePolicy", "FLAT_RATE"
                ))
                .withRecipient(lucrezia)
                .withRecipient(petrarca)
                .sendNotification(Tenant.GROSSINI, NotificationStatus.ACCEPTED);

//        LegalNotificationDomain legalNotificationDomain = sendJourney
//                .withSender(sender)
//                .withType(type)
//                .withRecipient(RecipientSpec.of(Recipient.fromUsername(recipient)))
//                .sendNotification(BffNotificationStatus.fromValue("ACCEPTED"))
//                .get(LegalNotificationDomain.class);
//



        webContractValidator.as(Tenant.GROSSINI, List.of(lucrezia.recipient(), petrarca.recipient()))
                .on(MittenteNotificationDetailsPage.class)
                .tests(Stream.of());

        return Stream.of();
    }

//    private Stream<WebScenario<MittenteNotificationDetailsPage>> scenarios() {
//        return Stream.of(
//                new WebScenario<>(
//                        "client assertion vuota",
//                        page -> {
//                            page.clientAssertionInput().fill(" ");
//                            page.submitButton().click();
//                        },
//                        page -> Assertions.assertThat(
//                                page.getClientAssertionErrorMessage()
//                        ).isEqualTo("Inserisci un JWT valido.")
//                ),
//
//                new WebScenario<>(
//                        "client assertion non valida",
//                        page -> {
//                            page.clientAssertionInput()
//                                    .fill("invalid client assertion");
//                            page.submitButton().click();
//                        },
//                        page -> Assertions.assertThat(
//                                page.getClientAssertionErrorMessage()
//                        ).isEqualTo("Inserisci un JWT valido.")
//                ),
//
//                new WebScenario<>(
//                        "client id vuoto",
//                        page -> {
//                            page.clientIdInput().fill(" ");
//                            page.submitButton().click();
//                        },
//                        page -> Assertions.assertThat(
//                                page.getClientIdErrorMessage()
//                        ).isEqualTo("Inserisci un UUID valido.")
//                )
//        );
//    }

}
