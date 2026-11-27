package it.pagopa.interop.suite.contract;

import static org.assertj.core.api.Assertions.assertThat;

import it.frontend.e2e.framework.core.capability.core.Gettable;
import it.frontend.e2e.framework.core.capability.core.Readable;
import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.common.agreement.domain.AgreementState;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.common.infrastructure.config.JunitEnvironmentProfiles;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.purpose.domain.Purpose;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.common.purpose.domain.PurposeVersionState;
import it.pagopa.interop.web.purpose.infrastructure.page.ConsumerPurposeEditPage;
import it.pagopa.interop.web.infrastructure.config.WebJUnitSuitConfig;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import it.pagopa.interop.web.purpose.infrastructure.page.purpose_edit_steps.GeneralInfoStep;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;

@ActiveProfiles(resolver = JunitEnvironmentProfiles.class)
@SpringBootTest(
        classes = {
                TestBootApp.class,
                JunitContextConfig.class,
                WebJUnitSuitConfig.class
        }
)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
class WebConsumerPurposeEditPageContractTest {

    private static final String BACK_TO_PURPOSES_LABEL = "Torna alle finalità";
    private static final List<String> EXPECTED_BREADCRUMB_LABELS =
            List.of("Fruizione", "Finalità inoltrate", "Modifica finalità");
    private static final String EXPECTED_PURPOSE_BREADCRUMB_HREF = "/ui/it/fruizione";
    private static final String EXPECTED_SEND_PURPOSE_BREADCRUMB_HREF = "/ui/it/fruizione/finalita";

    private static final String PAGE_TITLE = "Modifica finalità";
    private static final String MANDATORY_FIELDS_NOTE = "*Campi obbligatori";
    private static final List<String> EXPECTED_STEPPER_STEPS = List.of("Informazioni generali", "Assegnazione", "Analisi del rischio");

    private static final String GENERAL_INFO_TITLE = "Informazioni generali";
    private static final String PURPOSE_NAME_LABEL = "Nome della finalità";
    private static final String PURPOSE_NAME_HELPER =
            "Ti aiuterà a distinguerla dalle altre. Min 5 caratteri, max 60 caratteri";
    private static final String PURPOSE_DESCRIPTION_LABEL = "Descrizione finalità";
    private static final String PURPOSE_DESCRIPTION_HELPER = "Min 10 caratteri, max 250 caratteri";
    private static final String FREE_OF_CHARGE_LABEL =
            "Ho diritto all'utilizzo dell'e-service a titolo gratuito";
    private static final String FREE_OF_CHARGE_REASON_LABEL =
            "Spiega perché puoi usare l’e-service a titolo gratuito (richiesto)";
    private static final String FREE_OF_CHARGE_REASON_HELPER =
            "Indica se lo fai in base a un’esenzione o a un’esclusione prevista dalla legge, oppure per un altro motivo. Min 10, max 250 caratteri";

    private static final String API_CALLS_TITLE = "Stima chiamate API";
    private static final String API_CALLS_SUBTITLE =
            "Indica il numero di chiamate previste ogni giorno per questa finalità.";
    private static final String DAILY_CALLS_LABEL = "Stima numero chiamate API/giorno";
    private static final String DAILY_CALLS_HELPER = "Inserisci un numero superiore o uguale a 1";

    private static final String ALERT_TITLE = "Chiamate API disponibili al giorno";
    private static final String ALERT_PER_CONSUMER_LABEL = "Per fruitore";
    private static final String ALERT_TOTAL_LABEL = "Totali";
    private static final String ALERT_DISCLAIMER =
            "Le chiamate disponibili possono variare se l'erogatore riceve altre finalità prima della tua pubblicazione.";

    private static final String SAVE_DRAFT_BUTTON_LABEL = "Salva bozza e prosegui";

    private final WebBrowserContractValidator webContractValidator;
    private final InteropJourney interopJourney;

    private final Tenant tenant = Tenant.SOGECAP;

    @TestFactory
    @DisplayName("Consumer Purpose Edit Page - Visual and Copy Contract")
    Stream<DynamicTest> verifyConsumerPurposeEditPageContract() {
        Purpose purpose = interopJourney
                .withProducer(Tenant.PAGO_PA, UserRole.ADMIN)
                .createEService(EServiceDescriptorState.PUBLISHED)
                .withConsumer(tenant, UserRole.ADMIN)
                .linkAgreement(AgreementState.ACTIVE)
                .createPurpose(PurposeVersionState.ACTIVE)
                .get(Purpose.class);
        return webContractValidator
                .as(User.getTenantAdmin(tenant), tenant)
                .on(ConsumerPurposeEditPage.class, purpose.getId().toString())
                .tests(
                        Stream.of(
                                new WebScenario<>(
                                        "Verify Consumer Purpose Edit page initial structure, copy and states",
                                        page -> {
                                        },
                                        page -> {
                                            assertHeaderAndStepper(page);
                                            assertGeneralInfoCard(page);
                                            assertApiCallsEstimateCard(page);
                                            assertFooterActions(page);
                                        })));
    }

