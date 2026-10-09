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
        properties = {"spring.profiles.include=junit", "channel.web.browser=chrome", "channel.web.headless=false"}
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
//                present("01", "h1 titolo pagina", EServiceDetailPage::pageTitle),
//                visible("02", "h1 titolo pagina", EServiceDetailPage::pageTitle),
//                text("03", "h1 titolo pagina", EServiceDetailPage::pageTitle, eService.getName()),
//                present("04", "Torna al catalogo", EServiceDetailPage::backToCatalogAction),
//                text("05", "Torna al catalogo", EServiceDetailPage::backToCatalogAction, "Torna al catalogo"),
//                // h2 rendered in uppercase by the frontend (text-transform)
//                text("06", "Label versione", EServiceDetailPage::versionHeaderLabel, "VERSIONE"),
//                text("07", "Pulsante versione", EServiceDetailPage::versionShortcutButton, descriptor.getVersion()),
//                enabledButton("08", "Pulsante versione", EServiceDetailPage::versionShortcutButton),
//                text("09", "Chip di stato descrittore", EServiceDetailPage::descriptorStatusChip, "attiva"),

//
//                // ---- Azioni primarie
//                present("11", "Richiedi fruizione", EServiceDetailPage::agreementButton),
//                visible("12", "Richiedi fruizione", EServiceDetailPage::agreementButton),
//                text("13", "Richiedi fruizione", EServiceDetailPage::agreementButton, "Richiedi fruizione"),
//                enabledButton("14", "Richiedi fruizione", EServiceDetailPage::agreementButton),
//                absentAll("15", "Visualizza richiesta, Completa richiesta, Aggiorna a nuova versione",
//                        EServiceDetailPage::inspectAgreementButton,
//                        EServiceDetailPage::completeAgreementButton,
//                        EServiceDetailPage::upgradeToNewVersionButton),
//
//                // ---- Alert
//                scenario("16", "Alert di stato descrittore", "Presenza nel DOM",
//                        page -> Assertions.assertThat(page.alerts())
//                                .as(label("16", "Alert di stato descrittore"))
//                                .isEmpty()),
//                scenario("17", "Alert portachiavi mancante / chiavi mancanti", "Presenza nel DOM",
//                        page -> Assertions.assertThat(page.alerts().stream().map(a -> a.message().read()).toList())
//                                .as(label("17", "Alert portachiavi mancante / chiavi mancanti"))
//                                .noneMatch(message -> message.contains("portachiavi"))),
//
//                // ---- Tab
//                text("18", "Tab Dettaglio e-service", EServiceDetailPage::eserviceDetailTab, "Dettaglio e-service"),
//                attribute("19", "Tab Dettaglio e-service", EServiceDetailPage::eserviceDetailTab, "aria-selected", "true"),
//                text("20", "Tab Template finalità collegati", EServiceDetailPage::purposeTemplatesTab, "Template finalità collegati"),
//                attribute("21", "Tab Template finalità collegati", EServiceDetailPage::purposeTemplatesTab, "aria-selected", "false"),
//                notVisible("22", "Pannello tab Template finalità collegati", EServiceDetailPage::purposeTemplatesTabPanel),
//
//                // ---- Informazioni generali
//                text("23", "Titolo sezione Informazioni generali", EServiceDetailPage::generalInfoTitle, "Informazioni generali"),
//                text("24", "Label produttore", EServiceDetailPage::producerLabel, "Erogatore"),
//                text("25", "Valore produttore", EServiceDetailPage::producerValue, Tenant.COMUNE_DI_MILANO.getName()),
//                absent("26", "Riga Template in uso", EServiceDetailPage::templateInUseLabel),
//                text("27", "Label versione", EServiceDetailPage::descriptorVersionLabel, "Stai vedendo la versione"),
//                text("28", "Valore versione", EServiceDetailPage::descriptorVersionValue, descriptor.getVersion()),
//                text("29", "Label dati personali", EServiceDetailPage::personalDataLabel, "Eroga dati personali"),
                text("30", "Valore dati personali", EServiceDetailPage::personalDataValue, "No"),
//                text("31", "Label scambio dati", EServiceDetailPage::exchangeTypeLabel, "Scambio dati"),
                text("32", "Valore scambio dati", EServiceDetailPage::exchangeTypeValue, "Sincrono"),
//                text("33", "Label descrizione e-service", EServiceDetailPage::eserviceDescriptionLabel, "Descrizione dell'e-service"),
                text("34", "Valore descrizione e-service", EServiceDetailPage::eserviceDescriptionValue, eService.getDescription()),
//                text("35", "Label descrizione versione", EServiceDetailPage::descriptorDescriptionLabel, "Descrizione della versione"),
                text("36", "Valore descrizione versione", EServiceDetailPage::descriptorDescriptionValue, descriptor.getDescription()),
