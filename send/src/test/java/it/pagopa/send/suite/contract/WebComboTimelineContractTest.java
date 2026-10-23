package it.pagopa.send.suite.contract;

import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.send.TestBootApp;
import it.pagopa.send.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.send.common.infrastructure.config.JunitContextConfig;
import it.pagopa.send.common.user.domain.Recipient;
import it.pagopa.send.common.user.domain.Tenant;
import it.pagopa.send.common.informal_notification.domain.InformalNotificationDomain;
import it.pagopa.send.common.informal_notification.domain.InformalRecipientSpec;
import it.pagopa.send.common.informal_notification.infrastructure.factory.InformalRecipientSpecFactory;
import it.pagopa.send.common.journey.application.SendJourney;
import it.pagopa.send.web.infrastructure.config.WebJUnitSuitConfig;
import it.pagopa.send.web.mittente.infrastructure.page.MittenteComboTimelinePage;
import it.pagopa.send.JunitActiveProfilesResolver;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Test di contratto per la pagina Web di Timeline Comunicazione Bonaria (Combo).
 * <p>
 * Verifica il caricamento atomico delle componenti della timeline, la corretta renderizzazione
 * degli eventi cronologici con timestamp, i log di dettaglio per ciascun canale di invio
 * e il box apicale con l'esito finale.
 *
 */
@ActiveProfiles(resolver = JunitActiveProfilesResolver.class)
@SpringBootTest(classes = {
        TestBootApp.class,
        JunitContextConfig.class,
        WebJUnitSuitConfig.class
})
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class WebComboTimelineContractTest {

    private final SendJourney sendJourney;
    private final InformalRecipientSpecFactory informalRecipientSpecFactory;
    private final WebBrowserContractValidator webContractValidator;
    private final it.pagopa.send.bff.delivery.infrastructure.BffComboRestClient bffComboRestClient;

    @TestFactory
    Stream<DynamicTest> shouldValidateComboTimelinePageLayout() {
        InformalRecipientSpec lucrezia = informalRecipientSpecFactory.build(Recipient.LUCREZIA, Map.of());

        InformalNotificationDomain created = sendJourney
                .withInformalSender(Tenant.GROSSINI)
                .prepareInformalNotification(Map.of(
                        "subject", "Test notifica bonaria per contratto Web Timeline",
                        "campaignId", "FattOrd"
                ))
                .withInformalRecipient(lucrezia)
                .sendInformalNotification(Tenant.GROSSINI);

        String campaignId = created.getCampaignId();
        String iun = created.getIun();

        // Polling BE affinché la timeline sia popolata di eventi prima di verificare il frontend
        org.awaitility.Awaitility.await()
                .atMost(java.time.Duration.ofSeconds(90))
                .pollInterval(java.time.Duration.ofSeconds(3))
                .until(() -> {
                    var timeline = bffComboRestClient.getComboTimeline(campaignId, iun)
                            .withoutPolling()
                            .get();
                    return timeline != null
                            && timeline.getNotificationStatusHistory() != null
                            && timeline.getNotificationStatusHistory().stream()
                                    .anyMatch(h -> h.getSteps() != null && !h.getSteps().isEmpty());
                });

        return webContractValidator.as(Tenant.GROSSINI, List.of(Recipient.LUCREZIA))
                .on(MittenteComboTimelinePage.class, campaignId, iun)
                .tests(scenarios());
    }

    private Stream<WebScenario<MittenteComboTimelinePage>> scenarios() {
        return Stream.of(
                new WebScenario<>(
                        "Controllo caricamento atomico componenti pagina Timeline Combo",
                        page -> {},
                        page -> page.assertLoaded()
                ),
                new WebScenario<>(
                        "Controllo caricamento items timeline",
                        page -> {},
                        page -> page.timeline().assertLoaded()
                ),
                new WebScenario<>(
                        "Controllo visualizzazione responsive card eventi e gestione wrapping testi",
                        page -> {},
                        page -> {
                            page.timeline().items().forEach(item ->
                                    item.title().readAndAssert(org.junit.jupiter.api.Assertions::assertNotNull));

                            var eventsWithTimestamp = page.timeline().itemsWithTimestamp();
                            org.assertj.core.api.Assertions.assertThat(eventsWithTimestamp)
                                    .as("La timeline deve contenere eventi tracciati con data/ora")
                                    .isNotEmpty();
                            eventsWithTimestamp.forEach(item ->
                                    item.timestamp().readAndAssert(ts ->
                                            org.assertj.core.api.Assertions.assertThat(ts)
                                                    .as("Il timestamp dell'evento non deve essere vuoto")
                                                    .isNotBlank()));
                        }
                ),
                new WebScenario<>(
                        "Controllo log ed eventi di dettaglio per canale nella timeline",
                        page -> {},
                        page -> {
                            var items = page.timeline().items();
                            org.assertj.core.api.Assertions.assertThat(items)
                                    .as("La timeline deve contenere gli eventi di tracciamento dei canali")
                                    .isNotEmpty();
                            items.forEach(item -> {
                                item.assertLoaded();
                                item.content().readAndAssert(c ->
                                        org.assertj.core.api.Assertions.assertThat(c).as("Contenuto log canale non nullo").isNotBlank());
                            });
                        }
                ),
                new WebScenario<>(
                        "Controllo esito finale e box informativo apicale nella timeline",
                        page -> {},
                        page -> {
                            var items = page.timeline().items();
                            org.assertj.core.api.Assertions.assertThat(items)
                                    .as("La timeline deve contenere almeno un evento per l'esito finale")
                                    .isNotEmpty();
                            var topItem = items.get(0);
                            topItem.title().readAndAssert(title ->
                                    org.assertj.core.api.Assertions.assertThat(title).as("Titolo evento apicale non nullo").isNotBlank());
                            String fullText = topItem.read();
                            org.assertj.core.api.Assertions.assertThat(fullText)
                                    .as("Testo esito finale nel box timeline non nullo")
                                    .isNotBlank();
                        }
                )
        );
    }
}

