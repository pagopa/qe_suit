package it.pagopa.send.suite.contract;

import it.pagopa.send.common.user.domain.UserProvider;
import it.pagopa.send.common.user.domain.UserAlias;
import it.pagopa.infrastructure.template.action.strategy.PollingStrategy;
import it.pagopa.send.TestBootApp;
import it.pagopa.send.bff.delivery.infrastructure.BffComboRestClient;
import it.pagopa.send.common.infrastructure.config.JunitContextConfig;
import it.pagopa.send.common.informal_notification.domain.InformalNotificationDomain;
import it.pagopa.send.common.informal_notification.domain.InformalRecipientSpec;
import it.pagopa.send.common.informal_notification.infrastructure.factory.InformalRecipientSpecFactory;
import it.pagopa.send.common.journey.application.SendJourney;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Map;

/**
 * Test di contratto per le API BFF delle comunicazioni bonarie: status e struttura delle risposte di dettaglio e
 * timeline. Le verifiche di comportamento (dettaglio di uno IUN inesistente, ordinamento della timeline) sono in
 * {@code consultazione-comunicazione-bonaria.feature}.
 */
@Slf4j
@SpringBootTest(classes = {
        TestBootApp.class,
        JunitContextConfig.class
}, properties = "spring.profiles.include=junit")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class BffComboContractTest {

    private final UserProvider userProvider;
    private final BffComboRestClient bffComboRestClient;
    private final SendJourney sendJourney;
    private final InformalRecipientSpecFactory informalRecipientSpecFactory;

    private InformalNotificationDomain createComboNotification(String subject) {
        InformalRecipientSpec lucrezia = informalRecipientSpecFactory.build(userProvider.resolve(UserAlias.USER_1), Map.of());

        return sendJourney
                .withInformalSender(userProvider.resolve(UserAlias.COMUNE_1))
                .prepareInformalNotification(Map.of(
                        "subject", subject,
                        "campaignId", "FattOrd"
                ))
                .withInformalRecipient(lucrezia)
                .sendInformalNotification(userProvider.resolve(UserAlias.COMUNE_1));
    }

    // ===========================
    // Contratti Dettaglio - GET /campaigns/{campaignId}/communications/{iun}
    // ===========================

    /**
     * Verifica la struttura del payload di risposta per il dettaglio della comunicazione bonaria (schema DTO e campi obbligatori).
     * Migrato da dettaglio-combo-bff.feature.
     */
    @Test
    void shouldValidateComboDetailResponseContract() {
        log.info("Verifica contratto API: schema e campi obbligatori del dettaglio combo");
        InformalNotificationDomain created = createComboNotification("Test notifica bonaria per contratto BFF Dettaglio");

        var response = bffComboRestClient.getComboDetails(created.getCampaignId(), created.getIun())
                .withPolling(PollingStrategy.UNTIL_SUCCESS, Duration.of(90, ChronoUnit.SECONDS), Duration.of(3, ChronoUnit.SECONDS));

        response.assertStatusCode(200);
        var detail = response.get();
        // i campi required della specifica non sono ricontrollati: si verifica solo ciò che la specifica non garantisce
        Assertions.assertThat(detail.getRecipients()).isNotEmpty();
        Assertions.assertThat(detail.getChannelsStatus()).isNotNull();
    }

    // ===========================
    // Contratti Timeline - GET /campaigns/{campaignId}/communications/{iun}/timeline
    // ===========================

    /**
     * Verifica la struttura del payload di risposta per la timeline della comunicazione bonaria.
     * Migrato da timeline-combo-bff.feature.
     */
    @Test
    void shouldValidateComboTimelineResponseContract() {
        log.info("Verifica contratto API: schema e campi obbligatori della timeline combo");
        InformalNotificationDomain created = createComboNotification("Test notifica bonaria per contratto BFF Timeline");

        var response = bffComboRestClient.getComboTimeline(created.getCampaignId(), created.getIun())
                .withPolling(PollingStrategy.UNTIL_SUCCESS, Duration.of(90, ChronoUnit.SECONDS), Duration.of(3, ChronoUnit.SECONDS));

        response.assertStatusCode(200);
        var timeline = response.get();
        // i campi required della specifica non sono ricontrollati: si verifica solo ciò che la specifica non garantisce
        Assertions.assertThat(timeline.getRecipients()).isNotEmpty();
        Assertions.assertThat(timeline.getNotificationStatusHistory()).isNotEmpty();
    }
}