//                text("37", "Titolo Fruizione tramite delega", EServiceDetailPage::delegationSectionTitle, "Fruizione tramite delega"),
//                text("38", "Label autorizzazione", EServiceDetailPage::consumerDelegableLabel, "Autorizzazione"),
                text("39", "Valore autorizzazione", EServiceDetailPage::consumerDelegableValue, "Sì"),
//                text("40", "Label autorizzazione client", EServiceDetailPage::clientAccessDelegableLabel, "Autorizzazione all’associazione dei client"),
                text("41", "Valore autorizzazione client", EServiceDetailPage::clientAccessDelegableValue, "Sì"),
//                present("42", "Azione Vedi i dettagli tecnici dell’e-service", EServiceDetailPage::showTechnicalDetailsAction),
//                text("43", "Azione Vedi i dettagli tecnici dell’e-service", EServiceDetailPage::showTechnicalDetailsAction, "Vedi i dettagli tecnici dell’e-service"),
//                enabledElement("44", "Azione Vedi i dettagli tecnici dell’e-service", EServiceDetailPage::showTechnicalDetailsAction),
//                absent("45", "Azione Vedi i dettagli sullo scambio asincrono", EServiceDetailPage::showAsyncExchangeDetailsAction),
                absent("46", "Azione Visualizza i contatti dell’erogatore", EServiceDetailPage::showProducerContactsAction),
//                notVisible("47", "Drawer specifiche tecniche / scambio asincrono / contatti", EServiceDetailPage::openDrawer),
//
//                // ---- Compilazione agevolata
//                text("48", "Titolo sezione Compilazione agevolata", EServiceDetailPage::purposeTemplatesTitle, "Compilazione agevolata della finalità"),
//                text("49", "Descrizione sezione Compilazione agevolata", EServiceDetailPage::purposeTemplatesDescription, PURPOSE_TEMPLATES_DESCRIPTION),
//                present("50", "Pulsante Visualizza i template collegati", EServiceDetailPage::viewLinkedPurposeTemplatesButton),
                text("51", "Pulsante Visualizza i template collegati", EServiceDetailPage::viewLinkedPurposeTemplatesButton, "Visualizza i template collegati"),
//                enabledButton("52", "Pulsante Visualizza i template collegati", EServiceDetailPage::viewLinkedPurposeTemplatesButton),
//
//                // ---- Signal Hub
//                text("53", "Titolo sezione Signal Hub", EServiceDetailPage::signalHubTitle, "Signal Hub"),
//                text("54", "Descrizione sezione Signal Hub", EServiceDetailPage::signalHubDescription, SIGNAL_HUB_DESCRIPTION),
//                present("55", "Link soluzione integrata", EServiceDetailPage::signalHubLink),
//                text("56", "Link soluzione integrata", EServiceDetailPage::signalHubLink, "soluzione integrata"),
//                text("57", "Label disponibilità", EServiceDetailPage::signalHubAvailabilityLabel, "Disponibilità del servizio"),
                text("58", "Valore disponibilità", EServiceDetailPage::signalHubAvailabilityValue, "No, non è disponibile"),
//
//                // ---- Soglie e attributi
//                text("59", "Titolo sezione Soglie e attributi", EServiceDetailPage::attributesSectionTitle, "Soglie e attributi"),
//                text("60", "Descrizione sezione Soglie e attributi", EServiceDetailPage::attributesSectionDescription, ATTRIBUTES_DESCRIPTION),
//                text("61", "Titolo Soglie di chiamate API", EServiceDetailPage::thresholdsTitle, "Soglie di chiamate API"),
//                text("62", "Label soglia per fruitore", EServiceDetailPage::dailyCallsPerConsumerLabel, "Soglia giornaliera per fruitore"),
                text("63", "Valore soglia per fruitore", EServiceDetailPage::dailyCallsPerConsumerValue, "1"),
//                text("64", "Label soglia totale", EServiceDetailPage::dailyCallsTotalLabel, "Soglia giornaliera totale"),
                text("65", "Valore soglia totale", EServiceDetailPage::dailyCallsTotalValue, "10"),
//                absent("66", "Sezione Soglie di chiamate API personalizzate", EServiceDetailPage::customizedThresholdsTitle),
                absentAll("67", "Gruppi attributi Certificati / Verificati / Dichiarati",
                        EServiceDetailPage::certifiedAttributesTitle,
                        EServiceDetailPage::verifiedAttributesTitle,
                        EServiceDetailPage::declaredAttributesTitle)//,
//                text("68", "Titolo sezione interna attributi", EServiceDetailPage::attributesTitle, "Attributi"),
//                text("68b", "Messaggio nessun attributo richiesto", EServiceDetailPage::noAttributesRequiredMessage, "Questo e-service non richiede attributi"),
//
//                // ---- Elementi non previsti (lista catalogo)
//                absent("69", "h1 Catalogo degli e-service", EServiceDetailPage::catalogListTitle),
//                absentAll("70", "Campi Cerca per nome e Cerca per erogatore",
//                        EServiceDetailPage::searchByNameField,
//                        EServiceDetailPage::searchByProducerField)
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

