package it.pagopa.interop.suite.contract;

import it.frontend.e2e.framework.core.capability.core.Readable;
import it.frontend.e2e.framework.web.capability.core.Gettable;
import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersionState;
import it.pagopa.interop.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.web.eservice.infrastructure.page.EServiceCreateFromTemplatePage;
import it.pagopa.interop.web.eservice.infrastructure.suit.component.general_data_form.DelegationsSection;
import it.pagopa.interop.web.eservice.infrastructure.suit.component.general_data_form.EServiceTemplateDetailsSection;
import it.pagopa.interop.web.eservice.infrastructure.suit.component.general_data_form.EServiceTemplateInfoSection;
import it.pagopa.interop.web.eservice.infrastructure.suit.component.general_data_form.InstanceLabelSection;
import it.pagopa.interop.web.eservice.infrastructure.suit.component.general_data_form.SignalHubSection;
import it.pagopa.interop.web.eservice.infrastructure.suit.component.provider_eservice_summary.EServiceSummaryCard.Field;
import it.pagopa.interop.web.infrastructure.config.WebJUnitSuitConfig;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        classes = {
                TestBootApp.class,
                JunitContextConfig.class,
                WebJUnitSuitConfig.class
        },
        properties = "spring.profiles.include=junit"
)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
@DisplayName("Web E-Service Create From Template Contract Test")
public class WebEServiceCreateFromTemplateContractTest {

    private static final List<String> EXPECTED_BREADCRUMB_LABELS = List.of(
            "Erogazione", "I miei e-service", "Crea E-Service da template"
    );
    private static final String EXPECTED_EROGAZIONE_BREADCRUMB_HREF = "/ui/it/erogazione";
    private static final String EXPECTED_MY_ESERVICES_BREADCRUMB_HREF = "/ui/it/erogazione/e-service";
    private static final List<String> EXPECTED_STEP_LABELS = List.of(
            "Informazioni generali", "Soglie e attributi", "Specifiche tecniche", "Informazioni aggiuntive"
    );

    private static final String DELEGATIONS_SECTION_TITLE = "Deleghe";
    private static final String CONSUMER_DELEGABLE_TITLE = "L’e-service può essere fruito tramite delega?";
    private static final String CLIENT_ACCESS_DELEGABLE_TITLE = "Autorizzo l’accesso ai dati in delega";
    private static final String SIGNAL_HUB_SECTION_TITLE = "Signal Hub: servizio di notifica della variazione dei dati";
    private static final String SIGNAL_HUB_LABEL = "Notificherò su Signal Hub le variazioni dei dati";
    private static final String SAMPLE_INSTANCE_LABEL = "testLabel";

    private final WebBrowserContractValidator webContractValidator;
    private final InteropJourney interopJourney;

    @TestFactory
    @DisplayName("Verify visual and contractual state of the e-service creation from template page")
    Stream<DynamicTest> createFromTemplatePageContractTests() {
        EServiceTemplate template = preparePublishedTemplate();

        return webContractValidator
                .as(User.getTenantAdmin(Tenant.COMUNE_DI_MILANO), Tenant.COMUNE_DI_MILANO)
                .on(EServiceCreateFromTemplatePage.class, template.getId().toString())
                .tests(Stream.of(
                        new WebScenario<>(
                                "Verifica componenti, copy, le 5 sezioni e stato iniziale della pagina Crea e-service da template",
                                page -> {},
                                page -> {
                                    page.assertLoaded();
                                    assertHeaderBreadcrumbsAndStepper(page, template);
                                    assertTemplateInfoSection(page.templateInfoSection(), template);
                                    assertTemplateDetailsSection(page.templateDetailsSection(), template);
                                    assertDelegationsSection(page.delegationsSection());
                                    assertSignalHubSection(page.signalHubSection());
                                    assertInstanceLabelSection(page.instanceLabelSection(), template);
                                    assertSubmitAction(page);
                                }
                        )
                ));
    }

    private EServiceTemplate preparePublishedTemplate() {
        return interopJourney
                .switchChannel(Channel.BFF)
                .withProducer(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN)
                .createEServiceTemplate(EServiceTemplateVersionState.PUBLISHED)
                .get(EServiceTemplate.class);
    }

