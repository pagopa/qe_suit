package it.pagopa.interop.suite.contract;

import static org.assertj.core.api.Assertions.assertThat;

import it.frontend.e2e.framework.core.capability.core.Gettable;
import it.frontend.e2e.framework.core.capability.core.Readable;
import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersion;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersionState;
import it.pagopa.interop.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.web.eservice.infrastructure.page.ProducerEServiceTemplateEditPage;
import it.pagopa.interop.web.eservice.infrastructure.page.component.template_eservice_stepper.GeneralInfoStep;
import it.pagopa.interop.web.infrastructure.config.WebJUnitSuitConfig;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;


@SpringBootTest(classes = {TestBootApp.class, JunitContextConfig.class, WebJUnitSuitConfig.class},
        properties = "spring.profiles.include=junit")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
class WebProducerEServiceTemplateEditPageContractTest {

    private final WebBrowserContractValidator webContractValidator;
    private final InteropJourney interopJourney;

    private static final List<String> EXPECTED_BREADCRUMB_LABELS =
            List.of("Erogazione", "I miei template e-service", "Modifica Template E-Service");
    private static final List<String> EXPECTED_STEP_LABELS = List.of(
            "Informazioni generali", "Soglie e attributi", "Specifiche tecniche", "Informazioni aggiuntive");

    private static final String CARD_SIGNAL_HUB_TITLE = "Signal Hub";
    private static final String BTN_SAVE_DRAFT = "Salva bozza e prosegui";


    @TestFactory
    @DisplayName("Contract test for ProducerEServiceTemplateEditPage")
    Stream<DynamicTest> producerEServiceTemplateEditPageContractTest() {

        EServiceTemplate template = interopJourney
                .withProducer(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN)
                .createEServiceTemplate(EServiceTemplateVersionState.PUBLISHED)
                .waitUntilEServiceTemplate(t ->
                        t.getLatestVersion().getState() == EServiceTemplateVersionState.PUBLISHED)
                .addVersion(EServiceTemplateVersionState.DRAFT)
                .waitUntilEServiceTemplate(t -> t.getVersions().stream()
                        .anyMatch(v -> v.getState() == EServiceTemplateVersionState.DRAFT))
                .get(EServiceTemplate.class);
        EServiceTemplateVersion version = template.getLastDraftVersion();

        return webContractValidator
                .as(User.getTenantAdmin(Tenant.COMUNE_DI_MILANO), Tenant.COMUNE_DI_MILANO)
                .on(ProducerEServiceTemplateEditPage.class, template.getId().toString(), version.getId().toString())
                .tests(
                        Stream.of(
                                new WebScenario<>(
                                        "Verify initial static state, copy, and Signal Hub card on ProducerEServiceTemplateEditPage",
                                        page -> {},
                                        page -> {
                                            assertBreadcrumbs(page);
                                            assertStepper(page);
                                            assertInfoStep(page.generalInfoStep());
                                        })));
    }

    private void assertInfoStep(GeneralInfoStep generalInfoStep) {
        assertEServiceTemplateCaratherizationCard(generalInfoStep.eServiceTemplateCharacterizationCard());
        assertEServiceDetailsCard(generalInfoStep.eServiceDetailsCard());
        assertSignalHubCard(generalInfoStep.signalHubCard());

        assertThat(generalInfoStep.saveDraftButton().get(Gettable.FindPolicy.VISIBLE).isPresent())
                .as("Save Draft button must be present and VISIBLE in DOM")
                .isTrue();
        generalInfoStep.saveDraftButton().readAndAssert(BTN_SAVE_DRAFT);
    }

