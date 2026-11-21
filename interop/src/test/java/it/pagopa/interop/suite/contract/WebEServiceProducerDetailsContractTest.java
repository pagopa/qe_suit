package it.pagopa.interop.suite.contract;

import it.frontend.e2e.framework.web.adapter.model.FindPolicy;
import it.frontend.e2e.framework.web.model.WebPresentationElement;
import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.infrastructure.suit.component.Label;
import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.common.eservice.application.EServiceDescriptorUseCase;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.web.eservice.infrastructure.page.EServiceProducerDetailsPage;
import it.pagopa.interop.web.infrastructure.config.WebJUnitSuitConfig;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * QA-18084 Visual contract approved from the producer DOM supplied on 14 October 2026.
 * Uses an Italian Comune di Milano administrator and a newly published REST, synchronous
 * e-service with no attributes, personal data, delegation or Signal Hub activation.
 * Checks headings, control inventory, initial tabs, field labels/values and explanatory copy.
 * Resource/document names, IDs and publication dates are not frozen to the supplied snapshot.
 * Only the version caption accepts both DOM "Versione" and CSS-uppercase "VERSIONE",
 * because Selenium reads rendered text and typography is outside this contract.
 * Uses direct navigation and the configured desktop viewport. Sidebar navigation, mobile
 * variants, layout, colours, fonts, screenshots, shared chrome and business actions are excluded.
 * DOM presence and initial attributes are checked separately; no visibility contract is inferred.
 */