    private void assertHeaderBreadcrumbsAndStepper(EServiceCreateFromTemplatePage page, EServiceTemplate template) {
        // ES-TPL-01 - Back to e-services button
        var backButtonElement = page.backToEServicesButton().get(Gettable.FindPolicy.VISIBLE);
        assertThat(backButtonElement).as("Torna agli e-service visibile").isPresent();
        assertThat(page.backToEServicesButton().isDisabled())
                .as("Torna agli e-service abilitato")
                .isFalse();
        page.backToEServicesButton().readAndAssert(EServiceCreateFromTemplatePage.BTN_BACK_ESERVICE);
        assertThat(backButtonElement.orElseThrow().getAttributes().get("href"))
                .as("Torna agli e-service href")
                .isEqualTo(EXPECTED_MY_ESERVICES_BREADCRUMB_HREF);

        // ES-TPL-02..04 - Breadcrumbs
        assertThat(page.breadcrumbs().get(Gettable.FindPolicy.VISIBLE))
                .as("Breadcrumbs visibili")
                .isPresent();
        assertThat(page.breadcrumbs().items().stream().map(Readable::read).toList())
                .as("E-service from template creation breadcrumb labels in order")
                .containsExactlyElementsOf(EXPECTED_BREADCRUMB_LABELS);
        assertThat(page.breadcrumbs().links()).hasSize(2);
        assertThat(page.breadcrumbs().links().get(0).getHref())
                .as("Erogazione breadcrumb href")
                .isEqualTo(EXPECTED_EROGAZIONE_BREADCRUMB_HREF);
        assertThat(page.breadcrumbs().links().get(1).getHref())
                .as("I miei e-service breadcrumb href")
                .isEqualTo(EXPECTED_MY_ESERVICES_BREADCRUMB_HREF);

        // ES-TPL-05 - Page title
        assertThat(page.title().get(Gettable.FindPolicy.VISIBLE))
                .as("Titolo pagina visibile")
                .isPresent();
        page.title().readAndAssert(EServiceCreateFromTemplatePage.PAGE_TITLE);

        // ES-TPL-06..07 - Subtitle and template catalog link
        assertThat(page.templateDescription().get(Gettable.FindPolicy.VISIBLE))
                .as("Sottotitolo istanza template visibile")
                .isPresent();
        String subtitleText = page.templateDescription().read()
                .replaceFirst("^" + Pattern.quote(EServiceCreateFromTemplatePage.PAGE_TITLE) + "\\s*", "");
        assertThat(subtitleText)
                .as("Sottotitolo istanza template")
                .isEqualToNormalizingWhitespace("Istanza di " + template.getName());

        var templateLinkElement = page.templateLink().get(Gettable.FindPolicy.VISIBLE);
        assertThat(templateLinkElement).as("Link al template nel sottotitolo visibile").isPresent();
        page.templateLink().readAndAssert(template.getName());
        String expectedTemplateHref = "/ui/it/erogazione/catalogo-template/"
                + template.getId() + "/" + template.getLastPublishedVersion().getId();
        assertThat(page.templateLink().getHref())
                .as("Href del link al template")
                .isEqualTo(expectedTemplateHref);
        assertThat(templateLinkElement.orElseThrow().getAttributes().get("target"))
                .as("Target del link al template")
                .isEqualTo("_blank");

        // ES-TPL-08..10 - Stepper
        assertStepper(page);
    }

    private void assertStepper(EServiceCreateFromTemplatePage page) {
        var stepper = page.upsertStepper();
        var stepperElement = stepper.get(Gettable.FindPolicy.VISIBLE);
        assertThat(stepperElement).as("The stepper must be visible").isPresent();
        assertThat(stepperElement.orElseThrow().getAttributes())
                .containsEntry("role", "list");

        var steps = stepper.steps();
        assertThat(steps).as("The stepper must contain four steps").hasSize(4);
        var visibleSteps = steps.stream()
                .map(step -> step.get(Gettable.FindPolicy.VISIBLE))
                .toList();
        assertThat(visibleSteps)
                .as("All four steps must be visible")
                .allSatisfy(element -> assertThat(element).isPresent());
        var stepElements = visibleSteps.stream()
                .map(Optional::orElseThrow)
                .toList();
        assertThat(steps.stream().map(step -> step.number().get(Gettable.FindPolicy.VISIBLE).isPresent()).toList())
                .as("All four step numbers must be visible")
                .containsExactly(true, true, true, true);
        assertThat(steps.stream().map(step -> step.label().get(Gettable.FindPolicy.VISIBLE).isPresent()).toList())
                .as("All four step labels must be visible")
                .containsExactly(true, true, true, true);

        assertThat(steps.stream().map(step -> step.number().read()).toList())
                .containsExactly("1", "2", "3", "4");
        assertThat(steps.stream().map(step -> step.label().read()).toList())
                .containsExactlyElementsOf(EXPECTED_STEP_LABELS);

        assertThat(stepElements.stream()
                        .map(element -> element.getAttributes().get("aria-current"))
                        .toList())
                .as("Only the first step must be current")
                .containsExactly("step", null, null, null);
        assertThat(steps.get(0).isActiveOnLabel())
                .as("The first step must be active")
                .isTrue();
        for (int index = 1; index < steps.size(); index++) {
            assertThat(steps.get(index).isActiveOnLabel())
                    .as("Step %s must not be active", index + 1)
                    .isFalse();
        }
    }

