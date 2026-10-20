package it.pagopa.interop.suite.contract;

import it.frontend.e2e.framework.web.adapter.model.FindPolicy;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.DomNode;
import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.web.eservice.infrastructure.page.EServiceDetailPage;
import it.pagopa.interop.web.infrastructure.config.WebJUnitSuitConfig;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Contract of the catalog e-service detail page (see matrix.md).
 * Context [PUB]: Pozzallo/Admin session, e-service of Comune di Milano with a PUBLISHED descriptor.
 * Scenarios are read-only: initial state is verified before any interaction.
 */
@SpringBootTest(
        classes = {TestBootApp.class, JunitContextConfig.class, WebJUnitSuitConfig.class},
        properties = {"spring.profiles.include=junit", "channel.web.browser=chrome"}//, "channel.web.headless=false"}
)
@Execution(ExecutionMode.SAME_THREAD)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class WebEServiceCatalogDetailsContractTest {

    private static final String PURPOSE_TEMPLATES_DESCRIPTION = "Questa funzionalità ti guida durante l'analisi del rischio con annotazioni, documenti di supporto e alcune risposte precompilate. Il modello agevolato è reso disponibile per il riuso da altri enti e può essere usato senza vincoli.";
    private static final String SIGNAL_HUB_DESCRIPTION = "Signal Hub è una soluzione integrata che informa i fruitori quando i dati dell'e-service vengono modificati.";
    private static final String ATTRIBUTES_DESCRIPTION = "Qui trovi le soglie e gli attributi che sono richiesti per la fruizione di questo e-service.";

    private final WebBrowserContractValidator webContractValidator;
    private final InteropJourney interopJourney;

    @TestFactory
    Stream<DynamicTest> catalogDetailsPageMustRespectContract() {
        EService eService = interopJourney
                .withProducer(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN)
                .createEService(EServiceDescriptorState.PUBLISHED)
                .get(EService.class);
        EServiceDescriptor descriptor = eService.getActiveDescriptor();

        return webContractValidator
                .as(User.getTenantAdmin(Tenant.COMUNE_DI_POZZALLO), Tenant.COMUNE_DI_POZZALLO)
                .on(EServiceDetailPage.class, eService.getId().toString(), descriptor.getId().toString())
                .tests(scenarios(eService, descriptor));
    }

    private Stream<WebScenario<EServiceDetailPage>> scenarios(EService eService, EServiceDescriptor descriptor) {
        return Stream.of(
                // ---- Intestazione
                present("01", "h1 titolo pagina", page-> page.header().pageTitle()),
                visible("02", "h1 titolo pagina", page-> page.header().pageTitle()),
                text("03", "h1 titolo pagina", page-> page.header().pageTitle(), eService.getName()),
                present("04", "Torna al catalogo", page-> page.header().backToCatalogAction()),
                text("05", "Torna al catalogo", page-> page.header().backToCatalogAction(), "Torna al catalogo"),
                // h2 rendered in uppercase by the frontend (text-transform)
                text("06", "Label versione", page-> page.header().versionHeaderLabel(), "VERSIONE"),
                text("07", "Pulsante versione", page-> page.header().versionShortcutButton(), descriptor.getVersion()),
                enabledButton("08", "Pulsante versione", page-> page.header().versionShortcutButton()),
                text("09", "Chip di stato descrittore", page-> page.header().descriptorStatusChip(), "attiva"),


                // ---- Azioni primarie
                present("11", "Richiedi fruizione", page->page.primaryActions().agreementButton()),
                visible("12", "Richiedi fruizione", page->page.primaryActions().agreementButton()),
                text("13", "Richiedi fruizione", page->page.primaryActions().agreementButton(), "Richiedi fruizione"),
                enabledButton("14", "Richiedi fruizione", page->page.primaryActions().agreementButton()),
                absentAll("15", "Visualizza richiesta, Completa richiesta, Aggiorna a nuova versione",
                        page->page.primaryActions().inspectAgreementButton(),
                        page->page.primaryActions().completeAgreementButton(),
                        page->page.primaryActions().upgradeToNewVersionButton()),

                // ---- Alert
                scenario("16", "Alert di stato descrittore", "Presenza nel DOM",
                        page -> Assertions.assertThat(page.alerts())
                                .as(label("16", "Alert di stato descrittore"))
                                .isEmpty()),
                scenario("17", "Alert portachiavi mancante / chiavi mancanti", "Presenza nel DOM",
                        page -> Assertions.assertThat(page.alerts().stream().map(a -> a.message().read()).toList())
                                .as(label("17", "Alert portachiavi mancante / chiavi mancanti"))
                                .noneMatch(message -> message.contains("portachiavi"))),

                // ---- Tab
                text("18", "Tab Dettaglio e-service", page->page.tabs().eserviceDetailTab(), "Dettaglio e-service"),
                attribute("19", "Tab Dettaglio e-service", page->page.tabs().eserviceDetailTab(), "aria-selected", "true"),
                text("20", "Tab Template finalità collegati", page->page.tabs().purposeTemplatesTab(), "Template finalità collegati"),
                attribute("21", "Tab Template finalità collegati", page->page.tabs().purposeTemplatesTab(), "aria-selected", "false"),
                notVisible("22", "Pannello tab Template finalità collegati", page->page.tabs().purposeTemplatesTabPanel()),

                // ---- Informazioni generali
                text("23", "Titolo sezione Informazioni generali", page->page.generalInfo().generalInfoTitle(), "Informazioni generali"),
                text("24", "Label produttore", page->page.generalInfo().producerLabel(), "Erogatore"),
                text("25", "Valore produttore", page->page.generalInfo().producerValue(), Tenant.COMUNE_DI_MILANO.getName()),
                absent("26", "Riga Template in uso", page->page.generalInfo().templateInUseLabel()),
                text("27", "Label versione", page->page.generalInfo().descriptorVersionLabel(), "Stai vedendo la versione"),
                text("28", "Valore versione", page->page.generalInfo().descriptorVersionValue(), descriptor.getVersion()),
                text("29", "Label dati personali", page->page.generalInfo().personalDataLabel(), "Eroga dati personali"),
                text("30", "Valore dati personali", page->page.generalInfo().personalDataValue(), "No"),
                text("31", "Label scambio dati", page->page.generalInfo().exchangeTypeLabel(), "Scambio dati"),
                text("32", "Valore scambio dati", page->page.generalInfo().exchangeTypeValue(), "Sincrono"),
                text("33", "Label descrizione e-service", page->page.generalInfo().eserviceDescriptionLabel(), "Descrizione dell'e-service"),
                text("34", "Valore descrizione e-service", page->page.generalInfo().eserviceDescriptionValue(), eService.getDescription()),
                text("35", "Label descrizione versione", page->page.generalInfo().descriptorDescriptionLabel(), "Descrizione della versione"),
                text("36", "Valore descrizione versione", page->page.generalInfo().descriptorDescriptionValue(), descriptor.getDescription()),
                text("37", "Titolo Fruizione tramite delega", page->page.generalInfo().delegationSectionTitle(), "Fruizione tramite delega"),
                text("38", "Label autorizzazione", page->page.generalInfo().consumerDelegableLabel(), "Autorizzazione"),
                text("39", "Valore autorizzazione", page->page.generalInfo().consumerDelegableValue(), "Sì"),
                text("40", "Label autorizzazione client", page->page.generalInfo().clientAccessDelegableLabel(), "Autorizzazione all’associazione dei client"),
                text("41", "Valore autorizzazione client", page->page.generalInfo().clientAccessDelegableValue(), "Sì"),
                present("42", "Azione Vedi i dettagli tecnici dell’e-service", page->page.generalInfo().showTechnicalDetailsAction()),
                text("43", "Azione Vedi i dettagli tecnici dell’e-service", page->page.generalInfo().showTechnicalDetailsAction(), "Vedi i dettagli tecnici dell’e-service"),
                enabledElement("44", "Azione Vedi i dettagli tecnici dell’e-service", page->page.generalInfo().showTechnicalDetailsAction()),
                absent("45", "Azione Vedi i dettagli sullo scambio asincrono", page->page.generalInfo().showAsyncExchangeDetailsAction()),
                present("46", "Azione Visualizza i contatti dell’erogatore", page->page.generalInfo().showProducerContactsAction()),
                notVisible("47", "Drawer specifiche tecniche / scambio asincrono / contatti", page->page.generalInfo().openDrawer()),

                // ---- Compilazione agevolata
                text("48", "Titolo sezione Compilazione agevolata", page->page.purposeTemplateSection().purposeTemplatesTitle(), "Compilazione agevolata della finalità"),
                text("49", "Descrizione sezione Compilazione agevolata", page->page.purposeTemplateSection().purposeTemplatesDescription(), PURPOSE_TEMPLATES_DESCRIPTION),
                present("50", "Pulsante Visualizza i template collegati", page->page.purposeTemplateSection().viewLinkedPurposeTemplatesButton()),
                text("51", "Pulsante Visualizza i template collegati", page->page.purposeTemplateSection().viewLinkedPurposeTemplatesButton(), "Visualizza i template collegati"),
                enabledButton("52", "Pulsante Visualizza i template collegati", page->page.purposeTemplateSection().viewLinkedPurposeTemplatesButton()),

                // ---- Signal Hub
                text("53", "Titolo sezione Signal Hub", page->page.signalHub().signalHubTitle(), "Signal Hub"),
                text("54", "Descrizione sezione Signal Hub", page->page.signalHub().signalHubDescription(), SIGNAL_HUB_DESCRIPTION),
                present("55", "Link soluzione integrata", page->page.signalHub().signalHubLink()),
                text("56", "Link soluzione integrata", page->page.signalHub().signalHubLink(), "soluzione integrata"),
                text("57", "Label disponibilità", page->page.signalHub().signalHubAvailabilityLabel(), "Disponibilità del servizio"),
                text("58", "Valore disponibilità", page->page.signalHub().signalHubAvailabilityValue(), "No, non è disponibile"),

                // ---- Soglie e attributi
                text("59", "Titolo sezione Soglie e attributi", page->page.thresholdsAndAttributes().attributesSectionTitle(), "Soglie e attributi"),
                text("60", "Descrizione sezione Soglie e attributi", page->page.thresholdsAndAttributes().attributesSectionDescription(), ATTRIBUTES_DESCRIPTION),
                text("61", "Titolo Soglie di chiamate API", page->page.thresholdsAndAttributes().thresholdsTitle(), "Soglie di chiamate API"),
                text("62", "Label soglia per fruitore", page->page.thresholdsAndAttributes().dailyCallsPerConsumerLabel(), "Soglia giornaliera per fruitore"),
                text("63", "Valore soglia per fruitore", page->page.thresholdsAndAttributes().dailyCallsPerConsumerValue(), "1"),
                text("64", "Label soglia totale", page->page.thresholdsAndAttributes().dailyCallsTotalLabel(), "Soglia giornaliera totale"),
                text("65", "Valore soglia totale", page->page.thresholdsAndAttributes().dailyCallsTotalValue(), "10"),
                absent("66", "Sezione Soglie di chiamate API personalizzate", page->page.thresholdsAndAttributes().customizedThresholdsTitle()),
                absentAll("67", "Gruppi attributi Certificati / Verificati / Dichiarati",
                        page->page.thresholdsAndAttributes().certifiedAttributesTitle(),
                        page->page.thresholdsAndAttributes().verifiedAttributesTitle(),
                        page->page.thresholdsAndAttributes().declaredAttributesTitle()),
                text("68", "Titolo sezione interna attributi", page->page.thresholdsAndAttributes().attributesTitle(), "Attributi"),
                text("68b", "Messaggio nessun attributo richiesto", page->page.thresholdsAndAttributes().noAttributesRequiredMessage(), "Questo e-service non richiede attributi"),

                // ---- Elementi non previsti (lista catalogo)
                absent("69", "h1 Catalogo degli e-service", EServiceDetailPage::catalogListTitle),
                absentAll("70", "Campi Cerca per nome e Cerca per erogatore",
                        EServiceDetailPage::searchByNameField,
                        EServiceDetailPage::searchByProducerField)
        );
    }

    // ------------------------------------------------------------------ helpers

    private static String label(String id, String element) {
        return "CATALOG-DETAILS-" + id + " " + element;
    }

    private static String normalize(String value) {
        return value == null ? null : value.replaceAll("\\s+", " ").trim();
    }

    private static WebScenario<EServiceDetailPage> scenario(
            String id,
            String element,
            String property,
            Consumer<EServiceDetailPage> assertion
    ) {
        return new WebScenario<>(
                "CATALOG-DETAILS-" + id + " – " + element + " – " + property,
                page -> {
                },
                assertion
        );
    }

    private static <T extends DomNode & Readable<String>> WebScenario<EServiceDetailPage> present(
            String id, String element, Function<EServiceDetailPage, T> locator
    ) {
        return scenario(id, element, "Presenza nel DOM",
                page -> Assertions.assertThat(locator.apply(page).get(FindPolicy.PRESENT))
                        .as(label(id, element) + " (atteso: presente)")
                        .isPresent());
    }

    private static <T extends DomNode & Readable<String>> WebScenario<EServiceDetailPage> visible(
            String id, String element, Function<EServiceDetailPage, T> locator
    ) {
        return scenario(id, element, "Visibilità",
                page -> Assertions.assertThat(locator.apply(page).get(FindPolicy.VISIBLE))
                        .as(label(id, element) + " (atteso: visibile)")
                        .isPresent());
    }

    private static <T extends DomNode & Readable<String>> WebScenario<EServiceDetailPage> notVisible(
            String id, String element, Function<EServiceDetailPage, T> locator
    ) {
        return scenario(id, element, "Visibilità",
                page -> Assertions.assertThat(locator.apply(page).get(FindPolicy.VISIBLE))
                        .as(label(id, element) + " (atteso: non visibile)")
                        .isEmpty());
    }

    private static <T extends DomNode & Readable<String>> WebScenario<EServiceDetailPage> absent(
            String id, String element, Function<EServiceDetailPage, T> locator
    ) {
        return scenario(id, element, "Presenza nel DOM",
                page -> Assertions.assertThat(locator.apply(page).get(FindPolicy.PRESENT))
                        .as(label(id, element) + " (atteso: assente)")
                        .isEmpty());
    }

    @SafeVarargs
    private static <T extends DomNode & Readable<String>> WebScenario<EServiceDetailPage> absentAll(
            String id, String element, Function<EServiceDetailPage, T>... locators
    ) {
        return scenario(id, element, "Presenza nel DOM", page -> {
            for (Function<EServiceDetailPage, T> locator : locators) {
                Assertions.assertThat(locator.apply(page).get(FindPolicy.PRESENT))
                        .as(label(id, element) + " (atteso: assenti)")
                        .isEmpty();
            }
        });
    }

    private static <T extends DomNode & Readable<String>> WebScenario<EServiceDetailPage> text(
            String id, String element, Function<EServiceDetailPage, T> locator, String expected
    ) {
        return scenario(id, element, "Copy",
                page -> Assertions.assertThat(normalize(locator.apply(page).read()))
                        .as(label(id, element) + " (copy)")
                        .isEqualTo(normalize(expected)));
    }

    private static <T extends DomNode & Readable<String>> WebScenario<EServiceDetailPage> attribute(
            String id, String element, Function<EServiceDetailPage, T> locator, String attribute, String expected
    ) {
        return scenario(id, element, "Stato iniziale",
                page -> Assertions.assertThat(
                                locator.apply(page).get(FindPolicy.PRESENT)
                                        .map(found -> found.getAttributes().get(attribute))
                                        .orElse(null))
                        .as(label(id, element) + " (attributo " + attribute + ")")
                        .isEqualTo(expected));
    }

    private static WebScenario<EServiceDetailPage> enabledButton(
            String id, String element, Function<EServiceDetailPage, Button> locator
    ) {
        return scenario(id, element, "Stato iniziale",
                page -> Assertions.assertThat(locator.apply(page).isDisabled())
                        .as(label(id, element) + " (atteso: abilitato)")
                        .isFalse());
    }

    private static <T extends DomNode & Readable<String>> WebScenario<EServiceDetailPage> enabledElement(
            String id, String element, Function<EServiceDetailPage, T> locator
    ) {
        return scenario(id, element, "Stato iniziale",
                page -> {
                    var found = locator.apply(page).get(FindPolicy.PRESENT);
                    Assertions.assertThat(found).as(label(id, element) + " (atteso: presente)").isPresent();
                    Assertions.assertThat(found.get().getAttributes())
                            .as(label(id, element) + " (atteso: abilitato)")
                            .doesNotContainKey("disabled")
                            .doesNotContainEntry("aria-disabled", "true");
                });
    }
}

