package it.pagopa.send.suite.contract;

import it.frontend.e2e.framework.core.capability.core.Gettable.FindPolicy;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.infrastructure.suit.component.Checkbox;
import it.pagopa.send.TestBootApp;
import it.pagopa.send.bff.dashboard.infrastructure.BffDashboardRestClient;
import it.pagopa.send.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.send.common.infrastructure.config.JunitContextConfig;
import it.pagopa.send.common.user.domain.User;
import it.pagopa.send.common.user.domain.UserAlias;
import it.pagopa.send.common.user.domain.UserProvider;
import it.pagopa.send.web.infrastructure.config.WebJUnitSuitConfig;
import it.pagopa.send.web.mittente.infrastructure.page.StatisticsPage;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Test di contratto per la pagina Statistiche del mittente PA: componenti, testi e stato iniziale
 * del filtro. La data di ultimo aggiornamento attesa arriva dal BFF della dashboard.
 */
@SpringBootTest(classes = {
        TestBootApp.class,
        JunitContextConfig.class,
        WebJUnitSuitConfig.class
}, properties = "spring.profiles.include=junit")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class WebStatisticsPaContractTest {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final String FILED = "Notifiche dell'ente su SEND";
    private static final String DELIVERY_MODE = "Notifiche consegnate per modalità di invio";
    // titolo del grafico con selettore -> voci della sua legenda
    private static final Map<String, List<String>> GRAPH_LEGENDS = Map.of(
            FILED, List.of("Accettate", "Scartate"),
            DELIVERY_MODE, List.of("Invii digitali", "Invii analogici"));

    private final UserProvider userProvider;
    private final WebBrowserContractValidator webContractValidator;
    private final BffDashboardRestClient bffDashboardRestClient;

    @TestFactory
    Stream<DynamicTest> shouldValidateStatisticsPage() {
        // COMUNE_1 ha statistiche su dev, test e uat (hotfix escluso): il test presuppone che i dati ci siano
        User comune = userProvider.resolve(UserAlias.COMUNE_1);
        return webContractValidator.as(comune)
                .on(StatisticsPage.class)
                .tests(scenarios(comune.getOrganizationId()));
    }

    private Stream<WebScenario<StatisticsPage>> scenarios(String organizationId) {
        return Stream.of(
                new WebScenario<>(
                        "Controllo caricamento pagina Statistiche",
                        page -> {},
                        page -> page.assertLoaded()
                ),
                new WebScenario<>(
                        "Intestazione: titolo, sottotitolo e bottone Esporta JPEG abilitato",
                        page -> {},
                        page -> {
                            Assertions.assertThat(page.header().read()).as("Titolo").isEqualTo("Statistiche");
                            Assertions.assertThat(page.subtitle().read()).as("Sottotitolo").isEqualTo(
                                    "Esplora le statistiche relative alle notifiche del tuo ente su SEND - Servizio Notifiche Digitali");
                            Assertions.assertThat(page.exportJpgButton().read()).as("Testo bottone export").isEqualTo("Esporta JPEG");
                            Assertions.assertThat(!page.exportJpgButton().isDisabled()).as("Bottone export abilitato").isTrue();
                        }
                ),
                new WebScenario<>(
                        "Data di ultimo aggiornamento uguale a quella del BFF",
                        page -> {},
                        page -> Assertions.assertThat(page.lastUpdate().read()).as("Ultimo aggiornamento")
                                .isEqualTo("Ultimo aggiornamento " + lastDate(organizationId).format(DATE))
                ),
                new WebScenario<>(
                        "Titoli delle sezioni, titoli e descrizioni dei grafici",
                        page -> {},
                        page -> {
                            Assertions.assertThat(page.sectionTitles().stream().map(Readable::read).toList()).as("Sezioni")
                                    .containsExactly("Panoramica delle notifiche", "Dettaglio delle notifiche inviate digitalmente");
                            Assertions.assertThat(page.graphTitles().stream().map(Readable::read).toList()).as("Grafici")
                                    .containsExactly(FILED, "Notifiche inviate per stato", DELIVERY_MODE,
                                            "Invii digitali per esito", "Tempo medio degli invii digitali", "Errori tecnici per tipologia");
                            // "Errori tecnici per tipologia" mostra la descrizione solo se ha dati
                            Assertions.assertThat(page.graphDescriptions().stream().map(Readable::read).toList()).as("Descrizioni grafici")
                                    .startsWith("Numero di notifiche accettate dalla piattaforma SEND sul totale delle notifiche caricate dall'ente",
                                            "Numero di notifiche accettate per ultimo stato raggiunto durante il processo di invio tramite SEND",
                                            "Numero di notifiche consegnate per modalità di invio (digitale o analogica)",
                                            "Numero di notifiche inviate in digitale per esito",
                                            "Tempo medio (in giorni) di consegna, avvenuto accesso e perfezionamento delle notifiche inviate in digitale");
                        }
                ),
                new WebScenario<>(
                        "Grafici con selettore: tipo Aggregato e voci di legenda tutte selezionate",
                        page -> {},
                        page -> {
                            var cards = page.graphCards();
                            Assertions.assertThat(cards.stream().map(c -> c.title().read()).toList()).as("Grafici con selettore")
                                    .containsExactly(FILED, DELIVERY_MODE);
                            cards.forEach(card -> {
                                String title = card.title().read();
                                Assertions.assertThat(card.graphTypeLabel().read()).as("Etichetta selettore di '%s'", title).isEqualTo("Grafico");
                                Assertions.assertThat(card.graphType().read()).as("Tipo grafico di '%s'", title).isEqualTo("Aggregato");
                                Assertions.assertThat(card.legendItems().stream().map(i -> i.description().read()).toList())
                                        .as("Legenda di '%s'", title).isEqualTo(GRAPH_LEGENDS.get(title));
                                Assertions.assertThat(card.legendItems()).as("Voci di legenda selezionate in '%s'", title)
                                        .allMatch(Checkbox::isChecked);
                            });
                        }
                ),
                new WebScenario<>(
                        "Opzioni del selettore del tipo di grafico",
                        page -> page.graphCards().get(0).graphType().click(),
                        page -> Assertions.assertThat(page.graphTypeOptions().stream().map(Readable::read).toList())
                                .as("Opzioni tipo grafico").containsExactly("Aggregato", "Andamentale")
                ),
                new WebScenario<>(
                        "Filtro: testi e stato iniziale sugli ultimi 12 mesi dall'ultimo aggiornamento",
                        page -> {},
                        page -> {
                            LocalDate lastDate = lastDate(organizationId);
                            var filter = page.filter();
                            Assertions.assertThat(page.filterSubtitle().read()).as("Sottotitolo filtro")
                                    .isEqualTo("Scegli l'intervallo di rilevazione");
                            Assertions.assertThat(page.filterTooltip().get(FindPolicy.PRESENT).orElseThrow().getAttributes().get("aria-label"))
                                    .as("Tooltip filtro")
                                    .isEqualTo("Il giorno più recente di rilevazione corrisponde alla data di ultimo aggiornamento dei dati");
                            Assertions.assertThat(filter.periods().stream().map(Readable::read).toList()).as("Periodi")
                                    .containsExactly("Ultimi 12 mesi", "Ultimi 6 mesi", "Ultimi 3 mesi", "Ultimi 30 giorni");
                            Assertions.assertThat(filter.periods().stream().map(p -> p.isDisabled()).toList())
                                    .as("Solo il periodo selezionato (12 mesi) è disabilitato")
                                    .containsExactly(true, false, false, false);
                            Assertions.assertThat(filter.startDate().read()).as("Data inizio")
                                    .isEqualTo(lastDate.minusYears(1).plusDays(1).format(DATE));
                            Assertions.assertThat(filter.endDate().read()).as("Data fine").isEqualTo(lastDate.format(DATE));
                            Assertions.assertThat(filter.filterButton().isDisabled()).as("Filtra disabilitato").isTrue();
                            Assertions.assertThat(filter.cancelButton().isDisabled()).as("Annulla filtri disabilitato").isTrue();
                        }
                )
        );
    }

    // il BFF va chiamato nello scenario, dopo che il validator ha impostato l'utente in sessione
    private LocalDate lastDate(String organizationId) {
        var response = bffDashboardRestClient.getDashboardData(organizationId).withoutPolling().get();
        Assertions.assertThat(response).as("Precondizione: il BFF restituisce le statistiche dell'ente").isNotNull();
        LocalDate lastDate = response.getLastDate();
        Assertions.assertThat(lastDate).as("Precondizione: il BFF restituisce le statistiche dell'ente").isNotNull();
        return lastDate;
    }
}