    private void assertTemplateInfoSection(EServiceTemplateInfoSection section, EServiceTemplate template) {
        // ES-TPL-11..15 - Informazioni sul template
        section.title().readAndAssert(EServiceTemplateInfoSection.TITLE);
        section.description().readAndAssert(EServiceTemplateInfoSection.DESCRIPTION);
        assertField(
                section.templateName(),
                EServiceTemplateInfoSection.TEMPLATE_NAME_LABEL,
                template.getName()
        );
        assertField(
                section.intendedTarget(),
                EServiceTemplateInfoSection.INTENDED_TARGET_LABEL,
                template.getIntendedTarget()
        );
        assertField(
                section.allowWhat(),
                EServiceTemplateInfoSection.ALLOW_WHAT_LABEL,
                template.getDescription()
        );
    }

    private void assertTemplateDetailsSection(EServiceTemplateDetailsSection section, EServiceTemplate template) {
        // ES-TPL-16..21 - Dettagli dell'e-service (read-only)
        section.title().readAndAssert(EServiceTemplateDetailsSection.TITLE);
        section.description().readAndAssert(EServiceTemplateDetailsSection.DESCRIPTION);
        assertField(
                section.dataExchange(),
                EServiceTemplateDetailsSection.DATA_EXCHANGE_LABEL,
                "Sincrono"
        );
        assertField(
                section.apiTechnology(),
                EServiceTemplateDetailsSection.API_TECHNOLOGY_LABEL,
                template.getTechnology().getValue()
        );
        assertField(
                section.mode(),
                EServiceTemplateDetailsSection.MODE_LABEL,
                "Eroga"
        );
        assertField(
                section.personalData(),
                EServiceTemplateDetailsSection.PERSONAL_DATA_LABEL,
                Boolean.TRUE.equals(template.getPersonalData()) ? "SI" : "NO"
        );
    }

    private void assertDelegationsSection(DelegationsSection section) {
        // ES-TPL-22..31 - Deleghe
        section.title().readAndAssert(DELEGATIONS_SECTION_TITLE);
        assertThat(section.description().read()).isEqualToNormalizingWhitespace(
                "Consulta la documentazione relativa al funzionamento delle deleghe"
        );
        var docLinkElement = section.documentationLink().get(Gettable.FindPolicy.VISIBLE);
        assertThat(docLinkElement).as("The delegations documentation link must be visible").isPresent();
        assertThat(section.documentationLink().getHref())
                .as("Delegations documentation link href")
                .endsWith("/riferimenti-tecnici/deleghe");
        assertThat(docLinkElement.orElseThrow().getAttributes().get("target"))
                .isEqualTo("_blank");

        section.consumerDelegableTitle().readAndAssert(CONSUMER_DELEGABLE_TITLE);
        section.consumerDelegableLabel().readAndAssert(
                "Autorizzo che i fruitori dell’e-service possano delegare altri enti ad iscriversi per conto proprio"
        );
        assertThat(section.consumerDelegableSwitch().isChecked())
                .as("The delegation consumption must be enabled by default")
                .isTrue();

        section.clientAccessDelegableTitle().readAndAssert(CLIENT_ACCESS_DELEGABLE_TITLE);
        assertThat(section.clientAccessDelegableLabel().read()).isEqualToNormalizingWhitespace(
                "Autorizzo che i delegati possano associare i propri client alle finalità che hanno creato in qualità di delegati alla fruizione. Consulta la documentazione per approfondire"
        );
        assertThat(section.clientAccessDelegableCheckbox().isChecked())
                .as("The access to data by delegation must be authorized by default")
                .isTrue();
        var clientAccessDocElement = section.clientAccessDocLink().get(Gettable.FindPolicy.VISIBLE);
        assertThat(clientAccessDocElement)
                .as("The link to the access by delegation documentation must be visible")
                .isPresent();
        assertThat(section.clientAccessDocLink().getHref())
                .as("Client access delegation documentation link href")
                .endsWith("/riferimenti-tecnici/deleghe/delega-per-la-fruizione");
        assertThat(clientAccessDocElement.orElseThrow().getAttributes().get("target"))
                .isEqualTo("_blank");
    }

