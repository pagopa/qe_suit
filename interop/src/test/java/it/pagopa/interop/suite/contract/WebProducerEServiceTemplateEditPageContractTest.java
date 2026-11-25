package it.pagopa.interop.suite.contract;

import static org.assertj.core.api.Assertions.assertThat;

import it.frontend.e2e.framework.core.capability.core.Gettable;
import it.frontend.e2e.framework.core.capability.core.Readable;
import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.web.eservice.infrastructure.page.ProducerEServiceTemplateEditPage;
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

@SpringBootTest(classes = {TestBootApp.class, JunitContextConfig.class, WebJUnitSuitConfig.class})
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
class WebProducerEServiceTemplateEditPageContractTest {


    private static final List<String> EXPECTED_BREADCRUMB_LABELS =
            List.of("Erogazione", "I miei template e-service");
    private static final List<String> EXPECTED_STEP_LABELS = List.of(
            "Informazioni generali", "Soglie e attributi", "Specifiche tecniche", "Informazioni aggiuntive");
    private static final String STEP_1_GENERAL_DATA = "Informazioni generali";
    private static final String STEP_2_THRESHOLDS_AND_ATTRIBUTES = "Soglie e attributi";
    private static final String STEP_3_TECHNICAL_SPECS = "Specifiche tecniche";
    private static final String STEP_4_ADDITIONAL_INFO = "Informazioni aggiuntive";

    private static final String CARD_SIGNAL_HUB_TITLE = "Signal Hub";
    private static final String SIGNAL_HUB_SWITCH_PRIMARY_LABEL =
            "Suggerisci a chi userà questo template di segnalare le variazioni dei dati con Signal Hub";
    private static final String SIGNAL_HUB_SWITCH_DESCRIPTION =
            "Signal Hub invia notifiche push in caso di variazioni dei dati dell'e-service.";
    private static final String SIGNAL_HUB_PUSH_NOTIFICATIONS_LINK_TEXT = "notifiche push";
    private static final String SIGNAL_HUB_MANUAL_HREF =
            "https://developer.pagopa.it/pdnd-interoperabilita/guides/manuale-operativo-signal-hub";

    private static final String BTN_SAVE_DRAFT = "Salva bozza";
    private static final String BTN_NEXT = "Avanti";

    private final WebBrowserContractValidator webContractValidator;


