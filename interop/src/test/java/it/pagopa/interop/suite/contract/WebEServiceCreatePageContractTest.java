package it.pagopa.interop.suite.contract;

import it.frontend.e2e.framework.web.adapter.model.FindPolicy;
import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.web.eservice.infrastructure.suit.component.eservice_create_process.DelegationsCard;
import it.pagopa.interop.web.eservice.infrastructure.suit.component.eservice_create_process.DetailsCard;
import it.pagopa.interop.web.eservice.infrastructure.suit.component.eservice_create_process.InformationCard;
import it.pagopa.interop.web.eservice.infrastructure.suit.component.eservice_create_process.SignalHubCard;
import it.pagopa.interop.web.eservice.infrastructure.suit.page.EServiceCreatePage;
import it.pagopa.interop.web.infrastructure.config.WebJUnitSuitConfig;
import it.pagopa.utils.RandomUtils;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.stream.Stream;

import static it.pagopa.interop.web.eservice.infrastructure.suit.page.EServiceCreatePage.*;
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
@Tag("wait_for_qa")
public class WebEServiceCreatePageContractTest {

    private final WebBrowserContractValidator webContractValidator;

    String SECOND_STEP_LABEL = "Soglie e attributi";
    String THIRD_STEP_LABEL = "Specifiche tecniche";
    String FOURTH_STEP_LABEL = "Informazioni aggiuntive";

    /**
     * Verifies the initial state of the e-service creation page, opened as a tenant admin.
     * <p>
     * The single scenario checks:
     * <ul>
     *   <li>the header, breadcrumbs and stepper;</li>
     *   <li>the copy, links and default values of the four cards (information, details,
     *       delegations and Signal Hub);</li>
     *   <li>the enabled "Salva bozza e prosegui" button.</li>
     * </ul>
     *
     * @return a stream containing the dynamic test of the initial page contracts
     */
    @TestFactory
    @DisplayName("Verify visual and contractual state of the initial e-service creation page")
    Stream<DynamicTest> initialCreatePageContractMustBeCompliant() {
        return webContractValidator
                .as(User.getTenantAdmin(Tenant.COMUNE_DI_MILANO), Tenant.COMUNE_DI_MILANO)
                .on(EServiceCreatePage.class)
                .tests(Stream.of(
                        new WebScenario<>(
                                "All components, the 4 cards, and the initial states must comply with the contract",
                                page -> {},
                                page -> {
                                    assertHeaderAndStepper(page);
                                    assertInformationCard(page.infoCard());
                                    assertDetailsCard(page.detailsCard());
                                    assertDelegationsCard(page.delegationsCard());
                                    assertSignalHubCard(page.signalHubCard());
                                    assertSubmitAction(page);
                                }
                        )
                ));
    }

    /**
     * Verifies the back button, breadcrumbs, page title and stepper.
     *
     * @param page the e-service creation page
     */
    private void assertHeaderAndStepper(EServiceCreatePage page) {
        assertThat(page.backButton().isDisabled()).isFalse();
        page.backButton().readAndAssert(BACK_TO_ESERVICE);
        assertThat(page.breadcrumbErogazione().get().orElseThrow().getText()).isEqualTo(BREADCRUMB_EROGAZIONE);
        assertThat(page.breadcrumbMyEServices().get().orElseThrow().getText()).isEqualTo(BREADCRUMB_MY_ESERVICES);
        page.breadcrumbCurrentPage().readAndAssert(BREADCRUMB_CURRENT);
        page.title().readAndAssert(PAGE_TITLE);
        assertStepper(page);
    }

