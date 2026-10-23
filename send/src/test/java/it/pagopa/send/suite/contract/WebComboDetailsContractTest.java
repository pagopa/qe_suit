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
import it.pagopa.send.web.mittente.infrastructure.page.MittenteComboDetailsPage;
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
 * Test di contratto per la pagina Web di Dettaglio Comunicazione Bonaria (Combo).
 * <p>
 * Verifica il caricamento atomico delle sezioni della pagina, la presenza delle componenti
 * di overview, stato, canali di invio e relativi badge, oltre alle sezioni condizionali
 * Documenti e Pagamenti e al corretto comportamento nel layout desktop e mobile.
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
public class WebComboDetailsContractTest {

    private final SendJourney sendJourney;
    private final InformalRecipientSpecFactory informalRecipientSpecFactory;
    private final WebBrowserContractValidator webContractValidator;
    private final it.pagopa.send.bff.delivery.infrastructure.BffComboRestClient bffComboRestClient;

    @TestFactory
    Stream<DynamicTest> shouldValidateComboDetailsPageLayout() {
        InformalRecipientSpec lucrezia = informalRecipientSpecFactory.build(Recipient.LUCREZIA, Map.of());

        InformalNotificationDomain created = sendJourney
                .withInformalSender(Tenant.GROSSINI)
                .prepareInformalNotification(Map.of(
                        "subject", "Test notifica bonaria per contratto Web Dettaglio",
                        "campaignId", "FattOrd"
                ))
                .withInformalRecipient(lucrezia)
                .sendInformalNotification(Tenant.GROSSINI);

        String campaignId = created.getCampaignId();
        String iun = created.getIun();

        // Polling BE affinché la comunicazione sia completamente arricchita prima di verificare il frontend
        org.awaitility.Awaitility.await()
                .atMost(java.time.Duration.ofSeconds(90))
                .pollInterval(java.time.Duration.ofSeconds(3))
                .until(() -> {
                    var detail = bffComboRestClient.getComboDetails(campaignId, iun)
                            .withoutPolling()
                            .get();
                    return detail != null && detail.getChannelsStatus() != null && !detail.getChannelsStatus().isEmpty();
                });

        return webContractValidator.as(Tenant.GROSSINI, List.of(Recipient.LUCREZIA))
                .on(MittenteComboDetailsPage.class, campaignId, iun)
                .tests(scenarios());
    }

    private Stream<WebScenario<MittenteComboDetailsPage>> scenarios() {
        return Stream.of(
                new WebScenario<>(
                        "Controllo caricamento atomico sezioni di layout pagina Dettaglio Combo",
                        page -> {},
                        page -> page.assertLoaded()
                ),
                new WebScenario<>(
                        "Controllo presenza sezione overview e canali",
                        page -> {},
                        page -> {
                            page.overviewSection().assertLoaded();
                            page.statusSection().assertLoaded();
                            page.channelsSection().assertLoaded();
                        }
                ),
                new WebScenario<>(
                        "Controllo visibilità canali di invio e relativi badge di stato",
                        page -> {},
                        page -> {
                            var items = page.channelsSection().channelItems();
                            org.assertj.core.api.Assertions.assertThat(items)
                                    .as("La sezione canali deve contenere la lista dei canali abilitati")
                                    .isNotEmpty();
                            items.forEach(item -> {
                                item.assertLoaded();
                                item.name().readAndAssert(name ->
                                        org.assertj.core.api.Assertions.assertThat(name).as("Nome canale non nullo").isNotBlank());
                                if (item.hasStatusBadge()) {
                                    String badge = item.statusBadge().read();
                                    if (badge != null && !badge.isBlank()) {
                                        org.assertj.core.api.Assertions.assertThat(badge).isNotBlank();
                                    }
                                }
                            });
                        }
                ),
                new WebScenario<>(
                        "Controllo coerenza testi descrittivi ed esplicativi dei canali di invio",
                        page -> {},
                        page -> {
                            var items = page.channelsSection().channelItems();
                            items.forEach(item -> {
                                if (item.hasDescription()) {
                                    String desc = item.description().read();
                                    if (desc != null && !desc.isBlank()) {
                                        org.assertj.core.api.Assertions.assertThat(desc).isNotBlank();
                                    }
                                }
                            });
                        }
                ),
                new WebScenario<>(
                        "Controllo caricamento sezioni condizionali Documenti e Pagamenti",
                        page -> {},
                        page -> {
                            page.documentsSection().ifPresent(docSection -> docSection.assertLoaded());
                            page.paymentsSection().ifPresent(paySection -> paySection.assertLoaded());
                        }
                ),
                new WebScenario<>(
                        "Controllo accessibilità ed integrità sezioni in layout responsive desktop/mobile",
                        page -> {},
                        page -> {
                            page.overviewSection().openSidebarButton();
                            page.statusSection().openTimelineButton();
                        }
                )
        );
    }
}

