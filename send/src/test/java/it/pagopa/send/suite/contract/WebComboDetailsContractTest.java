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
import it.frontend.e2e.framework.core.capability.core.Readable;
import it.pagopa.send.bff.delivery.infrastructure.BffComboRestClient;
import it.pagopa.send.generated.openapi.clients.sender.informal.bff.model.BffFullSentInformalNotificationV1;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Test di contratto per la pagina Web di Dettaglio Comunicazione Bonaria (Combo).
 * <p>
 * Verifica il caricamento atomico delle sezioni della pagina, la presenza delle componenti
 * di overview, stato, canali di invio e relativi badge, oltre alle sezioni condizionali
 * Documenti e Pagamenti e al corretto comportamento nel layout desktop e mobile.
 */
@SpringBootTest(classes = {
        TestBootApp.class,
        JunitContextConfig.class,
        WebJUnitSuitConfig.class
}, properties = "spring.profiles.include=junit")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class WebComboDetailsContractTest {

    private static final Map<String, String> STATUS_DESCRIPTIONS = Map.of(
            "Pronta per l'invio", "La comunicazione ha superato le verifiche di conformità. Inizierà a breve il processo d'invio.",
            "Invio in corso", "La comunicazione proverà a raggiungere la persona destinataria sui canali scelti.",
            "Invio terminato", "La comunicazione è stata inviata sui canali scelti.",
            "Invio fallito", "Non è stato possibile inviare la comunicazione sui canali scelti.",
            "Rifiutata", "La comunicazione non ha superato le verifiche di conformità.");
    private static final Set<String> CHANNELS = Set.of("IO", "PEC", "Email", "SMS", "Servizio postale", "SEND");
    // coppie "badge|descrizione" del frontend: WORKFLOW TERMINATO non mostra il badge, solo la descrizione
    private static final Set<String> CHANNEL_BADGE_DESCRIPTIONS = Set.of("CONSEGNATA|", "DEPOSITATA|", "NON CONSEGNATA|", "INVIATA|", "LETTA|",
            "IN ATTESA|Invio previsto dopo la validazione della comunicazione.",
            "IN ATTESA|In attesa del completamento dell'invio sul canale precedente.");
    private static final Map<String, String> UNAVAILABLE_DESCRIPTIONS = Map.of(
            "IO", "Impossibile risalire all'account della persona destinataria.",
            "PEC", "Impossibile risalire all'indirizzo PEC della persona destinataria.",
            "Email", "Impossibile risalire all'indirizzo email della persona destinataria.",
            "SMS", "Impossibile risalire al numero di telefono della persona destinataria.");
    private static final Map<String, String> WORKFLOW_ENDED_DESCRIPTIONS = Map.of(
            "Servizio postale", "Non sarà utilizzato, perché la comunicazione ha già raggiunto l'esito richiesto.",
            "SMS", "Non sarà utilizzato, perché la comunicazione ha già raggiunto l'esito richiesto.",
            "Email", "Non sarà utilizzata, perché la comunicazione ha già raggiunto l'esito richiesto.",
            "PEC", "Non sarà utilizzata, perché la comunicazione ha già raggiunto l'esito richiesto.");

    private final UserProvider userProvider;
    private final SendJourney sendJourney;
    private final InformalRecipientSpecFactory informalRecipientSpecFactory;
    private final WebBrowserContractValidator webContractValidator;
    private final BffComboRestClient bffComboRestClient;

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
        BffFullSentInformalNotificationV1 detail = Awaitility.await()
                .atMost(Duration.ofSeconds(90))
                .pollInterval(Duration.ofSeconds(3))
                .until(() -> bffComboRestClient.getComboDetails(campaignId, iun).withoutPolling().get(),
                        d -> d != null && d.getChannelsStatus() != null && !d.getChannelsStatus().isEmpty());

        return webContractValidator.as(userProvider.resolve(UserAlias.COMUNE_1))
                .on(MittenteComboDetailsPage.class, campaignId, iun)
                .tests(scenarios(campaignId, iun, detail));
    }

    private Stream<WebScenario<MittenteComboDetailsPage>> scenarios(String campaignId, String iun, BffFullSentInformalNotificationV1 detail) {
        return Stream.of(
                new WebScenario<>(
                        "Controllo caricamento atomico sezioni di layout pagina Dettaglio Combo",
                        page -> {},
                        page -> page.assertLoaded()
                ),
                new WebScenario<>(
                        "Controllo sezione overview e stato: IUN, label, valori, stato e descrizione",
                        page -> {},
                        page -> {
                            Assertions.assertThat(page.overviewSection().title().read()).as("Titolo").isEqualTo(iun);
                            Assertions.assertThat(page.summaryLabels().stream().map(Readable::read).toList()).as("Label")
                                    .containsExactly("Mittente", "Persona destinataria", "Oggetto della comunicazione");
                            var recipient = detail.getRecipients().get(0);
                            Assertions.assertThat(page.summaryValues().stream().map(Readable::read).toList()).as("Valori")
                                    .containsExactly(detail.getSenderDenomination(), recipient.getDenomination() + " (" + recipient.getTaxId() + ")",
                                            recipient.getMessage().getPrimaryMessage().getSubject());
                            Assertions.assertThat(page.statusSection().header().read()).isEqualTo("Stato della comunicazione");
                            String status = page.statusSection().statusChip().text().read();
                            Assertions.assertThat(STATUS_DESCRIPTIONS).as("Stato '%s' tra quelli del frontend", status).containsKey(status);
                            Assertions.assertThat(page.statusSection().description().read()).isEqualTo(STATUS_DESCRIPTIONS.get(status));
                        }
                ),
                new WebScenario<>(
                        "Controllo visibilità canali di invio: titolo e nomi tra i valori del frontend",
                        page -> {},
                        page -> {
                            Assertions.assertThat(page.channelsSection().header().read()).isEqualTo("Dettaglio invio per canale");
                            var items = page.channelsSection().channelItems();
                            Assertions.assertThat(items)
                                    .as("La sezione canali deve contenere la lista dei canali abilitati")
                                    .isNotEmpty();
                            items.forEach(item -> Assertions.assertThat(item.name().read()).as("Nome canale").isIn(CHANNELS));
                        }
                ),
                new WebScenario<>(
                        "Controllo coerenza badge di stato e testi descrittivi dei canali di invio",
                        page -> {},
                        page -> page.channelsSection().channelItems().forEach(item -> {
                            String channel = item.name().read();
                            String badgeAndDescription = Objects.toString(item.statusBadge().read(), "") + "|" + Objects.toString(item.description().read(), "");
                            Assertions.assertThat(badgeAndDescription).as("Badge e descrizione del canale %s", channel)
                                    .isIn(allowedBadgeAndDescription(channel));
                        })
                ),
                new WebScenario<>(
                        "Controllo caricamento sezioni Documenti e Pagamenti",
                        page -> {},
                        page -> {
                            Assertions.assertThat(page.documentsSection().orElseThrow().header().read()).isEqualTo("Documenti allegati");
                            page.paymentsSection().filter(section -> section.get().isPresent()).ifPresent(section -> section.assertLoaded());
                        }
                ),
                new WebScenario<>(
                        "Controllo accessibilità ed integrità sezioni in layout responsive desktop/mobile",
                        page -> {},
                        page -> {
                            Assertions.assertThat(page.overviewSection().openSidebarButton().read()).as("Pulsante overview").isEqualTo("Vai al dettaglio");
                            Assertions.assertThat(page.statusSection().openTimelineButton().read()).as("Pulsante stato").isEqualTo("Vai al dettaglio");
                        }
                ),
                new WebScenario<>("Breadcrumb: Campagne > campagna > IUN", page -> {},
                        page -> Assertions.assertThat(page.breadcrumbItems().stream().map(Readable::read).toList())
                                .containsExactly("Campagne", campaignId, iun))
        );
    }

    private static Set<String> allowedBadgeAndDescription(String channel) {
        return Stream.concat(CHANNEL_BADGE_DESCRIPTIONS.stream(), Stream.of(
                "NON DISPONIBILE|" + UNAVAILABLE_DESCRIPTIONS.get(channel), "|" + WORKFLOW_ENDED_DESCRIPTIONS.get(channel))).collect(Collectors.toSet());
    }
}
