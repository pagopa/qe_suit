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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

@SpringBootTest(
        classes = {TestBootApp.class, JunitContextConfig.class, WebJUnitSuitConfig.class},
        properties = {"spring.profiles.include=junit", "channel.web.browser=chrome"}
)
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
                present("01", "h1 titolo pagina", p -> p.header().pageTitle()),
                visible("02", "h1 titolo pagina", p -> p.header().pageTitle()),
                text("03", "h1 titolo pagina", p -> p.header().pageTitle(), eService.getName()),
                present("04", "Torna al catalogo", p -> p.header().backToCatalogAction()),
                text("05", "Torna al catalogo", p -> p.header().backToCatalogAction(), "Torna al catalogo"),
                text("06", "Label versione", p -> p.header().versionHeaderLabel(), "VERSIONE"),
                text("07", "Pulsante versione", p -> p.header().versionShortcutButton(), descriptor.getVersion()),
                enabledButton("08", "Pulsante versione", p -> p.header().versionShortcutButton()),
                text("09", "Chip di stato descrittore", p -> p.header().descriptorStatusChip(), "attiva"),
                // ---- Azioni primarie
                present("11", "Richiedi fruizione", p -> p.primaryActions().agreementButton()),
                visible("12", "Richiedi fruizione", p -> p.primaryActions().agreementButton()),
                text("13", "Richiedi fruizione", p -> p.primaryActions().agreementButton(), "Richiedi fruizione"),
                enabledButton("14", "Richiedi fruizione", p -> p.primaryActions().agreementButton()),
                absentAll("15", "Visualizza richiesta, Completa richiesta, Aggiorna a nuova versione", p -> p.primaryActions().inspectAgreementButton(), p -> p.primaryActions().completeAgreementButton(), p -> p.primaryActions().upgradeToNewVersionButton()),
                // ---- Alert
                scenario("16", "Alert di stato descrittore", "Presenza nel DOM", p -> Assertions.assertThat(p.alerts()).as(label("16", "Alert di stato descrittore")).isEmpty()),
                scenario("17", "Alert portachiavi mancante / chiavi mancanti", "Presenza nel DOM",
                        p -> Assertions.assertThat(p.alerts().stream().map(a -> a.message().read()).toList())
                                .as(label("17", "Alert portachiavi mancante / chiavi mancanti"))
                                .noneMatch(msg -> msg.contains("portachiavi"))),
                // ---- Tab
                text("18", "Tab Dettaglio e-service", p -> p.tabs().eserviceDetailTab(), "Dettaglio e-service"),
                attribute("19", "Tab Dettaglio e-service", p -> p.tabs().eserviceDetailTab(), "aria-selected", "true"),
                text("20", "Tab Template finalità collegati", p -> p.tabs().purposeTemplatesTab(), "Template finalità collegati"),
                attribute("21", "Tab Template finalità collegati", p -> p.tabs().purposeTemplatesTab(), "aria-selected", "false"),
                notVisible("22", "Pannello tab Template finalità collegati", p -> p.tabs().purposeTemplatesTabPanel()),
                // ---- Informazioni generali
                text("23", "Titolo sezione Informazioni generali", p -> p.generalInfo().generalInfoTitle(), "Informazioni generali"),
                text("24", "Label produttore", p -> p.generalInfo().producerLabel(), "Erogatore"),
                text("25", "Valore produttore", p -> p.generalInfo().producerValue(), Tenant.COMUNE_DI_MILANO.getName()),
                absent("26", "Riga Template in uso", p -> p.generalInfo().templateInUseLabel()),
                text("27", "Label versione", p -> p.generalInfo().descriptorVersionLabel(), "Stai vedendo la versione"),
                text("28", "Valore versione", p -> p.generalInfo().descriptorVersionValue(), descriptor.getVersion()),
                text("29", "Label dati personali", p -> p.generalInfo().personalDataLabel(), "Eroga dati personali"),
                text("30", "Valore dati personali", p -> p.generalInfo().personalDataValue(), "No"),
                text("31", "Label scambio dati", p -> p.generalInfo().exchangeTypeLabel(), "Scambio dati"),
                text("32", "Valore scambio dati", p -> p.generalInfo().exchangeTypeValue(), "Sincrono"),
                text("33", "Label descrizione e-service", p -> p.generalInfo().eserviceDescriptionLabel(), "Descrizione dell'e-service"),
                text("34", "Valore descrizione e-service", p -> p.generalInfo().eserviceDescriptionValue(), eService.getDescription()),
                text("35", "Label descrizione versione", p -> p.generalInfo().descriptorDescriptionLabel(), "Descrizione della versione"),
                text("36", "Valore descrizione versione", p -> p.generalInfo().descriptorDescriptionValue(), descriptor.getDescription()),
                text("37", "Titolo Fruizione tramite delega", p -> p.generalInfo().delegationSectionTitle(), "Fruizione tramite delega"),
                text("38", "Label autorizzazione", p -> p.generalInfo().consumerDelegableLabel(), "Autorizzazione"),
                text("39", "Valore autorizzazione", p -> p.generalInfo().consumerDelegableValue(), "Sì"),
                text("40", "Label autorizzazione client", p -> p.generalInfo().clientAccessDelegableLabel(), "Autorizzazione all’associazione dei client"),
                text("41", "Valore autorizzazione client", p -> p.generalInfo().clientAccessDelegableValue(), "Sì"),
                present("42", "Azione Vedi i dettagli tecnici dell’e-service", p -> p.generalInfo().showTechnicalDetailsAction()),
                text("43", "Azione Vedi i dettagli tecnici dell’e-service", p -> p.generalInfo().showTechnicalDetailsAction(), "Vedi i dettagli tecnici dell’e-service"),
                enabledElement("44", "Azione Vedi i dettagli tecnici dell’e-service", p -> p.generalInfo().showTechnicalDetailsAction()),
                absent("45", "Azione Vedi i dettagli sullo scambio asincrono", p -> p.generalInfo().showAsyncExchangeDetailsAction()),
                present("46", "Azione Visualizza i contatti dell’erogatore", p -> p.generalInfo().showProducerContactsAction()),
                notVisible("47", "Drawer specifiche tecniche / scambio asincrono / contatti", p -> p.generalInfo().openDrawer()),
                // ---- Compilazione agevolata
                text("48", "Titolo sezione Compilazione agevolata", p -> p.purposeTemplateSection().purposeTemplatesTitle(), "Compilazione agevolata della finalità"),
                text("49", "Descrizione sezione Compilazione agevolata", p -> p.purposeTemplateSection().purposeTemplatesDescription(), PURPOSE_TEMPLATES_DESCRIPTION),
                present("50", "Pulsante Visualizza i template collegati", p -> p.purposeTemplateSection().viewLinkedPurposeTemplatesButton()),
                text("51", "Pulsante Visualizza i template collegati", p -> p.purposeTemplateSection().viewLinkedPurposeTemplatesButton(), "Visualizza i template collegati"),
                enabledButton("52", "Pulsante Visualizza i template collegati", p -> p.purposeTemplateSection().viewLinkedPurposeTemplatesButton()),
                // ---- Signal Hub
                text("53", "Titolo sezione Signal Hub", p -> p.signalHub().signalHubTitle(), "Signal Hub"),
                text("54", "Descrizione sezione Signal Hub", p -> p.signalHub().signalHubDescription(), SIGNAL_HUB_DESCRIPTION),
                present("55", "Link soluzione integrata", p -> p.signalHub().signalHubLink()),
                text("56", "Link soluzione integrata", p -> p.signalHub().signalHubLink(), "soluzione integrata"),
                text("57", "Label disponibilità", p -> p.signalHub().signalHubAvailabilityLabel(), "Disponibilità del servizio"),
                text("58", "Valore disponibilità", p -> p.signalHub().signalHubAvailabilityValue(), "No, non è disponibile"),
                // ---- Soglie e attributi
                text("59", "Titolo sezione Soglie e attributi", p -> p.thresholdsAndAttributes().attributesSectionTitle(), "Soglie e attributi"),
                text("60", "Descrizione sezione Soglie e attributi", p -> p.thresholdsAndAttributes().attributesSectionDescription(), ATTRIBUTES_DESCRIPTION),
                text("61", "Titolo Soglie di chiamate API", p -> p.thresholdsAndAttributes().thresholdsTitle(), "Soglie di chiamate API"),
                text("62", "Label soglia per fruitore", p -> p.thresholdsAndAttributes().dailyCallsPerConsumerLabel(), "Soglia giornaliera per fruitore"),
                text("63", "Valore soglia per fruitore", p -> p.thresholdsAndAttributes().dailyCallsPerConsumerValue(), "1"),
                text("64", "Label soglia totale", p -> p.thresholdsAndAttributes().dailyCallsTotalLabel(), "Soglia giornaliera totale"),
                text("65", "Valore soglia totale", p -> p.thresholdsAndAttributes().dailyCallsTotalValue(), "10"),
                absent("66", "Sezione Soglie di chiamate API personalizzate", p -> p.thresholdsAndAttributes().customizedThresholdsTitle()),
                absentAll("67", "Gruppi attributi Certificati / Verificati / Dichiarati", p -> p.thresholdsAndAttributes().certifiedAttributesTitle(), p -> p.thresholdsAndAttributes().verifiedAttributesTitle(), p -> p.thresholdsAndAttributes().declaredAttributesTitle()),
                text("68", "Titolo sezione interna attributi", p -> p.thresholdsAndAttributes().attributesTitle(), "Attributi"),
                text("68b", "Messaggio nessun attributo richiesto", p -> p.thresholdsAndAttributes().noAttributesRequiredMessage(), "Questo e-service non richiede attributi"),
                // ---- Elementi non previsti (lista catalogo)
                absent("69", "h1 Catalogo degli e-service", EServiceDetailPage::catalogListTitle),
                absentAll("70", "Campi Cerca per nome e Cerca per erogatore",
                        EServiceDetailPage::searchByNameField, EServiceDetailPage::searchByProducerField)
        );
    }

    private static String label(String id, String element) {
        return "CATALOG-DETAILS-" + id + " " + element;
    }

    private static String normalize(String value) {
        return value == null ? null : value.replaceAll("\\s+", " ").trim();
    }

    private static WebScenario<EServiceDetailPage> scenario(String id, String el, String prop, Consumer<EServiceDetailPage> assertion) {
        return new WebScenario<>("CATALOG-DETAILS-" + id + " – " + el + " – " + prop, page -> {}, assertion);
    }

    private static <T extends DomNode & Readable<String>> WebScenario<EServiceDetailPage> checkPolicy(
            String id, String el, String prop, FindPolicy policy, boolean expectedPresent, Function<EServiceDetailPage, T> loc) {
        return scenario(id, el, prop, page -> Assertions.assertThat(loc.apply(page).get(policy).isPresent())
                .as(label(id, el)).isEqualTo(expectedPresent));
    }

    private static <T extends DomNode & Readable<String>> WebScenario<EServiceDetailPage> present(String id, String el, Function<EServiceDetailPage, T> loc) {
        return checkPolicy(id, el, "Presenza nel DOM", FindPolicy.PRESENT, true, loc);
    }

    private static <T extends DomNode & Readable<String>> WebScenario<EServiceDetailPage> absent(String id, String el, Function<EServiceDetailPage, T> loc) {
        return checkPolicy(id, el, "Presenza nel DOM", FindPolicy.PRESENT, false, loc);
    }

    private static <T extends DomNode & Readable<String>> WebScenario<EServiceDetailPage> visible(String id, String el, Function<EServiceDetailPage, T> loc) {
        return checkPolicy(id, el, "Visibilità", FindPolicy.VISIBLE, true, loc);
    }

    private static <T extends DomNode & Readable<String>> WebScenario<EServiceDetailPage> notVisible(String id, String el, Function<EServiceDetailPage, T> loc) {
        return checkPolicy(id, el, "Visibilità", FindPolicy.VISIBLE, false, loc);
    }

    @SafeVarargs
    private static <T extends DomNode & Readable<String>> WebScenario<EServiceDetailPage> absentAll(String id, String el, Function<EServiceDetailPage, T>... locs) {
        return scenario(id, el, "Presenza nel DOM", page ->
                Stream.of(locs).forEach(loc -> Assertions.assertThat(loc.apply(page).get(FindPolicy.PRESENT)).as(label(id, el)).isEmpty()));
    }

    private static <T extends DomNode & Readable<String>> WebScenario<EServiceDetailPage> text(String id, String el, Function<EServiceDetailPage, T> loc, String expected) {
        return scenario(id, el, "Copy", page -> Assertions.assertThat(normalize(loc.apply(page).read()))
                .as(label(id, el) + " (copy)").isEqualTo(normalize(expected)));
    }

    private static <T extends DomNode & Readable<String>> WebScenario<EServiceDetailPage> attribute(String id, String el, Function<EServiceDetailPage, T> loc, String attr, String expected) {
        return scenario(id, el, "Stato iniziale", page -> Assertions.assertThat(
                        loc.apply(page).get(FindPolicy.PRESENT).map(f -> f.getAttributes().get(attr)).orElse(null))
                .as(label(id, el) + " (attributo " + attr + ")").isEqualTo(expected));
    }

    private static WebScenario<EServiceDetailPage> enabledButton(String id, String el, Function<EServiceDetailPage, Button> loc) {
        return scenario(id, el, "Stato iniziale", page -> Assertions.assertThat(loc.apply(page).isDisabled()).as(label(id, el)).isFalse());
    }

    private static <T extends DomNode & Readable<String>> WebScenario<EServiceDetailPage> enabledElement(String id, String el, Function<EServiceDetailPage, T> loc) {
        return scenario(id, el, "Stato iniziale", page -> {
            var found = loc.apply(page).get(FindPolicy.PRESENT);
            Assertions.assertThat(found).as(label(id, el)).isPresent();
            Assertions.assertThat(found.get().getAttributes()).as(label(id, el))
                    .doesNotContainKey("disabled").doesNotContainEntry("aria-disabled", "true");
        });
    }
}