    private void assertHeaderAndStepper(ConsumerPurposeEditPage page) {
        assertThat(page.headerBackToPurposesLink().get(Gettable.FindPolicy.CLICKABLE).isPresent())
                .as("Header 'Back to purposes' link must be present and clickable in DOM")
                .isTrue();
        page.headerBackToPurposesLink().readAndAssert(BACK_TO_PURPOSES_LABEL);

        assertThat(page.breadcrumbs().items().stream().map(Readable::read).toList())
                .as("E-service creation breadcrumb labels in order")
                .containsExactlyElementsOf(EXPECTED_BREADCRUMB_LABELS);
        assertThat(page.breadcrumbs().links()).hasSize(2);
        assertThat(page.breadcrumbs().links().get(0).getHref())
                .as("Erogazione breadcrumb href")
                .isEqualTo(EXPECTED_PURPOSE_BREADCRUMB_HREF);
        assertThat(page.breadcrumbs().links().get(1).getHref())
                .as("I miei e-service breadcrumb href")
                .isEqualTo(EXPECTED_SEND_PURPOSE_BREADCRUMB_HREF);
        page.pageTitle().readAndAssert(PAGE_TITLE);
        page.mandatoryFieldsNote().readAndAssert(MANDATORY_FIELDS_NOTE);

        assertStepper(page);

    }

    private void assertStepper(ConsumerPurposeEditPage page) {
        var stepper = page.stepper();
        var stepperElement = stepper.get(it.frontend.e2e.framework.web.capability.core.Gettable.FindPolicy.VISIBLE);
        assertThat(stepperElement).as("The stepper must be visible").isPresent();
        assertThat(stepperElement.orElseThrow().getAttributes())
                .containsEntry("role", "list");

        var steps = stepper.steps();
        assertThat(steps).as("The stepper must contain 3 steps").hasSize(3);
        var visibleSteps = steps.stream()
                .map(step -> step.get(it.frontend.e2e.framework.web.capability.core.Gettable.FindPolicy.VISIBLE))
                .toList();
        assertThat(visibleSteps)
                .as("All three steps must be visible")
                .allSatisfy(element -> assertThat(element).isPresent());
        var stepElements = visibleSteps.stream()
                .map(Optional::orElseThrow)
                .toList();
        assertThat(steps.stream().map(step -> step.number().get(it.frontend.e2e.framework.web.capability.core.Gettable.FindPolicy.VISIBLE).isPresent()).toList())
                .as("All 3 step numbers must be visible")
                .containsExactly(true, true, true);
        assertThat(steps.stream().map(step -> step.label().get(it.frontend.e2e.framework.web.capability.core.Gettable.FindPolicy.VISIBLE).isPresent()).toList())
                .as("All 3 step labels must be visible")
                .containsExactly(true, true, true);

        assertThat(steps.stream().map(step -> step.number().read()).toList())
                .containsExactly("1", "2", "3");
        assertThat(steps.stream().map(step -> step.label().read()).toList())
                .containsExactlyElementsOf(EXPECTED_STEPPER_STEPS);

        assertThat(stepElements.stream()
                .map(element -> element.getAttributes().get("aria-current"))
                .toList())
                .as("Only the first step must be current")
                .containsExactly("step", null, null);
        assertThat(steps.get(0).isActiveOnLabel())
                .as("The first step must be active")
                .isTrue();
        for (int index = 1; index < steps.size(); index++) {
            assertThat(steps.get(index).isActiveOnLabel())
                    .as("Step %s must not be active", index + 1)
                    .isFalse();
        }
    }

