package it.pagopa.send.suite.contract;

import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.send.TestBootApp;
import it.pagopa.send.common.user.domain.Recipient;
import it.pagopa.send.common.user.domain.Tenant;
import it.pagopa.send.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.send.common.infrastructure.config.JunitContextConfig;
import it.pagopa.send.common.legal_notification.domain.LegalNotificationDomain;
import it.pagopa.send.common.legal_notification.domain.NotificationStatus;
import it.pagopa.send.web.mittente.infrastructure.page.DashboardPage;
import it.pagopa.send.web.notification_details.infrastructure.page.MittenteNotificationDetailsPage;
import it.pagopa.send.web.notification_details.infrastructure.page.timeline.TimelineDetailsPage;
import it.pagopa.send.web.notification_details.infrastructure.page.timeline.TimelineItemComponent;
import it.pagopa.send.web.infrastructure.config.WebJUnitSuitConfig;
import it.pagopa.send.common.journey.application.SendJourney;
import it.pagopa.send.common.legal_notification.domain.RecipientSpec;
import it.pagopa.send.common.legal_notification.infrastructure.factory.RecipientSpecFactory;
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
public class WebNotificationTimelineContractTest {

    private final SendJourney sendJourney;
    private final WebBrowserContractValidator webContractValidator;
    private final RecipientSpecFactory recipientSpecFactory;

    @TestFactory
    Stream<DynamicTest> shouldValidateNotificationTimelineWithTwoRecipients() {
        RecipientSpec lucrezia = recipientSpecFactory.build(Recipient.LUCREZIA, Map.of(
                "physicalAddress_address", "Via@FAIL-Irreperibile_890",
                "physicalAddress_municipality", "COLLELUNGO",
                "physicalAddress_province", "TR",
                "physicalAddress_zip", "05010",
                "pagoPA_number", "0",
                "F24_number", "0"
        ));
        RecipientSpec petrarca = recipientSpecFactory.build(Recipient.PETRARCA, Map.of(
                "physicalAddress_address", "Via@FAIL-Irreperibile_890",
                "physicalAddress_municipality", "COLLELUNGO",
                "physicalAddress_province", "TR",
                "physicalAddress_zip", "05010",
                "pagoPA_number", "0",
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
                .withRecipient(petrarca)
                .sendNotification(Tenant.GROSSINI, NotificationStatus.ACCEPTED)
                .waitForNotificationStatus(NotificationStatus.EFFECTIVE_DATE)
                .get(LegalNotificationDomain.class);

        return webContractValidator.as(Tenant.GROSSINI, List.of(lucrezia.recipient(), petrarca.recipient()))
                .on(TimelineDetailsPage.class, createdNotification.getIun())
                .tests(scenarios());

    }

    private Stream<WebScenario<TimelineDetailsPage>> scenarios() {
        return Stream.of(
                new WebScenario<>(
                        "presenza della frase attesa nella timeline per invio in corso",
                        page -> {
                        },
                        page -> {
//                            page.assertLoaded();

                            TimelineItemComponent invioInCorso = page.timeline()
                                    .itemTitled("Invio in corso")
                                    .orElseThrow(() -> new AssertionError("Componente 'Invio in corso' non trovato in timeline"));

                            Assertions.assertTrue(invioInCorso.containsPhrase("Lucrezia - BRGLRZ80D58H501Q"),
                                    "Il componente 'Invio in corso' non contiene il destinatario atteso");
                            Assertions.assertTrue(invioInCorso.containsPhrase("Francesco Petrarca - 12666810299"),
                                    "Il componente 'Invio in corso' non contiene il destinatario atteso");
                        }
                ),
                new WebScenario<>(
                        "presenza della frase attesa nella timeline per decorrenza termini",
                        page -> {},
                        page -> {
                            TimelineItemComponent invioInCorso = page.timeline()
                                    .itemTitled("Perfezionata per decorrenza termini")
                                    .orElseThrow(() -> new AssertionError("Componente 'Perfezionata per decorrenza termini' non trovato in timeline"));

                            Assertions.assertTrue(invioInCorso.containsPhrase("La notifica non è stata letta entro il termine stabilito"),
                                    "Il componente 'Perfezionata per decorrenza termini' non contiene la frase attesa");
                        }
                ),
                new WebScenario<>(
                        "presenza della frase attesa nella timeline per depositata",
                        page -> {},
                        page -> {
                            TimelineItemComponent invioInCorso = page.timeline()
                                    .itemTitled("Depositata")
                                    .orElseThrow(() -> new AssertionError("Componente 'Depositata' non trovato in timeline"));

                            Assertions.assertTrue(invioInCorso.containsPhrase("L'ente ha depositato la notifica in piattaforma"),
                                    "Il componente 'Depositata' non contiene la frase attesa");
                        }
                )
        );
    }

}
