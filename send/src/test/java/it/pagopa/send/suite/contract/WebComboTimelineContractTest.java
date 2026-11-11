package it.pagopa.send.suite.contract;

import it.frontend.e2e.framework.web.capability.core.Readable;
import it.pagopa.infrastructure.suit.component.Label;
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
import it.pagopa.send.web.mittente.infrastructure.page.MittenteComboTimelinePage;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Test di contratto per la pagina Web di Timeline Comunicazione Bonaria (Combo).
 * <p>
 * Verifica il caricamento atomico delle componenti della timeline, la corretta renderizzazione
 * degli eventi cronologici con timestamp, i log di dettaglio per ciascun canale di invio
 * e il box apicale con l'esito finale.
 *
 */
@SpringBootTest(classes = {
        TestBootApp.class,
        JunitContextConfig.class,
        WebJUnitSuitConfig.class
}, properties = "spring.profiles.include=junit")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class WebComboTimelineContractTest {

    private static final String TITLE = "Stato della comunicazione";
    private static final String ACCEPTED = "Pronta per l'invio";
    // titolo -> descrizione di ogni elemento della timeline ("Invio in corso" non ha descrizione)
    private static final Map<String, String> ITEM_DESCRIPTIONS = Map.of(
            ACCEPTED, "La comunicazione ha superato le verifiche di conformità. Inizierà a breve il processo d'invio.",
            "Invio in corso", "",
            "Invio terminato", "La comunicazione è stata inviata sui canali scelti.",
            "Invio fallito", "Non è stato possibile inviare la comunicazione sui canali scelti.",
            "Letta", "La persona destinataria ha letto la comunicazione.",
            "Consegnata", "La persona destinataria ha ricevuto la comunicazione.");
    private static final Set<String> CHANNELS = Set.of("IO", "PEC", "Email", "SMS", "Servizio postale", "SEND");
    // coppie "tag|testo" degli eventi per canale; il numero di raccomandata è normalizzato in {{code}}
    private static final Set<String> CHANNEL_EVENTS = Set.of("|Depositata sulla piattaforma SEND.",
            "|Invio su IO riuscito.", "|Invio via PEC riuscito.", "|Invio via email riuscito.", "|Invio via SMS riuscito.",
            "|Impossibile effettuare l'invio su IO.", "|Impossibile effettuare l'invio via PEC.",
            "|Impossibile effettuare l'invio via email.", "|Impossibile effettuare l'invio via SMS.",
            "|Invio via posta ordinaria riuscito.", "|Invio via raccomandata semplice n. {{code}} riuscito.",
            "|Impossibile effettuare l'invio via posta ordinaria.", "|Impossibile effettuare l'invio via raccomandata semplice.",
            "|Impossibile risalire all'account della persona destinataria.", "|Impossibile risalire all'indirizzo PEC della persona destinataria.",
            "|Impossibile risalire all'indirizzo email della persona destinataria.", "|Impossibile risalire al numero di telefono della persona destinataria.",
            "CONSEGNATA|Consegna su IO riuscita.", "CONSEGNATA|Consegna via PEC riuscita.", "CONSEGNATA|Consegna via email riuscita.",
            "CONSEGNATA|Consegna via SMS riuscita.", "CONSEGNATA|Consegna via posta ordinaria riuscita.",
            "CONSEGNATA|Consegna via raccomandata semplice n. {{code}} riuscita.",
            "LETTA|La persona destinataria ha letto la comunicazione su IO.",
            "LETTA|La persona destinataria ha letto la comunicazione sulla piattaforma SEND.");
    private static final String EVENT_DATE = "\\d{2} [A-Z]{3}, \\d{2}:\\d{2}";

    private final UserProvider userProvider;
    private final SendJourney sendJourney;
    private final InformalRecipientSpecFactory informalRecipientSpecFactory;
    private final WebBrowserContractValidator webContractValidator;
    private final it.pagopa.send.bff.delivery.infrastructure.BffComboRestClient bffComboRestClient;

    @TestFactory
    Stream<DynamicTest> shouldValidateComboTimelinePageLayout() {
        InformalRecipientSpec lucrezia = informalRecipientSpecFactory.build(userProvider.resolve(UserAlias.USER_1), Map.of());

        InformalNotificationDomain created = sendJourney
                .withInformalSender(userProvider.resolve(UserAlias.COMUNE_1))
                .prepareInformalNotification(Map.of(
                        "subject", "Test notifica bonaria per contratto Web Timeline",
                        "campaignId", "FattOrd"
                ))
                .withInformalRecipient(lucrezia)
                .sendInformalNotification(userProvider.resolve(UserAlias.COMUNE_1));

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

        return webContractValidator.as(userProvider.resolve(UserAlias.COMUNE_1))
                .on(MittenteComboTimelinePage.class, campaignId, iun)
                .tests(scenarios(campaignId, iun));
    }

    private Stream<WebScenario<MittenteComboTimelinePage>> scenarios(String campaignId, String iun) {
        return Stream.of(
                new WebScenario<>(
                        "Controllo caricamento atomico componenti pagina Timeline Combo",
                        page -> {},
                        page -> page.assertLoaded()
                ),
                new WebScenario<>(
                        "Controllo caricamento items timeline e titolo della pagina",
                        page -> {},
                        page -> {
                            page.timeline().assertLoaded();
                            Assertions.assertThat(page.title().read()).as("Titolo pagina").isEqualTo(TITLE);
                        }
                ),
                new WebScenario<>(
                        "Controllo card degli stati: titolo, descrizione e data",
                        page -> {},
                        page -> page.timeline().items().forEach(item -> {
                            String title = item.title().read();
                            Assertions.assertThat(ITEM_DESCRIPTIONS).as("Titolo '%s' tra quelli del frontend", title).containsKey(title);
                            String date = textOrEmpty(item.descriptionDate());
                            String description = textOrEmpty(item.description()).replace(date, "").trim();
                            Assertions.assertThat(description).as("Descrizione di '%s'", title).isEqualTo(ITEM_DESCRIPTIONS.get(title));
                            Assertions.assertThat(date.isEmpty()).as("Data presente solo su '%s' (card '%s')", ACCEPTED, title).isNotEqualTo(ACCEPTED.equals(title));
                        })
                ),
                new WebScenario<>(
                        "Controllo log ed eventi di dettaglio per canale nella timeline",
                        page -> {},
                        page -> page.timeline().items().forEach(item -> item.channelStepsOrEmpty().forEach(step -> {
                            String channel = step.name().read();
                            Assertions.assertThat(channel).as("Nome canale").isIn(CHANNELS);
                            Assertions.assertThat(step.events()).as("Eventi del canale %s", channel).isNotEmpty();
                            step.events().forEach(event -> {
                                String tagAndText = textOrEmpty(event.tag()) + "|"
                                        + event.text().read().replaceAll("n\\. \\S+ ", "n. {{code}} ");
                                Assertions.assertThat(tagAndText).as("Tag e testo evento del canale %s", channel).isIn(CHANNEL_EVENTS);
                                Assertions.assertThat(event.date().read()).as("Data evento del canale %s", channel).matches(EVENT_DATE);
                            });
                        }))
                ),
                new WebScenario<>(
                        "Controllo esito finale e box informativo apicale nella timeline",
                        page -> {},
                        page -> {
                            var items = page.timeline().items();
                            Assertions.assertThat(items.get(0).title().read()).as("Elemento in cima: esito o ultimo stato")
                                    .isIn(ITEM_DESCRIPTIONS.keySet());
                            Assertions.assertThat(items.get(items.size() - 1).title().read()).as("Primo stato in fondo").isEqualTo(ACCEPTED);
                        }
                ),
                new WebScenario<>(
                        "Breadcrumb: Campagne > campagna > IUN > Stato della comunicazione",
                        page -> {},
                        page -> Assertions.assertThat(page.breadcrumbItems().stream().map(Readable::read).toList())
                                .containsExactly("Campagne", campaignId, iun, TITLE)
                )
        );
    }

    // testo dell'elemento, vuoto se non c'è: exists() legge il DOM senza attendere il timeout
    private static String textOrEmpty(Label element) {
        return element.exists() ? element.read() : "";
    }
}
