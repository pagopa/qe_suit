package it.pagopa.interop.suite.contract;

import it.frontend.e2e.framework.core.capability.core.Readable;
import it.frontend.e2e.framework.web.capability.core.Gettable;
import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.infrastructure.suit.component.RadioButton;
import it.pagopa.infrastructure.suit.component.RadioGroup;
import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.common.eservice.domain.EServiceMode;
import it.pagopa.interop.common.eservice.domain.EServiceTechnology;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersion;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersionState;
import it.pagopa.interop.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.web.eservice.infrastructure.page.ProducerEServiceTemplateUpdatePage;
import it.pagopa.interop.web.eservice.infrastructure.suit.component.general_data_form.AboutEServiceTemplateSection;
import it.pagopa.interop.web.eservice.infrastructure.suit.component.general_data_form.EServiceDetailsSection;
import it.pagopa.interop.web.eservice.infrastructure.suit.component.general_data_form.EServiceTemplateSignalHubSection;
import it.pagopa.interop.web.infrastructure.config.WebJUnitSuitConfig;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        classes = {
                TestBootApp.class,
                JunitContextConfig.class,
                WebJUnitSuitConfig.class
        },
        properties = {"spring.profiles.include=junit", "channel.web.browser=chrome", "channel.web.headless=false"}
)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class WebProducerEServiceTemplateCreatePageContractTest {

    private final WebBrowserContractValidator webContractValidator;
    private final InteropJourney interopJourney;

    private static final String SUBMIT_BTN_TEXT = "Salva bozza e prosegui";
    private static final List<String> EXPECTED_BREADCRUMB_LABELS = List.of(
            "Erogazione", "I miei template e-service", "Modifica Template E-Service"
    );
    private static final String EXPECTED_EROGAZIONE_BREADCRUMB_HREF = "/ui/it/erogazione";
    private static final String EXPECTED_MY_TEMPLATES_BREADCRUMB_HREF = "/ui/it/erogazione/template-eservice";
    private static final List<String> EXPECTED_STEP_LABELS = List.of(
            "Informazioni generali", "Soglie e attributi", "Specifiche tecniche", "Informazioni aggiuntive"
    );

    private static final String ABOUT_TEMPLATE_SECTION_TITLE = "Informazioni sul template";
    private static final String TEMPLATE_NAME_LABEL = "Nome del template";
    private static final String TEMPLATE_NAME_HELPER_TEXT =
            "È il nome che comparirà nel catalogo template e-service e che diventerà il nome di base di tutti gli e-service creati a partire da questo template. Min 5 caratteri, max 45 caratteri.";
    private static final String TEMPLATE_INTENDED_TARGET_LABEL = "A chi è rivolto il template?";
    private static final String TEMPLATE_INTENDED_TARGET_HELPER_TEXT = "Min 10 caratteri, max 250 caratteri";
    private static final String TEMPLATE_DESCRIPTION_LABEL = "Cosa permette di fare questo e-service?";
    private static final String TEMPLATE_DESCRIPTION_HELPER_TEXT = "Min 10 caratteri, max 400 caratteri";

    private static final String ESERVICE_DETAILS_SECTION_TITLE = "Dettagli dell'e-service";
    private static final String EXCHANGE_MODE_QUESTION = "In che modo vuoi scambiare i dati dell’e-service?";
    private static final String TECHNOLOGY_QUESTION = "Con quale tecnologia è costruita la tua API?";
    private static final String DATA_FLOW_QUESTION = "L’e-service eroga o riceve dati?";
    private static final String PERSONAL_DATA_DELIVER_QUESTION = "L’e-service eroga dati personali?";
    private static final String ESERVICE_DETAILS_SECTION_SYNC_LABEL = "Sincrono (standard)";
    private static final String ESERVICE_DETAILS_SECTION_ASYNC_LABEL = "Asincrono / massivo (in differita)";
    private static final String ESERVICE_DETAILS_SECTION_REST_LABEL = "REST";
    private static final String ESERVICE_DETAILS_SECTION_SOAP_LABEL = "SOAP";
    private static final String ESERVICE_DETAILS_SECTION_DELIVER_LABEL = "Eroga";
    private static final String ESERVICE_DETAILS_SECTION_RECEIVE_LABEL = "Riceve";

    private static final String SIGNAL_HUB_SECTION_TITLE = "Signal Hub";
    private static final String SIGNAL_HUB_LABEL =
            "Suggerisci a chi userà questo template di segnalare le variazioni dei dati con Signal Hub";
    private static final String SIGNAL_HUB_INFO_LABEL =
            "Signal Hub invia notifiche push in caso di variazioni dei dati dell'e-service.";
    private static final String SIGNAL_HUB_GUIDE_URL =
            "https://developer.pagopa.it/pdnd-interoperabilita/guides/manuale-operativo-signal-hub";

    private static final String ATTRIBUTES_GUIDE_URL =
            "https://developer.pagopa.it/pdnd-interoperabilita/guides/manuale-operativo-pdnd-interoperabilita/v1.0/riferimenti-tecnici/attributi";
    private static final String MOST_USED_ATTRIBUTES_GUIDE_URL =
//            "https://developer.pagopa.it/it/pdnd-interoperabilita/guides/manuale-operativo-pdnd-interoperabilita/v1.0/riferimenti-tecnici/attributi/gli-attributi-piu-utilizzati";
            "https://developer.pagopa.it/pdnd-interoperabilita/guides/manuale-operativo-pdnd-interoperabilita/v1.0/riferimenti-tecnici/attributi/gli-attributi-piu-utilizzati";

    @TestFactory
    @DisplayName("Verify visual and contractual state of the initial e-service template update page (DRAFT v1)")
    Stream<DynamicTest> initialUpdatePageContractMustBeCompliant() {
        EServiceTemplate template = interopJourney
                .withProducer(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN)
                .createEServiceTemplate(EServiceTemplateVersionState.DRAFT)
                .get(EServiceTemplate.class);
        EServiceTemplateVersion draftVersion = template.getLastDraftVersion();

        return webContractValidator
                .as(User.getTenantAdmin(Tenant.COMUNE_DI_MILANO), Tenant.COMUNE_DI_MILANO)
                .on(
                        ProducerEServiceTemplateUpdatePage.class,
                        template.getId().toString(),
                        draftVersion.getId().toString()
                )
                .tests(Stream.of(
                        new WebScenario<>(
                                "Header, stepper, Step 1 cards, prefilled values and submit action must comply with the contract",
                                ProducerEServiceTemplateUpdatePage::assertLoaded,
                                page -> {
                                    assertHeaderAndBreadcrumbs(page, template);
                                    assertStepper(page);
                                    var form = page.generalDataForm();
                                    assertAboutEServiceTemplateSection(form.aboutEServiceTemplateSection(), template);
                                    assertEServiceDetailsSection(form.eServiceDetailsSection(), template);
                                    assertSignalHubSection(form.signalHubSection());
                                    assertSubmitAction(page);
                                }
                        )
                ));
    }

    @TestFactory
    @DisplayName("Verify Step 2 (Soglie e attributi) copy and links after saving Step 1 draft")
    Stream<DynamicTest> step2ThresholdsAndAttributesMustShowCorrectLinksAndCopy() {
        EServiceTemplate template = interopJourney
                .withProducer(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN)
                .createEServiceTemplate(EServiceTemplateVersionState.DRAFT)
                .get(EServiceTemplate.class);
        EServiceTemplateVersion draftVersion = template.getLastDraftVersion();

        return webContractValidator
                .as(User.getTenantAdmin(Tenant.COMUNE_DI_MILANO), Tenant.COMUNE_DI_MILANO)
                .on(
                        ProducerEServiceTemplateUpdatePage.class,
                        template.getId().toString(),
                        draftVersion.getId().toString()
                )
                .tests(Stream.of(
                        new WebScenario<>(
                                "Step 2 Thresholds and Required Attributes sections must show expected copy and links",
                                page -> {
                                    page.assertLoaded();
                                    page.generalDataForm().saveDraft();
                                },
                                page -> {
                                    var thresholdAndAttributeForm = page.thresholdAndAttributeForm();
                                    thresholdAndAttributeForm.assertLoaded();

                                    String attributeHref = thresholdAndAttributeForm
                                            .requiredAttributeSection()
                                            .attributeLink()
                                            .getHref();
                                    assertThat(attributeHref)
                                            .as("The attribute link must be correct")
                                            .isEqualTo(ATTRIBUTES_GUIDE_URL);

                                    String certifiedAttributeHref = thresholdAndAttributeForm
                                            .requiredAttributeSection()
                                            .certifiedAttributeTabPanel()
                                            .findOutMoreAboutAttributesToUseLink()
                                            .getHref();
                                    assertThat(certifiedAttributeHref)
                                            .as("The certified attributes guide link must be correct")
                                            .isEqualTo(MOST_USED_ATTRIBUTES_GUIDE_URL);

                                    String certifiedAttributeDescription = thresholdAndAttributeForm
                                            .requiredAttributeSection()
                                            .certifiedAttributeTabPanel()
                                            .description()
                                            .read();
                                    assertThat(certifiedAttributeDescription)
                                            .as("The certified attribute description must be correct")
                                            .isEqualTo("Sono attributi riconosciuti da fonti certificate come IPA. Tra i più usati, per esempio, ci sono Comuni e loro Consorzi e Associazioni, Province e loro Consorzi e Associazioni, Gestori di Pubblici Servizi. Scopri di più su quali attributi usare.");
                                }
                        )
                ));
    }

    private void assertHeaderAndBreadcrumbs(ProducerEServiceTemplateUpdatePage page, EServiceTemplate template) {
        assertThat(page.backToTemplatesButton().isDisabled())
                .as("Torna ai template abilitato")
                .isFalse();
        page.backToTemplatesButton().readAndAssert(ProducerEServiceTemplateUpdatePage.BTN_BACK_TEMPLATES);

        assertThat(page.breadcrumbs().items().stream().map(Readable::read).toList())
                .as("E-service template update breadcrumb labels in order")
                .containsExactlyElementsOf(EXPECTED_BREADCRUMB_LABELS);
        assertThat(page.breadcrumbs().links()).hasSize(2);
        assertThat(page.breadcrumbs().links().get(0).getHref())
                .as("Erogazione breadcrumb href")
                .isEqualTo(EXPECTED_EROGAZIONE_BREADCRUMB_HREF);
        assertThat(page.breadcrumbs().links().get(1).getHref())
                .as("I miei template e-service breadcrumb href")
                .isEqualTo(EXPECTED_MY_TEMPLATES_BREADCRUMB_HREF);

        page.title().readAndAssert(template.getName());
        page.description().readAndAssert(template.getDescription());
        assertThat(page.requiredFieldsLabel().get(Gettable.FindPolicy.VISIBLE))
                .as("*Campi obbligatori label must be visible")
                .isPresent();
        page.requiredFieldsLabel().readAndAssert("*Campi obbligatori");
    }

    private void assertStepper(ProducerEServiceTemplateUpdatePage page) {
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

    private void assertAboutEServiceTemplateSection(AboutEServiceTemplateSection section, EServiceTemplate template) {
        assertThat(section.title().read()).isEqualTo(ABOUT_TEMPLATE_SECTION_TITLE);

        assertThat(section.nameLabel().read()).contains(TEMPLATE_NAME_LABEL);
        assertThat(section.nameInput().get(Gettable.FindPolicy.CLICKABLE)).as("The name field must be present and enabled").isPresent();
        assertThat(section.nameInput().read()).as("Prefilled template name").isEqualTo(template.getName());
        section.nameHelperText().readAndAssert(TEMPLATE_NAME_HELPER_TEXT);

        assertThat(section.intendedTargetLabel().read()).contains(TEMPLATE_INTENDED_TARGET_LABEL);
        assertThat(section.intendedTargetInput().get(Gettable.FindPolicy.CLICKABLE))
                .as("The intendedTarget field must be present and enabled").isPresent();
        assertThat(section.intendedTargetInput().read())
                .as("Prefilled template intendedTarget").isEqualTo(template.getIntendedTarget());
        section.intendedTargetHelperText().readAndAssert(TEMPLATE_INTENDED_TARGET_HELPER_TEXT);

        assertThat(section.descriptionLabel().read()).contains(TEMPLATE_DESCRIPTION_LABEL);
        assertThat(section.descriptionInput().get(Gettable.FindPolicy.CLICKABLE))
                .as("The description field must be present and enabled").isPresent();
        assertThat(section.descriptionInput().read())
                .as("Prefilled template description").isEqualTo(template.getDescription());
        section.descriptionHelperText().readAndAssert(TEMPLATE_DESCRIPTION_HELPER_TEXT);
    }

    private void assertEServiceDetailsSection(EServiceDetailsSection section, EServiceTemplate template) {
        assertThat(section.title().read()).isEqualTo(ESERVICE_DETAILS_SECTION_TITLE);
        assertThat(section.exchangeModeQuestion().read()).contains(EXCHANGE_MODE_QUESTION);
        assertThat(section.technologyQuestion().read()).contains(TECHNOLOGY_QUESTION);
        assertThat(section.dataFlowQuestion().read()).contains(DATA_FLOW_QUESTION);
        assertThat(section.personalDataQuestion().read()).contains(PERSONAL_DATA_DELIVER_QUESTION);

        assertRadioGroup("Data exchange", section.asyncExchange(),
                List.of(ESERVICE_DETAILS_SECTION_SYNC_LABEL, ESERVICE_DETAILS_SECTION_ASYNC_LABEL),
                ESERVICE_DETAILS_SECTION_SYNC_LABEL);

        String expectedTechnologyLabel = template.getTechnology() == EServiceTechnology.SOAP
                ? ESERVICE_DETAILS_SECTION_SOAP_LABEL
                : ESERVICE_DETAILS_SECTION_REST_LABEL;
        assertRadioGroup("API technology", section.technology(),
                List.of(ESERVICE_DETAILS_SECTION_REST_LABEL, ESERVICE_DETAILS_SECTION_SOAP_LABEL),
                expectedTechnologyLabel);

        String expectedModeLabel = template.getMode() == EServiceMode.RECEIVE
                ? ESERVICE_DETAILS_SECTION_RECEIVE_LABEL
                : ESERVICE_DETAILS_SECTION_DELIVER_LABEL;
        assertRadioGroup("Data flow", section.mode(),
                List.of(ESERVICE_DETAILS_SECTION_DELIVER_LABEL, ESERVICE_DETAILS_SECTION_RECEIVE_LABEL),
                expectedModeLabel);

        String expectedPersonalDataLabel = Boolean.TRUE.equals(template.getPersonalData())
                ? "Eroga dati personali"
                : "Non eroga dati personali";
        assertRadioGroup("Personal data", section.personalData(),
                List.of("Eroga dati personali", "Non eroga dati personali"),
                expectedPersonalDataLabel);
    }

    private void assertRadioGroup(String name, RadioGroup group, List<String> expectedLabels, String... expectedSelectedLabels) {
        var buttons = group.radioButtons();
        assertThat(buttons).as("E-service template update: %s radio group options", name)
                .extracting(RadioButton::getLabel)
                .containsExactlyInAnyOrderElementsOf(expectedLabels);
        assertThat(buttons.stream()
                .filter(RadioButton::isSelected)
                .map(RadioButton::getLabel)
                .toList())
                .as("E-service template update: %s initially selected radio group options", name)
                .containsExactly(expectedSelectedLabels);
    }

    private void assertSignalHubSection(EServiceTemplateSignalHubSection section) {
        assertThat(section.title().read()).isEqualTo(SIGNAL_HUB_SECTION_TITLE);
        section.signalHubLabel().readAndAssert(SIGNAL_HUB_LABEL);
        assertThat(section.infoLabel().read()).isEqualToNormalizingWhitespace(SIGNAL_HUB_INFO_LABEL);
        assertThat(section.pushNotificationsLink().get(Gettable.FindPolicy.PRESENT))
                .as("The link 'notifiche push' must be present")
                .isPresent();
        assertThat(section.pushNotificationsLink().getHref())
                .as("The Signal Hub guide link href must be correct")
                .isEqualTo(SIGNAL_HUB_GUIDE_URL);
        assertThat(section.signalHubSwitch().isDisabled())
                .as("Signal Hub switch must be enabled")
                .isFalse();
        assertThat(section.signalHubSwitch().isChecked())
                .as("Signal Hub switch must be disabled by default")
                .isFalse();
    }

    private void assertSubmitAction(ProducerEServiceTemplateUpdatePage page) {
        var button = page.generalDataForm().saveDraftButton();
        assertThat(button.get(Gettable.FindPolicy.VISIBLE)).isPresent();
        assertThat(button.isDisabled()).isFalse();
        button.readAndAssert(SUBMIT_BTN_TEXT);
    }
}