    /**
     * Verifies the rendered steps and their initial active/disabled states.
     *
     * @param page the e-service creation page
     */
    private void assertStepper(EServiceCreatePage page) {
        var stepper = page.upsertStepper();
        var stepperElement = stepper.get(FindPolicy.VISIBLE);
        assertThat(stepperElement).as("The stepper must be visible").isPresent();
        assertThat(stepperElement.orElseThrow().getAttributes())
                .containsEntry("role", "list");

        var steps = stepper.steps();
        assertThat(steps).as("The stepper must contain four steps").hasSize(4);
        assertThat(steps.stream().map(step -> step.get(FindPolicy.VISIBLE).isPresent()).toList())
                .as("All four steps must be visible")
                .containsExactly(true, true, true, true);
        assertThat(steps.stream().map(step -> step.number().get(FindPolicy.VISIBLE).isPresent()).toList())
                .as("All four step numbers must be visible")
                .containsExactly(true, true, true, true);
        assertThat(steps.stream().map(step -> step.label().get(FindPolicy.VISIBLE).isPresent()).toList())
                .as("All four step labels must be visible")
                .containsExactly(true, true, true, true);

        assertThat(steps.stream().map(step -> step.number().read()).toList())
                .containsExactly("1", "2", "3", "4");
        assertThat(steps.stream().map(step -> step.label().read()).toList())
                .containsExactly(
                        GENERAL_INFO,
                        SECOND_STEP_LABEL,
                        THIRD_STEP_LABEL,
                        FOURTH_STEP_LABEL
                );

        var stepElements = steps.stream()
                .map(step -> step.get(FindPolicy.VISIBLE).orElseThrow())
                .toList();
        assertThat(stepElements.stream()
                        .map(element -> element.getAttributes().get("role"))
                        .toList())
                .containsExactly("listitem", "listitem", "listitem", "listitem");
        assertThat(stepElements.stream()
                        .map(element -> element.getAttributes().get("data-testid"))
                        .toList())
                .containsExactly("step-0", "step-1", "step-2", "step-3");
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

    /**
     * Verifies the title, description, guidelines link, name and description fields, with their
     * labels and helper texts, of the information card.
     *
     * @param card the information card
     */
    private void assertInformationCard(InformationCard card) {
        card.sectionTitle().readAndAssert(CARD_INFO_TITLE);
        assertThat(card.description().read()).isEqualToNormalizingWhitespace(CARD_INFO_DESCRIPTION);
        assertThat(card.nameLabel().read()).contains(CARD_INFO_NAME_LABEL);
        assertThat(card.descriptionLabel().read()).contains(CARD_INFO_DESCRIPTION_LABEL);
        assertThat(card.guidelinesLink().get().isPresent()).as("The guidelines link must be present").isTrue();
        assertThat(card.nameInput().get().isPresent()).as("The name field must be present").isTrue();
        card.nameHelperText().readAndAssert(CARD_INFO_NAME_HELPER);
        assertThat(card.descriptionInput().get().isPresent()).as("The description field must be present").isTrue();
        card.descriptionHelperText().readAndAssert(CARD_INFO_DESC_HELPER);
    }

    /**
     * Verifies the title, warning alert and questions of the details card, then the label and
     * default selection of each radio option.
     *
     * @param card the details card
     */
    private void assertDetailsCard(DetailsCard card) {
        card.sectionTitle().readAndAssert(CARD_DETAILS_TITLE);
        card.warningAlert().readAndAssert(CARD_DETAILS_ALERT);
        assertThat(card.exchangeModeQuestion().read()).contains(CARD_DETAILS_EXCHANGE_MODE_QUESTION);
        assertThat(card.technologyQuestion().read()).contains(CARD_DETAILS_TECHNOLOGY_QUESTION);
        assertThat(card.dataFlowQuestion().read()).contains(CARD_DETAILS_DATA_FLOW_QUESTION);
        assertThat(card.personalDataQuestion().read()).contains(CARD_DETAILS_PERSONAL_DATA_QUESTION);

        card.syncRadio().label().readAndAssert(CARD_DETAILS_SYNC_LABEL);
        card.asyncRadio().label().readAndAssert(CARD_DETAILS_ASYNC_LABEL);
        card.restRadio().label().readAndAssert(CARD_DETAILS_REST_LABEL);
        card.soapRadio().label().readAndAssert(CARD_DETAILS_SOAP_LABEL);
        card.deliverRadio().label().readAndAssert(CARD_DETAILS_DELIVER_LABEL);
        card.receiveRadio().label().readAndAssert(CARD_DETAILS_RECEIVE_LABEL);
        card.personalDataYesRadio().label().readAndAssert(CARD_DETAILS_PERSONAL_DATA_YES_LABEL);
        card.personalDataNoRadio().label().readAndAssert(CARD_DETAILS_PERSONAL_DATA_NO_LABEL);
        assertThat(card.syncRadio().isSelected()).as("Synchronous exchange must be selected").isTrue();
        assertThat(card.asyncRadio().isSelected()).as("Asynchronous exchange must not be selected").isFalse();
        assertThat(card.restRadio().isSelected()).as("REST must be selected").isTrue();
        assertThat(card.soapRadio().isSelected()).as("SOAP must not be selected").isFalse();
        assertThat(card.deliverRadio().isSelected()).as("Deliver must be selected").isTrue();
        assertThat(card.receiveRadio().isSelected()).as("Receive must not be selected").isFalse();
        assertThat(card.personalDataYesRadio().isSelected()).as("Deliver personal data must not be selected").isFalse();
        assertThat(card.personalDataNoRadio().isSelected()).as("Do not deliver personal data must not be selected").isFalse();
    }

    /**
     * Verifies the title, description, section titles, switch and checkbox labels, documentation
     * links and the default checked state of the delegation controls.
     *
     * @param card the delegations card
     */
    private void assertDelegationsCard(DelegationsCard card) {
        card.sectionTitle().readAndAssert(CARD_DELEGATIONS_TITLE);
        assertThat(card.description().read()).isEqualToNormalizingWhitespace(CARD_DELEGATIONS_DESCRIPTION);
        card.consumerDelegableTitle().readAndAssert(CARD_DELEGATIONS_CONSUMER_DELEGABLE_TITLE);
        card.clientAccessDelegableTitle().readAndAssert(CARD_DELEGATIONS_CLIENT_ACCESS_DELEGABLE_TITLE);
        card.consumerDelegableLabel().readAndAssert(CARD_DELEGATIONS_CONSUMER_DELEGABLE_LABEL);
        assertThat(card.clientAccessDelegableLabel().read()).isEqualToNormalizingWhitespace(CARD_DELEGATIONS_CLIENT_ACCESS_DELEGABLE_LABEL);
        assertThat(card.documentationLink().get().isPresent()).as("The delegations documentation link must be present").isTrue();
        assertThat(card.consumerDelegableSwitch().isChecked()).as("The delegation consumption must be enabled").isTrue();
        assertThat(card.clientAccessDelegableCheckbox().isChecked()).as("The access to data by delegation must be authorized").isTrue();
        assertThat(card.clientAccessDocLink().get().isPresent()).as("The link to the access by delegation documentation must be present").isTrue();
    }

    /**
     * Verifies the title, descriptions, switch label and links of the Signal Hub card, and that
     * the Signal Hub switch is disabled by default.
     *
     * @param card the Signal Hub card
     */
    private void assertSignalHubCard(SignalHubCard card) {
        card.sectionTitle().readAndAssert(CARD_SIGNAL_HUB_TITLE);
        assertThat(card.description().read()).isEqualToNormalizingWhitespace(CARD_SIGNAL_HUB_DESCRIPTION);
        assertThat(card.designationDescription().read()).isEqualToNormalizingWhitespace(CARD_SIGNAL_HUB_DESIGNATION_DESCRIPTION);
        card.signalHubLabel().readAndAssert(CARD_SIGNAL_HUB_LABEL);
        assertThat(card.solutionLink().get().isPresent()).as("The link to the integrated solution must be present").isTrue();
        assertThat(card.designationPdfLink().get().isPresent()).as("The link to the designation document must be present").isTrue();
        assertThat(card.signalHubSwitch().isChecked()).as("Signal Hub must be disabled").isFalse();
    }

    /**
     * Verifies that the "Salva bozza e prosegui" button is enabled and shows the expected text.
     *
     * @param page the e-service creation page
     */
    private void assertSubmitAction(EServiceCreatePage page) {
        assertThat(page.saveDraftAndProceedButton().isDisabled()).isFalse();
        page.saveDraftAndProceedButton().readAndAssert(SUBMIT_BTN_TEXT);
    }

    /**
     * Verifies the links and copy shown in the "required attributes" section of the
     * e-service creation wizard (step "Soglie e attributi").
     * <p>
     * Each scenario logs in as a tenant admin of Comune di Milano, fills the general data step
     * with a random name and description (no personal data), saves the draft and then
     * asserts on the attributes step:
     * <ul>
     *   <li>the "Scopri di più sugli attributi" link points to the attributes technical reference;</li>
     *   <li>the certified attribute tab shows the expected description and its
     *       "Scopri di più su quali attributi usare" link points to the most used attributes guide.</li>
     * </ul>
     *
     *  Bug: https://pagopa.atlassian.net/browse/PIN-11075
     * @return a stream of dynamic tests, one for each checked link or description
     * @implNote Covers [PIN-10560].
     */
    @TestFactory
    Stream<DynamicTest> attributesSectionMustShowCorrectLinksAndCopy() {
        return webContractValidator
                .as(
                        User.getTenantAdmin(Tenant.COMUNE_DI_MILANO),
                        Tenant.COMUNE_DI_MILANO
                )
                .on(EServiceCreatePage.class)
                .tests(Stream.of(
                        new WebScenario<>(
                                "The attribute link 'Scopri di piu sugli attributi' must be correct",
                                creationPage -> creationPage.generalDataForm()
                                        .setEServiceName(RandomUtils.randomAlphanumericName("name"))
                                        .setEServiceDescription(RandomUtils.randomAlphanumericName("description"))
                                        .setUsePersonalData(false)
                                        .saveDraft(),
                                creationPage -> {
                                    var thresholdAndAttributeForm = creationPage.thresholdAndAttributeForm();
                                    thresholdAndAttributeForm.assertLoaded();

                                    String attributeHref = creationPage.thresholdAndAttributeForm()
                                            .requiredAttributeSection()
                                            .attributeLink()
                                            .getHref();

                                    Assertions.assertThat(attributeHref).as("The attribute link must be correct")
                                            .isEqualTo("https://developer.pagopa.it/pdnd-interoperabilita/guides/manuale-operativo-pdnd-interoperabilita/v1.0/riferimenti-tecnici/attributi");
                                }
                        ),
                        new WebScenario<>(
                                "The description of the certified attribute and the link 'Scopri di più su quali attributi usare.' must be corrected.",
                                creationPage -> creationPage.generalDataForm()
                                        .setEServiceName(RandomUtils.randomAlphanumericName("name"))
                                        .setEServiceDescription(RandomUtils.randomAlphanumericName("description"))
                                        .setUsePersonalData(false)
                                        .saveDraft(),
                                creationPage -> {
                                    var thresholdAndAttributeForm = creationPage.thresholdAndAttributeForm();
                                    thresholdAndAttributeForm.assertLoaded();

                                    String certifiedAttributeHref = creationPage.thresholdAndAttributeForm()
                                            .requiredAttributeSection()
                                            .certifiedAttributeTabPanel()
                                            .findOutMoreAboutAttributesToUseLink()
                                            .getHref();

                                    String certifiedAttributeDescription = creationPage.thresholdAndAttributeForm()
                                            .requiredAttributeSection()
                                            .certifiedAttributeTabPanel()
                                            .description()
                                            .read();

                                    Assertions.assertThat(certifiedAttributeHref).as("The attribute link must be correct")
                                            .isEqualTo("https://developer.pagopa.it/it/pdnd-interoperabilita/guides/manuale-operativo-pdnd-interoperabilita/v1.0/riferimenti-tecnici/attributi/gli-attributi-piu-utilizzati");

                                    Assertions.assertThat(certifiedAttributeDescription).as("The certified attribute description must be correct")
                                            .isEqualTo("Sono attributi riconosciuti da fonti certificate come IPA. Tra i più usati, per esempio, ci sono Comuni e loro Consorzi e Associazioni, Province e loro Consorzi e Associazioni, Gestori di Pubblici Servizi. Scopri di più su quali attributi usare.");
                                }
                        )
                ));
    }


}
