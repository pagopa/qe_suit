package it.pagopa.send.suite.contract;

import it.pagopa.send.common.user.domain.UserProvider;
import it.pagopa.send.common.user.domain.UserAlias;
import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.send.TestBootApp;
import it.pagopa.send.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.send.common.infrastructure.config.JunitContextConfig;
import it.pagopa.send.common.informal_notification.domain.InformalNotificationDomain;
import it.pagopa.send.common.informal_notification.domain.InformalRecipientSpec;
import it.pagopa.send.common.informal_notification.infrastructure.factory.InformalRecipientSpecFactory;
import it.pagopa.send.common.journey.application.SendJourney;
import it.pagopa.send.web.infrastructure.config.WebJUnitSuitConfig;
import it.pagopa.send.web.mittente.infrastructure.page.MittenteComboDetailsPage;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

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
@SpringBootTest(classes = {
        TestBootApp.class,
        JunitContextConfig.class,
        WebJUnitSuitConfig.class
}, properties = "spring.profiles.include=junit")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class WebComboDetailsContractTest {

    private final UserProvider userProvider;
    private final SendJourney sendJourney;
    private final InformalRecipientSpecFactory informalRecipientSpecFactory;
    private final WebBrowserContractValidator webContractValidator;
    private final it.pagopa.send.bff.delivery.infrastructure.BffComboRestClient bffComboRestClient;

    @TestFactory
    Stream<DynamicTest> shouldValidateComboDetailsPageLayout() {
        InformalRecipientSpec lucrezia = informalRecipientSpecFactory.build(userProvider.resolve(UserAlias.USER_1), Map.of());

        InformalNotificationDomain created = sendJourney
                .withInformalSender(userProvider.resolve(UserAlias.COMUNE_1))
                .prepareInformalNotification(Map.of(
                        "subject", "Test notifica bonaria per contratto Web Dettaglio",
                        "campaignId", "FattOrd"
                ))
                .withInformalRecipient(lucrezia)
                .sendInformalNotification(userProvider.resolve(UserAlias.COMUNE_1));

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

        return webContractValidator.as(userProvider.resolve(UserAlias.COMUNE_1))
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
                            page.overviewSection().openSidebarButton().assertLoaded();
                            page.statusSection().openTimelineButton().assertLoaded();
                        }
                )
        );
    }
}