    private void assertGeneralInfoCard(ConsumerPurposeEditPage page) {
        GeneralInfoStep.GeneralInfoCard card = page.generalInfoStep().generalInfoCard();

        assertThat(card.get().isPresent())
                .as("General info card section must be present in DOM")
                .isTrue();

        assertThat(card.sectionTitle().get().isPresent())
                .as("General info section title must be present in DOM")
                .isTrue();
        card.sectionTitle().readAndAssert(GENERAL_INFO_TITLE);

        assertThat(card.titleInput().get(Gettable.FindPolicy.CLICKABLE).isPresent())
                .as("Purpose name input must be enabled")
                .isTrue();
        //Use of startWith because the label contains asterisk for mandatory fields
        assertThat(card.titleInput().label().read().startsWith(PURPOSE_NAME_LABEL))
                .as("Purpose name input label must be correct")
                .isTrue();
        card.titleInput().helperText().readAndAssert(PURPOSE_NAME_HELPER);

        assertThat(card.descriptionInput().get(Gettable.FindPolicy.CLICKABLE).isPresent())
                .as("Purpose description textarea must be present and clickable in DOM")
                .isTrue();

        //Use of startWith because the label contains asterisk for mandatory fields
        assertThat(card.descriptionInput().label().read().startsWith(PURPOSE_DESCRIPTION_LABEL))
                .as("Purpose description input label must be correct")
                .isTrue();
        card.descriptionInput().helperText().readAndAssert(PURPOSE_DESCRIPTION_HELPER);

        assertThat(card.freeOfChargeSwitch().get(Gettable.FindPolicy.CLICKABLE).isPresent())
                .as("Free of charge switch input must be present and clickable in DOM")
                .isTrue();
        card.freeOfChargeLabel().readAndAssert(FREE_OF_CHARGE_LABEL);

        assertThat(card.freeOfChargeSwitch().isChecked())
                .as("Free of charge switch initial state must be checked")
                .isTrue();

        assertThat(card.freeOfChargeReasonInput().get(Gettable.FindPolicy.VISIBLE).isPresent())
                .as("Free of charge reason text field must be visible when switch is checked")
                .isTrue();
        //Use of startWith because the label contains asterisk for mandatory fields
        assertThat(card.freeOfChargeReasonInput().label().read().startsWith(FREE_OF_CHARGE_REASON_LABEL))
                .as("Free of charge reason input label must be correct")
                .isTrue();

        card.freeOfChargeReasonInput().helperText().readAndAssert(FREE_OF_CHARGE_REASON_HELPER);

        card.freeOfChargeSwitch().click();
        assertThat(card.freeOfChargeReasonInput().get(Gettable.FindPolicy.VISIBLE).isPresent())
                .as("Free of charge reason text field must be invisible when switch is checked")
                .isFalse();
    }



    private void assertApiCallsEstimateCard(ConsumerPurposeEditPage page) {
        GeneralInfoStep.ApiCallsEstimateCard card = page.generalInfoStep().apiCallsEstimateCard();

        assertThat(card.get().isPresent())
                .as("API calls estimate card section must be present in DOM")
                .isTrue();
        card.sectionTitle().readAndAssert(API_CALLS_TITLE);
        card.sectionSubtitle().readAndAssert(API_CALLS_SUBTITLE);

        assertThat(card.dailyCallsInput().get(Gettable.FindPolicy.CLICKABLE).isPresent())
                .as("Daily calls number input must be present and clickable in DOM")
                .isTrue();
        //Use of startWith because the label contains asterisk for mandatory fields
        assertThat(card.dailyCallsInput().label().read().startsWith(DAILY_CALLS_LABEL))
                .as("Daily call input label must be correct")
                .isTrue();
        card.dailyCallsInput().helperText().readAndAssert(DAILY_CALLS_HELPER);

        GeneralInfoStep.AvailableCallsAlert alert = card.availableCallsAlert();
        assertThat(alert.get(Gettable.FindPolicy.VISIBLE).isPresent())
                .as("Available daily API calls alert must be present in DOM")
                .isTrue();
        alert.alertTitle().readAndAssert(ALERT_TITLE.toUpperCase());
        alert.perConsumerLabel().readAndAssert(ALERT_PER_CONSUMER_LABEL);
        assertThat(alert.perConsumerValueLabel().get(Gettable.FindPolicy.VISIBLE).isPresent())
                .as("Alert 'Per consumer' calls value must be visible")
                .isTrue();
        alert.totalLabel().readAndAssert(ALERT_TOTAL_LABEL);
        assertThat(alert.totalValueLabel().get(Gettable.FindPolicy.VISIBLE).isPresent())
                .as("Alert 'Total' calls value must be visible")
                .isTrue();

        alert.disclaimerNote().readAndAssert(ALERT_DISCLAIMER);
    }


private void assertFooterActions(ConsumerPurposeEditPage page) {
    assertThat(page.backToPurposesBtn().get(Gettable.FindPolicy.CLICKABLE).isPresent())
            .as("Footer 'Back to purposes' link must be present and clickable in DOM")
            .isTrue();
    assertThat(page.backToPurposesBtn().isDisabled())
            .as("'Back to purposes' submit button must be enabled")
            .isFalse();
    page.backToPurposesBtn().readAndAssert(BACK_TO_PURPOSES_LABEL);

    assertThat(page.saveDraftAndContinueButton().get(Gettable.FindPolicy.CLICKABLE).isPresent())
            .as("'Save draft and continue' submit button must be present and clickable in DOM")
            .isTrue();
    assertThat(page.saveDraftAndContinueButton().isDisabled())
            .as("'Save draft and continue' submit button must be enabled")
            .isFalse();
    page.saveDraftAndContinueButton().readAndAssert(SAVE_DRAFT_BUTTON_LABEL);
}
}