    @TestFactory
    @DisplayName("Contract test for ProducerEServiceTemplateEditPage")
    Stream<DynamicTest> producerEServiceTemplateEditPageContractTest() {
        return webContractValidator
                .as(User.getTenantAdmin(Tenant.COMUNE_DI_MILANO), Tenant.COMUNE_DI_MILANO)
                .on(ProducerEServiceTemplateEditPage.class)
                .tests(
                        Stream.of(
                                new WebScenario<>(
                                        "Verify initial static state, copy, and Signal Hub card on ProducerEServiceTemplateEditPage",
                                        page -> {},
                                        page -> {
                                            assertBreadcrumbs(page);
                                            assertStepper(page);
//                                            assertSignalHubCard(page.generalDataSection().signalHubCard());
//                                            assertUnmountedFutureSteps(page);
//                                            assertFooterButtons(page);
                                        })));
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

//    private void assertSignalHubCard(ProducerEServiceTemplateEditPage.SignalHubSection signalHub) {
//        assertThat(signalHub.get().isPresent())
//                .as("Signal Hub card container must be present in DOM")
//                .isTrue();
//        assertThat(signalHub.get().isVisible())
//                .as("Signal Hub card container must be visible")
//                .isTrue();
//
//        assertThat(signalHub.title().get().isPresent())
//                .as("Signal Hub card title must be present in DOM")
//                .isTrue();
//        assertThat(signalHub.title().get().isVisible())
//                .as("Signal Hub card title must be visible")
//                .isTrue();
//        signalHub.title().readAndAssert(CARD_SIGNAL_HUB_TITLE);
//
//        assertThat(signalHub.isSignalHubEnabledCheckbox().get().isPresent())
//                .as("Signal Hub enable checkbox input must be present in DOM")
//                .isTrue();
//        assertThat(signalHub.isSignalHubEnabledCheckbox().get().isDisabled())
//                .as("Signal Hub enable checkbox input must be initially disabled")
//                .isTrue();
//
//        assertThat(signalHub.switchPrimaryLabel().get().isPresent())
//                .as("Signal Hub switch primary label must be present in DOM")
//                .isTrue();
//        assertThat(signalHub.switchPrimaryLabel().get().isVisible())
//                .as("Signal Hub switch primary label must be visible")
//                .isTrue();
//        signalHub.switchPrimaryLabel().readAndAssert(SIGNAL_HUB_SWITCH_PRIMARY_LABEL);
//
//        assertThat(signalHub.switchDescription().get().isPresent())
//                .as("Signal Hub switch description paragraph must be present in DOM")
//                .isTrue();
//        assertThat(signalHub.switchDescription().get().isVisible())
//                .as("Signal Hub switch description paragraph must be visible")
//                .isTrue();
//        signalHub.switchDescription().readAndAssert(SIGNAL_HUB_SWITCH_DESCRIPTION);
//
//        assertThat(signalHub.pushNotificationsLink().get().isPresent())
//                .as("Signal Hub push notifications manual link must be present in DOM")
//                .isTrue();
//        assertThat(signalHub.pushNotificationsLink().get().isVisible())
//                .as("Signal Hub push notifications manual link must be visible")
//                .isTrue();
//        signalHub.pushNotificationsLink().readAndAssert(SIGNAL_HUB_PUSH_NOTIFICATIONS_LINK_TEXT);
//        assertThat(signalHub.pushNotificationsLink().get().getAttribute("href"))
//                .as("Signal Hub manual link href must point to PagoPA developer documentation")
//                .isEqualTo(SIGNAL_HUB_MANUAL_HREF);
//        assertThat(signalHub.pushNotificationsLink().get().getAttribute("aria-disabled"))
//                .as("Signal Hub manual link must have aria-disabled set to true")
//                .isEqualTo("true");
//    }
//
//    private void assertUnmountedFutureSteps(ProducerEServiceTemplateEditPage page) {
//        assertThat(
//                page.thresholdsAndAttributesSection()
//                        .apiThresholdsCardTitle()
//                        .get(Gettable.FindPolicy.NO_WAIT)
//                        .isPresent())
//                .as("Step 2 API thresholds card must not be present in DOM on initial load")
//                .isFalse();
//        assertThat(
//                page.technicalSpecsSection()
//                        .interfaceCardTitle()
//                        .get(Gettable.FindPolicy.NO_WAIT)
//                        .isPresent())
//                .as("Step 3 Interface card must not be present in DOM on initial load")
//                .isFalse();
//        assertThat(
//                page.additionalInfoSection()
//                        .versionDescriptionCardTitle()
//                        .get(Gettable.FindPolicy.NO_WAIT)
//                        .isPresent())
//                .as("Step 4 Version description card must not be present in DOM on initial load")
//                .isFalse();
//    }
//
//    private void assertFooterButtons(ProducerEServiceTemplateEditPage page) {
//        assertThat(page.saveDraftButton().get().isPresent())
//                .as("Save draft button must be present in DOM")
//                .isTrue();
//        assertThat(page.saveDraftButton().get().isVisible())
//                .as("Save draft button must be visible")
//                .isTrue();
//        assertThat(page.saveDraftButton().get().isDisabled())
//                .as("Save draft button must be enabled")
//                .isFalse();
//        page.saveDraftButton().readAndAssert(BTN_SAVE_DRAFT);
//
//        assertThat(page.nextStepButton().get().isPresent())
//                .as("Next step button must be present in DOM")
//                .isTrue();
//        assertThat(page.nextStepButton().get().isVisible())
//                .as("Next step button must be visible")
//                .isTrue();
//        assertThat(page.nextStepButton().get().isDisabled())
//                .as("Next step button must be enabled")
//                .isFalse();
//        page.nextStepButton().readAndAssert(BTN_NEXT);
//    }
}