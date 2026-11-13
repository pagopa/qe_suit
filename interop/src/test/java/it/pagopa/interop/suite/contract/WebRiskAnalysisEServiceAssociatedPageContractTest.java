package it.pagopa.interop.suite.contract;

import it.frontend.e2e.framework.web.capability.core.Gettable;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.model.WebPresentationElement;
import it.pagopa.application.context.EntityStore;
import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.infrastructure.suit.component.Label;
import it.pagopa.infrastructure.suit.component.Link;
import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.eservice.domain.EServiceMode;
import it.pagopa.interop.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.*;
import it.pagopa.interop.common.risk_analysis.application.RiskAnalysisGateway;
import it.pagopa.interop.common.risk_analysis.domain.RiskAnalysisFormConfig;
import it.pagopa.interop.web.eservice.infrastructure.page.EServiceRiskAnalysisAssociatedPage;
import it.pagopa.interop.web.eservice.infrastructure.page.ProducerEServiceDetailPage;
import it.pagopa.interop.web.eservice.infrastructure.page.component.RiskAnalysisQuestionAnswerItem;
import it.pagopa.interop.web.infrastructure.config.WebJUnitSuitConfig;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootTest(
        classes = {TestBootApp.class, JunitContextConfig.class, WebJUnitSuitConfig.class},
        properties = {"spring.profiles.include=junit", "channel.web.browser=chrome", "channel.web.headless=false"}
)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class WebRiskAnalysisEServiceAssociatedPageContractTest {

    private static final Tenant PRODUCER = Tenant.COMUNE_DI_MILANO;

    private static final String BASE_PATH = "/ui/it/erogazione";
    private static final String MY_ESERVICES_PATH = BASE_PATH + "/e-service";
    private static final List<String> EXPECTED_BREADCRUMB_LABELS = List.of(
            "Erogazione", "I miei e-service", "Visualizza e-service", "Analisi del rischio"
    );
    private static final String BACK_TO_ESERVICE_LABEL = "Torna all'e-service";
    private static final String SECTION_TITLE = "Analisi del rischio";
    private static final String SECTION_DESCRIPTION = "Seleziona l'analisi del rischio da visualizzare";
    private static final String COMBOBOX_LABEL = "Analisi del rischio";
    private static final String WATCH_RISK_ANALYSIS_LABEL = "Consulta l'analisi del rischio di questo e-service";

    private final WebBrowserContractValidator webContractValidator;
    private final InteropJourney interopJourney;
    private final EntityStore entityStore;
    private final RiskAnalysisGateway riskAnalysisGateway;

    @TestFactory
    Stream<DynamicTest> riskAnalysisEServiceAssociatedPageContract() {
        PreparedData data = prepareReceiveEServiceWithRiskAnalysisAndPurpose();
        String eservicePath = MY_ESERVICES_PATH + "/" + data.eserviceId() + "/" + data.descriptorId();

        return webContractValidator
                .as(User.getTenantAdmin(PRODUCER), PRODUCER)
                .on(EServiceRiskAnalysisAssociatedPage.class, data.eserviceId(), data.descriptorId())
                .tests(Stream.of(pageScenario(eservicePath, data)));
    }

    /**
     * Fixture: E-Service RECEIVE pubblicato dal producer con una EServiceRiskAnalysis associata.
     * La configurazione del form è letta mentre la sessione è ancora quella del producer.
     */
    private PreparedData prepareReceiveEServiceWithRiskAnalysisAndPurpose() {
        interopJourney
                .switchChannel(Channel.BFF)
                .withProducer(PRODUCER, UserRole.ADMIN)
                .createEService(command -> command.mode(EServiceMode.RECEIVE), EServiceDescriptorState.DRAFT)
                .addRiskAnalysis()
                .updateDescriptor(command -> {}, EServiceDescriptorState.PUBLISHED);

        EService eService = entityStore.getLastOrThrow(EService.class);
        EServiceRiskAnalysis riskAnalysis = entityStore.getLastOrThrow(EServiceRiskAnalysis.class);
        RiskAnalysisFormConfig riskAnalysisConfig = riskAnalysisGateway.getLatestRiskAnalysisConfig(PRODUCER);

        return new PreparedData(
                eService.getId().toString(),
                eService.getActiveDescriptor().getId().toString(),
                riskAnalysis.getName(),
                buildExpectedSummary(riskAnalysis, riskAnalysisConfig)
        );
    }

    /**
     * ES-RA-01 .. ES-RA-21: navigazione, riepilogo e selezione in un unico scenario.
     * L'apertura del menu a tendina modifica lo stato della pagina, quindi è l'ultima verifica.
     */
    private WebScenario<EServiceRiskAnalysisAssociatedPage> pageScenario(String eservicePath, PreparedData data) {
        return new WebScenario<>(
                "Pagina analisi del rischio associata: navigazione, riepilogo e selezione",
                page -> {},
                page -> {
                    assertSelection(page, data);
                    assertSummary(page, data);
                    assertNavigation(page, eservicePath);
                }
        );
    }

    /**
     * ES-RA-01 .. ES-RA-09: navigazione superiore e breadcrumbs.
     */
    private static void assertNavigation(EServiceRiskAnalysisAssociatedPage page, String eservicePath) {
        System.out.println("\n\nassertNavigation\n\n");

        var back = page.backToEServiceButton();
        Assertions.assertThat(back.exists(Gettable.FindPolicy.PRESENT))
                .as("Pulsante 'Torna all'e-service' presente").isTrue();
        Assertions.assertThat(back.exists(Gettable.FindPolicy.VISIBLE))
                .as("Pulsante 'Torna all'e-service' visibile").isTrue();
        Assertions.assertThat(back.isDisabled())
                .as("Pulsante 'Torna all'e-service' abilitato").isFalse();
        Assertions.assertThat(back.read())
                .as("Copy pulsante 'Torna all'e-service'").isEqualTo(BACK_TO_ESERVICE_LABEL);
        Assertions.assertThat(hrefOf(back.get(Gettable.FindPolicy.PRESENT)))
                .as("Destinazione pulsante 'Torna all'e-service'").isEqualTo(eservicePath);

        var breadcrumbs = page.breadcrumbs();
        Assertions.assertThat(breadcrumbs.exists(Gettable.FindPolicy.PRESENT))
                .as("Breadcrumbs presenti").isTrue();
        Assertions.assertThat(breadcrumbs.exists(Gettable.FindPolicy.VISIBLE))
                .as("Breadcrumbs visibili").isTrue();
        Assertions.assertThat(breadcrumbs.items().stream().map(Readable::read).toList())
                .as("Breadcrumbs: voci in ordine")
                .containsExactlyElementsOf(EXPECTED_BREADCRUMB_LABELS);
        Assertions.assertThat(breadcrumbs.links().stream().map(Link::getHref).toList())
                .as("Breadcrumbs: link cliccabili (l'ultimo segmento non è un link)")
                .containsExactly(BASE_PATH, MY_ESERVICES_PATH, eservicePath);
    }

    /**
     * ES-RA-10 .. ES-RA-17: sezione di selezione e stato iniziale del combobox.
     * Ultima verifica: l'apertura del menu modifica lo stato della pagina.
     */
    private static void assertSelection(EServiceRiskAnalysisAssociatedPage page, PreparedData data) {
        System.out.println("\n\nassertSelection\n\n");

        var section = page.selectionSection();
        Assertions.assertThat(section.exists(Gettable.FindPolicy.PRESENT))
                .as("Sezione di selezione presente").isTrue();
        Assertions.assertThat(section.exists(Gettable.FindPolicy.VISIBLE))
                .as("Sezione di selezione visibile").isTrue();

        assertVisibleWithCopy(section.title(), SECTION_TITLE, "Titolo sezione selezione");
        assertVisibleWithCopy(section.description(), SECTION_DESCRIPTION, "Descrizione sezione selezione");

        var input = section.combobox();
        Assertions.assertThat(input.exists(Gettable.FindPolicy.PRESENT))
                .as("Combobox presente").isTrue();
        Assertions.assertThat(input.exists(Gettable.FindPolicy.VISIBLE))
                .as("Combobox visibile").isTrue();
        assertVisibleWithCopy(section.comboboxLabel(), COMBOBOX_LABEL, "Label combobox");

        Map<String, String> inputAttributes = input.get(Gettable.FindPolicy.PRESENT)
                .map(WebPresentationElement::getAttributes)
                .orElseThrow(() -> new IllegalStateException("Combobox non presente nella pagina"));
        Assertions.assertThat(inputAttributes)
                .as("Combobox abilitato").doesNotContainKey("disabled");
        Assertions.assertThat(inputAttributes.get("aria-expanded"))
                .as("Combobox chiuso di default").isEqualTo("false");
        Assertions.assertThat(inputAttributes.get("value"))
                .as("Analisi del rischio selezionata di default").isEqualTo(data.riskAnalysisName());

        Assertions.assertThat(section.popupButton().exists(Gettable.FindPolicy.PRESENT))
                .as("Pulsante apertura menu presente").isTrue();
        Assertions.assertThat(section.popupButton().isDisabled())
                .as("Pulsante apertura menu abilitato").isFalse();
        Assertions.assertThat(section.clearButton().exists(Gettable.FindPolicy.PRESENT))
                .as("Pulsante cancella selezione presente").isTrue();
        Assertions.assertThat(section.clearButton().isDisabled())
                .as("Pulsante cancella selezione abilitato").isFalse();

        section.popupButton().click();
        var options = page.riskAnalysisOptions();
        Assertions.assertThat(options.stream().map(Readable::read).toList())
                .as("Opzioni del menu a tendina")
                .containsExactly(data.riskAnalysisName());
        Assertions.assertThat(options.get(0).isSelected())
                .as("Opzione di default selezionata nel menu")
                .isTrue();
    }

    /**
     * ES-RA-18 .. ES-RA-21: riepilogo dell'analisi del rischio e corrispondenza domande/risposte.
     */
    private static void assertSummary(EServiceRiskAnalysisAssociatedPage page, PreparedData data) {
        System.out.println("\n\nassertSummary\n\n");

        var section = page.summarySection();
        Assertions.assertThat(section.exists(Gettable.FindPolicy.PRESENT))
                .as("Sezione di riepilogo presente").isTrue();
        Assertions.assertThat(section.exists(Gettable.FindPolicy.VISIBLE))
                .as("Sezione di riepilogo visibile").isTrue();
        assertVisibleWithCopy(section.title(), SECTION_TITLE, "Titolo sottosezione riepilogo");

        List<RiskAnalysisQuestionAnswerItem> items = section.questionAnswerItems();
        List<ExpectedQuestionAnswer> expected = data.expectedSummary();
        Assertions.assertThat(items)
                .as("Numero di domande/risposte nel riepilogo")
                .hasSize(expected.size());
        for (int index = 0; index < expected.size(); index++) {
            assertSummaryItem(items.get(index), expected.get(index), index + 1);
        }
    }

    /**
     * ES-RA-22: accesso alla sezione dalla pagina di dettaglio dell'E-Service erogatore.
     */
    private WebScenario<ProducerEServiceDetailPage> watchRiskAnalysisButtonScenario() {
        return new WebScenario<>(
                "Pulsante di accesso all'analisi del rischio dalla pagina di dettaglio dell'e-service",
                page -> {},
                page -> {
                    var button = page.watchRiskAnalysisAssociatedButton();
                    Assertions.assertThat(button.exists(Gettable.FindPolicy.PRESENT))
                            .as("Pulsante di accesso presente").isTrue();
                    Assertions.assertThat(button.exists(Gettable.FindPolicy.VISIBLE))
                            .as("Pulsante di accesso visibile").isTrue();
                    Assertions.assertThat(button.isDisabled())
                            .as("Pulsante di accesso abilitato").isFalse();
                    Assertions.assertThat(button.read())
                            .as("Copy pulsante di accesso").isEqualTo(WATCH_RISK_ANALYSIS_LABEL);
                }
        );
    }

    private static void assertVisibleWithCopy(Label label, String expectedCopy, String description) {
        Assertions.assertThat(label.exists(Gettable.FindPolicy.PRESENT))
                .as("%s presente", description).isTrue();
        Assertions.assertThat(label.exists(Gettable.FindPolicy.VISIBLE))
                .as("%s visibile", description).isTrue();
        Assertions.assertThat(label.read())
                .as("%s: copy", description).isEqualTo(expectedCopy);
    }

    private static void assertSummaryItem(
            RiskAnalysisQuestionAnswerItem item,
            ExpectedQuestionAnswer expected,
            int position
    ) {
        Assertions.assertThat(item.question().exists(Gettable.FindPolicy.VISIBLE))
                .as("Domanda #%s visibile", position).isTrue();
        Assertions.assertThat(item.question().read())
                .as("Domanda #%s", position).isEqualTo(expected.question());
        Assertions.assertThat(item.answer().exists(Gettable.FindPolicy.VISIBLE))
                .as("Risposta #%s visibile", position).isTrue();
        Assertions.assertThat(item.answer().read())
                .as("Risposta #%s", position).isEqualTo(expected.answer());
        if (expected.infoLabel() == null) {
            Assertions.assertThat(item.infoLabel().exists())
                    .as("InfoLabel #%s assente", position).isFalse();
        } else {
            Assertions.assertThat(item.infoLabel().read())
                    .as("InfoLabel #%s", position).isEqualTo(expected.infoLabel());
        }
    }

    /**
     * Ricostruisce in modo deterministico domande/risposte attese dalla configurazione del form
     * e dalle risposte salvate nella EServiceRiskAnalysis, nell'ordine della configurazione.
     */
    private static List<ExpectedQuestionAnswer> buildExpectedSummary(
            EServiceRiskAnalysis riskAnalysis,
            RiskAnalysisFormConfig riskAnalysisConfig
    ) {
        Map<String, List<String>> answers = riskAnalysis.getRiskAnalysisForm().getAnswers();
        return riskAnalysisConfig.getQuestions().stream()
                .filter(question -> answers.containsKey(question.getId()))
                .map(question -> new ExpectedQuestionAnswer(
                        italianLabel(question.getLabel()),
                        resolveAnswer(question, answers.get(question.getId())),
                        italianLabel(question.getInfoLabel())
                ))
                .toList();
    }

    private static String resolveAnswer(RiskAnalysisFormConfig.Question question, List<String> values) {
        if ("text".equals(question.getVisualType())) {
            return values.get(0);
        }
        return Optional.ofNullable(question.getOptions()).orElse(List.of()).stream()
                .filter(option -> values.contains(option.getValue()))
                .map(option -> italianLabel(option.getLabel()))
                .collect(Collectors.joining(", "));
    }

    private static String italianLabel(Map<String, String> labels) {
        return labels == null ? null : labels.get("it");
    }

    private static String hrefOf(Optional<WebPresentationElement> element) {
        return element
                .map(presentationElement -> presentationElement.getAttributes().get("href"))
                .orElse(null);
    }

    private record ExpectedQuestionAnswer(String question, String answer, String infoLabel) {
    }

    private record PreparedData(
            String eserviceId,
            String descriptorId,
            String riskAnalysisName,
            List<ExpectedQuestionAnswer> expectedSummary
    ) {
    }
}