@SpringBootTest(
        classes = {TestBootApp.class, JunitContextConfig.class, WebJUnitSuitConfig.class},
        properties = {"spring.profiles.include=junit", "channel.web.browser=chrome"}
)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class WebEServiceProducerDetailsContractTest {
    private final WebBrowserContractValidator webContractValidator;
    private final InteropJourney interopJourney;
    private final EServiceDescriptorUseCase descriptorUseCase;

    @TestFactory
    Stream<DynamicTest> publishedProducerDetailsVisualContract() {
        var creation = descriptorUseCase.getDefaultEServiceCreationCommand(false)
                .handlePersonalData(false).isSignalHubEnabled(false)
                .isConsumerDelegable(false).isClientAccessDelegable(false)
                .description("Descrizione e-service");
        var version = descriptorUseCase.getUpdateEServiceDescriptorCommand(50, 1000)
                .audience(List.of("pagopa.it")).description(null).voucherLifespan(60);
        EService service = interopJourney
                .withProducer(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN)
                .createEService(creation, EServiceDescriptorState.DRAFT)
                .updateDescriptor(version, EServiceDescriptorState.PUBLISHED)
                .get(EService.class);

        return webContractValidator
                .as(User.getTenantAdmin(Tenant.COMUNE_DI_MILANO), Tenant.COMUNE_DI_MILANO)
                .on(EServiceProducerDetailsPage.class, service.getId().toString(),
                        service.getActiveDescriptor().getId().toString())
                .tests(Stream.of(
                        new WebScenario<>("Visual - producer details page complete DOM contract", page -> {}, page -> {
                            // All assertions share the same browser session.
                            assertHeadings(page, service);
                            assertActions(page, service);
                            assertTabs(page);
                            assertFields(page, service);
                            assertSectionCopy(page);
                        })
                ));
    }

    static void assertHeadings(EServiceProducerDetailsPage page, EService service) {
        assertThat(texts(page.title())).as("Single resource title").containsExactly(service.getName());
        assertThat(texts(page.breadcrumbs()))
                .as("The path in the breadcrumbs must be Erogazione/I miei e-service/Visualizza e-service")
                .containsExactly("Erogazione", "I miei e-service", "Visualizza e-service");
        var headingTexts = texts(page.headings());
        assertThat(headingTexts).isNotEmpty();
        assertThat(headingTexts.get(0)).as("Version caption, allowing CSS uppercase only")
                .isIn("Versione", "VERSIONE");
        assertThat(headingTexts).as("Exact heading inventory, including hierarchy order")
                .containsExactly(headingTexts.get(0), "Informazioni generali", "Nome dell’e-service",
                        "Descrizione dell'e-service", "Informazioni versione", "Dettagli",
                        "Richieste di fruizione", "Ciclo di vita", "Specifiche tecniche e-service",
                        "Voucher", "Fruizione tramite delega", "Documentazione", "Link utili",
                        "Signal Hub", "Adesione al servizio di notifica", "Soglie e attributi",
                        "Soglie di chiamate API", "Attributi Certificati", "Attributi Verificati",
                        "Attributi Dichiarati");
        assertThat(texts(page.versionStatus())).as("Published version status").containsExactly("attiva");
        assertThat(elements(page.headings()).stream().map(WebPresentationElement::getTag).toList())
                .as("Heading levels; general information is h2")
                .containsExactly("h2", "h2", "h3", "h3", "h2", "h3", "h3", "h3", "h2",
                        "h3", "h3", "h3", "h3", "h2", "h3", "h2", "h3", "h3", "h3", "h3");
        assertThat(normalize(page.sections().get(0).read()))
                .contains("Nome dell’e-service Modifica " + service.getName(),
                        "Descrizione dell'e-service Modifica " + service.getDescription());
    }

    static void assertActions(EServiceProducerDetailsPage page, EService service) {
        // getAll uses CLICKABLE: the adapter requires every matched node to be enabled,
        // without filtering any node out. A failed lookup is a failure, never successful absence.
        var buttons = elements(page.buttons());
        assertThat(buttons.stream().map(WebPresentationElement::getText).toList())
                .as("Exact button inventory; no consumer, draft or extra actions")
                .containsExactly("", "1", "Sospendi versione", "Crea nuova versione",
                        "Dettagli e-service", "Portachiavi associati", "Modifica", "Modifica",
                        "Scarica l'elenco dei fruitori", "Scarica questa versione dell’e-service",
                        "Modifica", "", "", "", "Modifica", "Modifica", "Modifica",
                        interfaceName(service), "", "Modifica", "Modifica");
        for (var button : buttons) {
            assertThat(button.getAttributes()).as("Enabled control: %s", button.getText())
                    .doesNotContainKey("disabled").doesNotContainEntry("aria-disabled", "true");
        }
        assertThat(buttons.get(0).getAttributes()).containsEntry("aria-label", "Apri menù delle azioni")
                .containsEntry("aria-haspopup", "true");
        for (int index : List.of(11, 12, 13, 18)) {
            assertThat(buttons.get(index).getAttributes()).containsEntry("aria-label", "Copia");
        }
        assertThat(buttons.get(17).getAttributes())
                .containsEntry("aria-label", "Scarica il documento " + interfaceName(service));
    }

    static void assertTabs(EServiceProducerDetailsPage page) {
        assertThat(page.tabs()).hasSize(2);
        assertThat(page.panels()).hasSize(2);
        for (int index = 0; index < 2; index++) {
            var tab = page.tabs().get(index).get(FindPolicy.PRESENT).orElseThrow();
            var panel = page.panels().get(index).get(FindPolicy.PRESENT).orElseThrow();
            assertThat(tab.getText()).isEqualTo(index == 0 ? "Dettagli e-service" : "Portachiavi associati");
            assertThat(tab.getAttributes()).containsEntry("aria-selected", index == 0 ? "true" : "false")
                    .containsEntry("aria-controls", panel.getAttributes().get("id"));
            assertThat(panel.getAttributes()).containsEntry("aria-labelledby", tab.getAttributes().get("id"));
            if (index == 0) assertThat(panel.getAttributes()).doesNotContainKey("hidden");
            else assertThat(panel.getAttributes()).containsKey("hidden");
        }
    }

    static void assertFields(EServiceProducerDetailsPage page, EService service) {
        var fields = page.fields();
        assertThat(fields.stream().map(field -> field.label().get(FindPolicy.PRESENT).orElseThrow().getText()).toList())
                .as("Complete field label inventory")
                .containsExactly("Stai vedendo la versione", "Eroga dati personali", "Scambio dati",
                        "Descrizione della versione", "Approvazione richieste", "Data pubblicazione",
                        "ID e-service", "ID versione", "ID erogatore", "Tecnologia", "Scambio dati",
                        "Audience", "Tipologia e-service", "Durata del voucher", "Autorizzazione",
                        "Autorizzazione all’associazione dei client", "Elenco documenti",
                        "Checksum file di interfaccia", "Stato di attivazione",
                        "Soglia giornaliera per fruitore", "Soglia giornaliera totale");
        List<String> expected = List.of("1", "No", "Sincrono", "", "Automatica", "",
                service.getId().toString(), service.getActiveDescriptor().getId().toString(),
                service.getProducerId(), "REST", "Sincrono", "pagopa.it", "Eroga", "1 minuto",
                "No", "No", interfaceName(service), "", "Non attivo", "50", "1000");
        for (int index = 0; index < expected.size(); index++) {
            String actual = fields.get(index).value().get(FindPolicy.PRESENT).orElseThrow().getText().trim();
            if (index == 5) assertThat(actual).as("Publication date").isNotBlank();
            else if (index == 17) assertThat(actual).as("Interface checksum").matches("[a-fA-F0-9]{64}");
            else assertThat(actual).as("Field value at index %s", index).isEqualTo(expected.get(index));
        }
    }

    static void assertSectionCopy(EServiceProducerDetailsPage page) {
        assertThat(page.sections()).hasSize(5);
        assertThat(texts(page.explanations()).stream().map(WebEServiceProducerDetailsContractTest::normalize).toList())
                .as("Exact explanatory paragraphs")
                .containsExactly(
                "In questa sezione potrai verificare le specifiche dell’e-service ed apportare modifiche.",
                "Signal Hub è una soluzione integrata che informa i fruitori quando i dati dell'e-service vengono modificati.",
                "Qui trovi le soglie e gli attributi che sono richiesti per la fruizione di questo e-service.",
                "Sono attributi certificati da una fonte autoritativa riconosciuta.",
                "Sono attributi verificati da altre organizzazioni, con la possibilità di richiedere una verifica dall’erogatore.",
                "Sono attributi dichiarati dal fruitore stesso durante la richiesta di fruizione. Non è necessaria una verifica dell’erogatore.");
        assertThat(texts(page.technicalLinks())).as("Technical help link copy")
                .containsExactly("Come implemento e gestisco un e-service?",
                        "Come faccio a verificare il voucher di un fruitore?", "URL .well-known");
        assertThat(normalize(page.sections().get(4).read())).contains(
                "Questo e-service non richiede attributi certificati",
                "Questo e-service non richiede attributi verificati",
                "Questo e-service non richiede attributi dichiarati");
    }

    private static String normalize(String text) {
        return text.replaceAll("\\s+", " ").replace(" ,", ",").trim();
    }

    private static String interfaceName(EService service) {
        return service.getActiveDescriptor().getInterfaceDocument().getPrettyName();
    }

    private static List<WebPresentationElement> elements(Label label) {
        return label.getAll().orElseThrow(() -> new AssertionError("DOM collection lookup failed"));
    }

    private static List<String> texts(Label label) {
        return elements(label).stream().map(WebPresentationElement::getText).toList();
    }
}
