package it.pagopa.interop.suite.contract;

import it.frontend.e2e.framework.web.adapter.model.FindPolicy;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.DomNode;
import it.frontend.e2e.framework.web.model.WebPresentationElement;
import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.Chip;
import it.pagopa.infrastructure.suit.component.Link;
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
                .as(
                        User.getTenantAdmin(Tenant.COMUNE_DI_POZZALLO),
                        Tenant.COMUNE_DI_POZZALLO
                )
                .on(EServiceDetailPage.class, eService.getId().toString(), descriptor.getId().toString())
                .tests(contractScenarios(eService, descriptor));
    }

    private Stream<WebScenario<EServiceDetailPage>> contractScenarios(
            EService eService,
            EServiceDescriptor descriptor
    ) {
        return Stream.of(
                new WebScenario<>(
                        "Catalog e-service detail page shows the expected content and sections",
                        page -> {
                        },
                        page -> {
                            // ---- Intestazione
                            var header = page.header();
                            assertPresent("01", "h1 titolo pagina", header.pageTitle());
                            assertVisible("02", "h1 titolo pagina", header.pageTitle());
                            assertText("03", "h1 titolo pagina", header.pageTitle(), eService.getName());
                            assertPresent("04", "Torna al catalogo", header.backToCatalogAction());
                            assertText("05", "Torna al catalogo", header.backToCatalogAction(), "Torna al catalogo");
                            assertText("06", "Label versione", header.versionHeaderLabel(), "VERSIONE");
                            assertText("07", "Pulsante versione", header.versionShortcutButton(), descriptor.getVersion());
                            assertEnabledButton("08", "Pulsante versione", header.versionShortcutButton());
                            assertText("09", "Chip di stato descrittore", header.descriptorStatusChip(), "attiva");

                            // ---- Azioni primarie
                            var primaryActions = page.primaryActions();
                            assertPresent("11", "Richiedi fruizione", primaryActions.agreementButton());
                            assertVisible("12", "Richiedi fruizione", primaryActions.agreementButton());
                            assertText("13", "Richiedi fruizione", primaryActions.agreementButton(), "Richiedi fruizione");
                            assertEnabledButton("14", "Richiedi fruizione", primaryActions.agreementButton());
                            assertAbsentAll("15", "Visualizza richiesta, Completa richiesta, Aggiorna a nuova versione",
                                    primaryActions.inspectAgreementButton(),
                                    primaryActions.completeAgreementButton(),
                                    primaryActions.upgradeToNewVersionButton());

                            // ---- Alert
                            Assertions.assertThat(page.alerts())
                                    .as(label("16", "Alert di stato descrittore"))
                                    .isEmpty();
                            Assertions.assertThat(page.alerts().stream().map(a -> a.message().read()).toList())
                                    .as(label("17", "Alert portachiavi mancante / chiavi mancanti"))
                                    .noneMatch(msg -> msg.contains("portachiavi"));

                            // ---- Tab
                            var tabs = page.tabs();
                            assertText("18", "Tab Dettaglio e-service", tabs.eserviceDetailTab(), "Dettaglio e-service");
                            assertAttribute("19", "Tab Dettaglio e-service", tabs.eserviceDetailTab(), "aria-selected", "true");
                            assertText("20", "Tab Template finalità collegati", tabs.purposeTemplatesTab(), "Template finalità collegati");
                            assertAttribute("21", "Tab Template finalità collegati", tabs.purposeTemplatesTab(), "aria-selected", "false");
                            assertNotVisible("22", "Pannello tab Template finalità collegati", tabs.purposeTemplatesTabPanel());

                            // ---- Informazioni generali
                            var generalInfo = page.generalInfo();
                            assertText("23", "Titolo sezione Informazioni generali", generalInfo.generalInfoTitle(), "Informazioni generali");
                            assertText("24", "Label produttore", generalInfo.producerLabel(), "Erogatore");
                            assertText("25", "Valore produttore", generalInfo.producerValue(), Tenant.COMUNE_DI_MILANO.getName());
                            assertAbsent("26", "Riga Template in uso", generalInfo.templateInUseLabel());
                            assertText("27", "Label versione", generalInfo.descriptorVersionLabel(), "Stai vedendo la versione");
                            assertText("28", "Valore versione", generalInfo.descriptorVersionValue(), descriptor.getVersion());
                            assertText("29", "Label dati personali", generalInfo.personalDataLabel(), "Eroga dati personali");
                            assertText("30", "Valore dati personali", generalInfo.personalDataValue(), "No");
                            assertText("31", "Label scambio dati", generalInfo.exchangeTypeLabel(), "Scambio dati");
                            assertText("32", "Valore scambio dati", generalInfo.exchangeTypeValue(), "Sincrono");
                            assertText("33", "Label descrizione e-service", generalInfo.eserviceDescriptionLabel(), "Descrizione dell'e-service");
                            assertText("34", "Valore descrizione e-service", generalInfo.eserviceDescriptionValue(), eService.getDescription());
                            assertText("35", "Label descrizione versione", generalInfo.descriptorDescriptionLabel(), "Descrizione della versione");
                            assertText("36", "Valore descrizione versione", generalInfo.descriptorDescriptionValue(), descriptor.getDescription());
                            assertText("37", "Titolo Fruizione tramite delega", generalInfo.delegationSectionTitle(), "Fruizione tramite delega");
                            assertText("38", "Label autorizzazione", generalInfo.consumerDelegableLabel(), "Autorizzazione");
                            assertText("39", "Valore autorizzazione", generalInfo.consumerDelegableValue(), "Sì");
                            assertText("40", "Label autorizzazione client", generalInfo.clientAccessDelegableLabel(), "Autorizzazione all’associazione dei client");
                            assertText("41", "Valore autorizzazione client", generalInfo.clientAccessDelegableValue(), "Sì");
                            assertPresent("42", "Azione Vedi i dettagli tecnici dell’e-service", generalInfo.showTechnicalDetailsAction());
                            assertText("43", "Azione Vedi i dettagli tecnici dell’e-service", generalInfo.showTechnicalDetailsAction(), "Vedi i dettagli tecnici dell’e-service");
                            assertEnabledElement("44", "Azione Vedi i dettagli tecnici dell’e-service", generalInfo.showTechnicalDetailsAction());
                            assertAbsent("45", "Azione Vedi i dettagli sullo scambio asincrono", generalInfo.showAsyncExchangeDetailsAction());
                            assertPresent("46", "Azione Visualizza i contatti dell’erogatore", generalInfo.showProducerContactsAction());

                            // ---- Compilazione agevolata
                            var purposeTemplateSection = page.purposeTemplateSection();
                            assertText("48", "Titolo sezione Compilazione agevolata", purposeTemplateSection.purposeTemplatesTitle(), "Compilazione agevolata della finalità");
                            assertText("49", "Descrizione sezione Compilazione agevolata", purposeTemplateSection.purposeTemplatesDescription(), PURPOSE_TEMPLATES_DESCRIPTION);
                            assertPresent("50", "Pulsante Visualizza i template collegati", purposeTemplateSection.viewLinkedPurposeTemplatesButton());
                            assertText("51", "Pulsante Visualizza i template collegati", purposeTemplateSection.viewLinkedPurposeTemplatesButton(), "Visualizza i template collegati");
                            assertEnabledButton("52", "Pulsante Visualizza i template collegati", purposeTemplateSection.viewLinkedPurposeTemplatesButton());

                            // ---- Signal Hub
                            var signalHub = page.signalHub();
                            assertText("53", "Titolo sezione Signal Hub", signalHub.signalHubTitle(), "Signal Hub");
                            assertText("54", "Descrizione sezione Signal Hub", signalHub.signalHubDescription(), SIGNAL_HUB_DESCRIPTION);
                            assertPresent("55", "Link soluzione integrata", signalHub.signalHubLink());
                            assertText("56", "Link soluzione integrata", signalHub.signalHubLink(), "soluzione integrata");
                            assertText("57", "Label disponibilità", signalHub.signalHubAvailabilityLabel(), "Disponibilità del servizio");
                            assertText("58", "Valore disponibilità", signalHub.signalHubAvailabilityValue(), "No, non è disponibile");

                            // ---- Soglie e attributi
                            var thresholdsAndAttributes = page.thresholdsAndAttributes();
                            assertText("59", "Titolo sezione Soglie e attributi", thresholdsAndAttributes.attributesSectionTitle(), "Soglie e attributi");
                            assertText("60", "Descrizione sezione Soglie e attributi", thresholdsAndAttributes.attributesSectionDescription(), ATTRIBUTES_DESCRIPTION);
                            assertText("61", "Titolo Soglie di chiamate API", thresholdsAndAttributes.thresholdsTitle(), "Soglie di chiamate API");
                            assertText("62", "Label soglia per fruitore", thresholdsAndAttributes.dailyCallsPerConsumerLabel(), "Soglia giornaliera per fruitore");
                            assertText("63", "Valore soglia per fruitore", thresholdsAndAttributes.dailyCallsPerConsumerValue(), "1");
                            assertText("64", "Label soglia totale", thresholdsAndAttributes.dailyCallsTotalLabel(), "Soglia giornaliera totale");
                            assertText("65", "Valore soglia totale", thresholdsAndAttributes.dailyCallsTotalValue(), "10");
                            assertAbsent("66", "Sezione Soglie di chiamate API personalizzate", thresholdsAndAttributes.customizedThresholdsTitle());
                            assertAbsentAll("67", "Gruppi attributi Certificati / Verificati / Dichiarati",
                                    thresholdsAndAttributes.certifiedAttributesTitle(),
                                    thresholdsAndAttributes.verifiedAttributesTitle(),
                                    thresholdsAndAttributes.declaredAttributesTitle());
                            assertText("68", "Titolo sezione interna attributi", thresholdsAndAttributes.attributesTitle(), "Attributi");
                            assertText("68b", "Messaggio nessun attributo richiesto", thresholdsAndAttributes.noAttributesRequiredMessage(), "Questo e-service non richiede attributi");

                            // ---- Elementi non previsti (lista catalogo)
                            assertAbsent("69", "h1 Catalogo degli e-service", page.catalogListTitle());
                            assertAbsentAll("70", "Campi Cerca per nome e Cerca per erogatore",
                                    page.searchByNameField(),
                                    page.searchByProducerField());
                        }
                )
        );
    }

    private static String label(String id, String element) {
        return "CATALOG-DETAILS-" + id + " " + element;
    }

    private static String normalize(String value) {
        return value == null ? null : value.replaceAll("\\s+", " ").trim();
    }

    private static void assertPresent(String id, String el, DomNode node) {
        Assertions.assertThat(node.get(FindPolicy.PRESENT).isPresent())
                .as(label(id, el))
                .isTrue();
    }

    private static void assertAbsent(String id, String el, DomNode node) {
        Assertions.assertThat(node.get(FindPolicy.PRESENT).isPresent())
                .as(label(id, el))
                .isFalse();
    }

    private static void assertVisible(String id, String el, DomNode node) {
        Assertions.assertThat(node.get(FindPolicy.VISIBLE).isPresent())
                .as(label(id, el))
                .isTrue();
    }

    private static void assertNotVisible(String id, String el, DomNode node) {
        Assertions.assertThat(node.get(FindPolicy.VISIBLE).isPresent())
                .as(label(id, el))
                .isFalse();
    }

    private static void assertAbsentAll(String id, String el, DomNode... nodes) {
        Stream.of(nodes).forEach(node ->
                Assertions.assertThat(node.get(FindPolicy.PRESENT))
                        .as(label(id, el))
                        .isEmpty()
        );
    }

    private static void assertText(String id, String el, Readable<String> node, String expected) {
        Assertions.assertThat(normalize(node.read()))
                .as(label(id, el) + " (copy)")
                .isEqualTo(normalize(expected));
    }

    private static void assertText(String id, String el, Chip chip, String expected) {
        assertText(id, el, chip.text(), expected);
    }

    private static void assertText(String id, String el, Link link, String expected) {
        String actual = link.get(FindPolicy.PRESENT)
                .map(WebPresentationElement::getText)
                .orElse(null);
        Assertions.assertThat(normalize(actual))
                .as(label(id, el) + " (copy)")
                .isEqualTo(normalize(expected));
    }

    private static void assertAttribute(String id, String el, DomNode node, String attr, String expected) {
        Assertions.assertThat(node.get(FindPolicy.PRESENT).map(f -> f.getAttributes().get(attr)).orElse(null))
                .as(label(id, el) + " (attributo " + attr + ")")
                .isEqualTo(expected);
    }

    private static void assertEnabledButton(String id, String el, Button button) {
        Assertions.assertThat(button.isDisabled())
                .as(label(id, el))
                .isFalse();
    }

    private static void assertEnabledElement(String id, String el, DomNode node) {
        var found = node.get(FindPolicy.PRESENT);
        Assertions.assertThat(found).as(label(id, el)).isPresent();
        Assertions.assertThat(found.get().getAttributes())
                .as(label(id, el))
                .doesNotContainKey("disabled")
                .doesNotContainEntry("aria-disabled", "true");
    }
}