    private void assertEServiceDetailsCard(GeneralInfoStep.EServiceDetailsCard eServiceDetailsCard) {
        assertThat(eServiceDetailsCard.get(Gettable.FindPolicy.VISIBLE).isPresent())
                .as("EService Details card container must be present and VISIBLE in DOM")
                .isTrue();

        assertThat(eServiceDetailsCard.eserviceDetailsCardTitle().get(Gettable.FindPolicy.VISIBLE).isPresent())
                .as("EService Details card title must be present in DOM")
                .isTrue();
        eServiceDetailsCard.eserviceDetailsCardTitle().readAndAssert("Dettagli dell'e-service");

        assertThat(eServiceDetailsCard.subTitle().get(Gettable.FindPolicy.VISIBLE).isPresent())
                .as("EService Details card subtitle must be present in DOM")
                .isTrue();
        eServiceDetailsCard.subTitle().readAndAssert("Queste informazioni non sono modificabili.");

        assertThat(eServiceDetailsCard.exchangeDataLabel().get(Gettable.FindPolicy.VISIBLE).isPresent())
                .as("EService Details card exchange data label must be present in DOM")
                .isTrue();
        eServiceDetailsCard.exchangeDataLabel().readAndAssert("Scambio dati");
        assertThat(eServiceDetailsCard.exchangeDataValue().get(Gettable.FindPolicy.VISIBLE).isPresent())
                .as("EService Details card exchange data value must be present in DOM")
                .isTrue();

        assertThat(eServiceDetailsCard.technologyLabel().get(Gettable.FindPolicy.VISIBLE).isPresent())
                .as("EService Details card technology label must be present in DOM")
                .isTrue();
        eServiceDetailsCard.technologyLabel().readAndAssert("Tecnologia API");
        assertThat(eServiceDetailsCard.technologyValue().get(Gettable.FindPolicy.VISIBLE).isPresent())
                .as("EService Details card technology value must be present in DOM")
                .isTrue();

        assertThat(eServiceDetailsCard.producerOrConsumerLabel().get(Gettable.FindPolicy.VISIBLE).isPresent())
                .as("EService Details card producer or consumer label must be present in DOM")
                .isTrue();
        eServiceDetailsCard.producerOrConsumerLabel().readAndAssert("L’e-service eroga o riceve dati?");
        assertThat(eServiceDetailsCard.producerOrConsumerValue().get(Gettable.FindPolicy.VISIBLE).isPresent())
                .as("EService Details card producer or consumer value must be present in DOM")
                .isTrue();

        assertThat(eServiceDetailsCard.personalDataLabel().get(Gettable.FindPolicy.VISIBLE).isPresent())
                .as("EService Details card personal data label must be present in DOM")
                .isTrue();
        eServiceDetailsCard.personalDataLabel().readAndAssert("Eroga dati personali");
        assertThat(eServiceDetailsCard.personalDataValue().get(Gettable.FindPolicy.VISIBLE).isPresent())
                .as("EService Details card personal data value must be present in DOM")
                .isTrue();
    }

    private void assertEServiceTemplateCaratherizationCard(GeneralInfoStep.EServiceTemplateCharacterizationCard eServiceTemplateCharacterizationCard) {
        assertThat(eServiceTemplateCharacterizationCard.get(Gettable.FindPolicy.VISIBLE).isPresent())
                .as("EService Template Characterization card container must be present and VISIBLE in DOM")
                .isTrue();

        assertThat(eServiceTemplateCharacterizationCard.templateCharacterizationCardTitle().get(Gettable.FindPolicy.VISIBLE).isPresent())
                .as("EService Template Characterization card title must be present in DOM")
                .isTrue();
        eServiceTemplateCharacterizationCard.templateCharacterizationCardTitle().readAndAssert("Caratterizzazione template di e-service");

        assertThat(eServiceTemplateCharacterizationCard.subtitle().get(Gettable.FindPolicy.VISIBLE).isPresent())
                .as("EService Template Characterization card subtitle must be present in DOM")
                .isTrue();
        eServiceTemplateCharacterizationCard.subtitle().readAndAssert("Queste informazioni non sono modificabili.");

        assertThat(eServiceTemplateCharacterizationCard.eserviceTemplateNameLabel().get(Gettable.FindPolicy.VISIBLE).isPresent())
                .as("EService Template Characterization card eservice name label must be present in DOM")
                .isTrue();
        eServiceTemplateCharacterizationCard.eserviceTemplateNameLabel().readAndAssert("Nome del template di e-service");
        assertThat(eServiceTemplateCharacterizationCard.eserviceTemplateNameValue().get(Gettable.FindPolicy.VISIBLE).isPresent())
                .as("EService Template Characterization card eservice name value must be present in DOM")
                .isTrue();

        assertThat(eServiceTemplateCharacterizationCard.templateScopeLabel().get(Gettable.FindPolicy.VISIBLE).isPresent())
                .as("EService Template Characterization card template scope label must be present in DOM")
                .isTrue();
        eServiceTemplateCharacterizationCard.templateScopeLabel().readAndAssert("A chi è rivolto questo template?");
        assertThat(eServiceTemplateCharacterizationCard.templateScopeValue().get(Gettable.FindPolicy.VISIBLE).isPresent())
                .as("EService Template Characterization card template scope value must be present in DOM")
                .isTrue();

        assertThat(eServiceTemplateCharacterizationCard.templateUseLabel().get(Gettable.FindPolicy.VISIBLE).isPresent())
                .as("EService Template Characterization card template use label must be present in DOM")
                .isTrue();
        eServiceTemplateCharacterizationCard.templateUseLabel().readAndAssert("Cosa permette di fare questo template?");
        assertThat(eServiceTemplateCharacterizationCard.templateUseValue().get(Gettable.FindPolicy.VISIBLE).isPresent())
                .as("EService Template Characterization card template use value must be present in DOM")
                .isTrue();

    }