    private void assertSignalHubSection(SignalHubSection section) {
        // ES-TPL-32..38 - Signal Hub
        section.title().readAndAssert(SIGNAL_HUB_SECTION_TITLE);
        assertThat(section.description().read()).isEqualToNormalizingWhitespace(
                "Signal Hub è una soluzione integrata che informa i fruitori quando i dati dell'e-service vengono modificati."
        );
        var solutionLinkElement = section.solutionLink().get(Gettable.FindPolicy.VISIBLE);
        assertThat(solutionLinkElement).as("The link to the integrated solution must be visible").isPresent();
        assertThat(section.solutionLink().getHref())
                .isEqualTo("https://developer.pagopa.it/pdnd-interoperabilita/guides/manuale-operativo-signal-hub");
        assertThat(solutionLinkElement.orElseThrow().getAttributes().get("target"))
                .isEqualTo("_blank");

        assertThat(section.designationDescription().read()).isEqualToNormalizingWhitespace(
                "Attivando la funzionalità designi PagoPa S.p.A. responsabile del trattamento dei dati facendo proprio il documento di designazione di PagoPa S.p.A. come responsabile del trattamento dei dati personali per \"Signal Hub\"."
        );
        var designationPdfElement = section.designationPdfLink().get(Gettable.FindPolicy.VISIBLE);
        assertThat(designationPdfElement).as("The link to the designation document must be visible").isPresent();
        assertThat(section.designationPdfLink().getHref()).isNotBlank();
        assertThat(designationPdfElement.orElseThrow().getAttributes().get("target"))
                .isEqualTo("_blank");

        section.signalHubLabel().readAndAssert(SIGNAL_HUB_LABEL);
        assertThat(section.signalHubSwitch().isChecked())
                .as("Signal Hub must be disabled by default")
                .isFalse();
    }

    private void assertInstanceLabelSection(InstanceLabelSection section, EServiceTemplate template) {
        // ES-TPL-39..46 - Parola che identifica l'e-service
        section.title().readAndAssert(InstanceLabelSection.TITLE);
        section.description().readAndAssert(InstanceLabelSection.DESCRIPTION);
        assertThat(section.instanceLabelFieldLabel().read())
                .contains(InstanceLabelSection.FIELD_LABEL);

        var inputElement = section.instanceLabelInput().get(Gettable.FindPolicy.VISIBLE);
        assertThat(inputElement).as("The instanceLabel input must be visible").isPresent();
        assertThat(inputElement.orElseThrow().getAttributes())
                .doesNotContainKey("disabled")
                .containsEntry("maxlength", "12");
        assertThat(section.instanceLabelInput().read())
                .as("Initial instanceLabel input value must be empty")
                .isEmpty();

        section.instanceLabelHelperText().readAndAssert(InstanceLabelSection.HELPER_TEXT);
        section.catalogPreviewLabel().readAndAssert(InstanceLabelSection.CATALOG_PREVIEW_LABEL);
        section.catalogPreviewValue().readAndAssert(template.getName());

        // ES-TPL-46 - Dynamic update of catalog preview when typing an instance label
        section.instanceLabelInput().fill(SAMPLE_INSTANCE_LABEL);
        section.catalogPreviewValue().readAndAssert(template.getName() + " - " + SAMPLE_INSTANCE_LABEL);
    }

    private void assertSubmitAction(EServiceCreateFromTemplatePage page) {
        // ES-TPL-47 - Salva bozza e prosegui button
        var button = page.saveDraftButton();
        assertThat(button.get(Gettable.FindPolicy.VISIBLE))
                .as("Salva bozza e prosegui button must be visible")
                .isPresent();
        assertThat(button.isDisabled()).isFalse();
        button.readAndAssert(EServiceCreateFromTemplatePage.SUBMIT_BTN_TEXT);
    }

    private void assertField(Field field, String expectedLabel, String expectedValue) {
        assertThat(field.get(Gettable.FindPolicy.VISIBLE)).isPresent();
        field.label().readAndAssert(expectedLabel);
        field.value().readAndAssert(expectedValue);
    }
}