    private void assertBreadcrumbs(ProducerEServiceTemplateEditPage page) {
        assertThat(page.breadcrumbs().get().isPresent())
                .as("Breadcrumbs navigation must be present in DOM")
                .isTrue();
        assertThat(page.breadcrumbs().get(Gettable.FindPolicy.VISIBLE))
                .as("Breadcrumbs navigation must be visible")
                .isPresent();
        assertThat(page.breadcrumbs().items().stream().map(Readable::read).toList())
                .as("Verify breadcrumbs labels match approved contract")
                .containsExactlyElementsOf(EXPECTED_BREADCRUMB_LABELS);
        assertThat(page.breadcrumbs().links())
                .as("Breadcrumbs must contain navigation links")
                .isNotEmpty();

        assertThat(page.pageTitle().get(Gettable.FindPolicy.VISIBLE))
                .as("Page title must be present and VISIBLE in DOM")
                .isPresent();
        assertThat(page.pageDescription().get(Gettable.FindPolicy.VISIBLE))
                .as("Page description must be present and VISIBLE in DOM")
                .isPresent();
    }

    private void assertStepper(ProducerEServiceTemplateEditPage page) {
        var stepper = page.stepper();
        var stepperElement = stepper.get(it.frontend.e2e.framework.web.capability.core.Gettable.FindPolicy.VISIBLE);
        assertThat(stepperElement).as("The stepper must be visible").isPresent();
        assertThat(stepperElement.orElseThrow().getAttributes())
                .containsEntry("role", "list");

        var steps = stepper.steps();
        assertThat(steps).as("The stepper must contain four steps").hasSize(4);
        var visibleSteps = steps.stream()
                .map(step -> step.get(it.frontend.e2e.framework.web.capability.core.Gettable.FindPolicy.VISIBLE))
                .toList();
        assertThat(visibleSteps)
                .as("All four steps must be visible")
                .allSatisfy(element -> assertThat(element).isPresent());
        var stepElements = visibleSteps.stream()
                .map(Optional::orElseThrow)
                .toList();
        assertThat(steps.stream().map(step -> step.number().get(it.frontend.e2e.framework.web.capability.core.Gettable.FindPolicy.VISIBLE).isPresent()).toList())
                .as("All four step numbers must be visible")
                .containsExactly(true, true, true, true);
        assertThat(steps.stream().map(step -> step.label().get(it.frontend.e2e.framework.web.capability.core.Gettable.FindPolicy.VISIBLE).isPresent()).toList())
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

    private void assertSignalHubCard(GeneralInfoStep.SignalHubCard signalHub) {
        assertThat(signalHub.get(Gettable.FindPolicy.VISIBLE).isPresent())
                .as("Signal Hub card container must be present and VISIBLE in DOM")
                .isTrue();

        assertThat(signalHub.signalHubCardTitle().get(Gettable.FindPolicy.VISIBLE).isPresent())
                .as("Signal Hub card title must be present in DOM")
                .isTrue();
        signalHub.signalHubCardTitle().readAndAssert(CARD_SIGNAL_HUB_TITLE);

        assertThat(signalHub.switchDescription().get(Gettable.FindPolicy.VISIBLE).isPresent())
                .as("Signal Hub description paragraph must be present in DOM")
                .isTrue();

        assertThat(signalHub.switchPrimaryLabel().get(Gettable.FindPolicy.VISIBLE).isPresent())
                .as("Signal Hub primary label must be present in DOM")
                .isTrue();
        signalHub.switchPrimaryLabel().readAndAssert("Suggerisci a chi userà questo template di segnalare le variazioni dei dati con Signal Hub");

        assertThat(signalHub.switchSignalHub().isDisabled())
                .as("Signal Hub switch input must be initially disabled")
                .isTrue();
    }
}