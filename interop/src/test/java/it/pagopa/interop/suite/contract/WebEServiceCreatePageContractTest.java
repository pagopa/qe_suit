package it.pagopa.interop.suite.contract;

import it.frontend.e2e.framework.web.adapter.model.FindPolicy;
import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.infrastructure.suit.component.RadioButton;
import it.pagopa.infrastructure.suit.component.RadioGroup;
import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.web.eservice.infrastructure.suit.component.general_data_form.DelegationsSection;
import it.pagopa.interop.web.eservice.infrastructure.suit.component.general_data_form.EServiceDetailsSection;
import it.pagopa.interop.web.eservice.infrastructure.suit.component.general_data_form.AboutEServiceSection;
import it.pagopa.interop.web.eservice.infrastructure.suit.component.general_data_form.SignalHubSection;
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

import java.util.List;
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
@Tag("wait_for_qa")
public class WebEServiceCreatePageContractTest {

    private final WebBrowserContractValidator webContractValidator;

    private static final String PAGE_TITLE = "Crea e-service";
    private static final String SUBMIT_BTN_TEXT = "Salva bozza e prosegui";
    private static final List<String> EXPECTED_BREADCRUMB_LABELS = List.of(
            "Erogazione", "I miei e-service", "Crea e-service"
    );
    private static final List<String> EXPECTED_STEP_LABELS = List.of(
            "Informazioni generali", "Soglie e attributi", "Specifiche tecniche", "Informazioni aggiuntive"
    );
    private static final String ABOUT_ESERVICE_SECTION_TITLE = "Informazioni sull’e-service";
    private static final String ESERVICE_NAME_LABEL = "Nome dell’e-service";
    private static final String ESERVICE_DESCRIPTION_LABEL = "Descrizione dell’e-service";

    private static final String ESERVICE_DETAILS_SECTION_TITLE = "Dettagli dell'e-service";
    private static final String EXCHANGE_MODE_QUESTION = "In che modo vuoi scambiare i dati dell’e-service?";
    private static final String TECHNOLOGY_QUESTION = "Con quale tecnologia è costruita la tua API?";
    private static final String DATA_FLOW_QUESTION = "L’e-service eroga o riceve dati?";
    private static final String PERSONAL_DATA_QUESTION = "L’e-service eroga dati personali?";
    private static final String ESERVICE_DETAILS_SECTION_SYNC_LABEL = "Sincrono (standard)";
    private static final String ESERVICE_DETAILS_SECTION_REST_LABEL = "REST";
    private static final String ESERVICE_DETAILS_SECTION_DELIVER_LABEL = "Eroga";

    private static final String DELEGATIONS_SECTION_TITLE = "Deleghe";
    private static final String CONSUMER_DELEGABLE_TITLE = "L’e-service può essere fruito tramite delega?";
    private static final String CLIENT_ACCESS_DELEGABLE_TITLE = "Autorizzo l’accesso ai dati in delega";

    private static final String SIGNAL_HUB_SECTION_TITLE = "Signal Hub: servizio di notifica della variazione dei dati";
    private static final String SIGNAL_HUB_LABEL = "Notificherò su Signal Hub le variazioni dei dati";

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
                                    var form = page.generalDataForm();
                                    assertAboutEServiceSection(form.aboutEServiceSection());
                                    assertEServiceDetailsSection(form.eServiceDetailsSection());
                                    assertDelegationsSection(form.delegationsSection());
                                    assertSignalHubSection(form.signalHubSection());
                                    assertSubmitAction(page);
                                }
                        )
                ));
    }

    /**
     * Verifies breadcrumbs, page title and stepper.
     * @param page the e-service creation page
     */
    private void assertHeaderAndStepper(EServiceCreatePage page) {
        assertThat(page.breadcrumbs().items().stream().map(item -> item.read()).toList())
                .as("E-service creation breadcrumb labels in order")
                .containsExactlyElementsOf(EXPECTED_BREADCRUMB_LABELS);
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
        var visibleSteps = steps.stream()
                .map(step -> step.get(FindPolicy.VISIBLE))
                .toList();
        assertThat(visibleSteps)
                .as("All four steps must be visible")
                .allSatisfy(element -> assertThat(element).isPresent());
        var stepElements = visibleSteps.stream()
                .map(element -> element.orElseThrow())
                .toList();
        assertThat(steps.stream().map(step -> step.number().get(FindPolicy.VISIBLE).isPresent()).toList())
                .as("All four step numbers must be visible")
                .containsExactly(true, true, true, true);
        assertThat(steps.stream().map(step -> step.label().get(FindPolicy.VISIBLE).isPresent()).toList())
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

    /**
     * Verifies the title, description, guidelines link, name and description fields, with their
     * labels and helper texts, of the about e-service section.
     *
     * @param section the about e-service section
     */
    private void assertAboutEServiceSection(AboutEServiceSection section) {
        assertThat(section.title().read()).isEqualTo(ABOUT_ESERVICE_SECTION_TITLE);
        assertThat(section.description().read()).isEqualToNormalizingWhitespace(
                "Per scegliere nomi chiari e rendere gli e-service facili da trovare, consulta le linee guida");
        assertThat(section.nameLabel().read()).contains(ESERVICE_NAME_LABEL);
        assertThat(section.descriptionLabel().read()).contains(ESERVICE_DESCRIPTION_LABEL);
        assertThat(section.guidelinesLink().get(FindPolicy.PRESENT)).as("The guidelines link must be present").isPresent();
        assertThat(section.eServiceNameInput().get(FindPolicy.PRESENT)).as("The name field must be present").isPresent();
        section.nameHelperText().readAndAssert("Min 5 caratteri, max 60 caratteri");
        assertThat(section.eServiceDescriptionInput().get(FindPolicy.PRESENT)).as("The description field must be present").isPresent();
        section.descriptionHelperText().readAndAssert(
                "Descrivi quali dati il fruitore deve fornire e quali dati l’e-service restituisce. Min 10 caratteri, max 400 caratteri");
    }

    /**
     * Verifies the title, warning alert and questions of the details section, then the label and
     * default selection of each radio option.
     *
     * @param section the details section
     */
    private void assertEServiceDetailsSection(EServiceDetailsSection section) {
        assertThat(section.title().read()).isEqualTo(ESERVICE_DETAILS_SECTION_TITLE);
        assertThat(section.generalAlert().isWarning()).as("The general alert must be a warning").isTrue();
        assertThat(section.generalAlert().message().read()).isEqualTo(
                "Questi dati non saranno più modificabili dopo la pubblicazione della prima versione dell’e-service.");
        assertThat(section.exchangeModeQuestion().read()).contains(EXCHANGE_MODE_QUESTION);
        assertThat(section.technologyQuestion().read()).contains(TECHNOLOGY_QUESTION);
        assertThat(section.dataFlowQuestion().read()).contains(DATA_FLOW_QUESTION);
        assertThat(section.personalDataQuestion().read()).contains(PERSONAL_DATA_QUESTION);

        assertRadioGroup("Data exchange", section.asyncExchange(),
                List.of(ESERVICE_DETAILS_SECTION_SYNC_LABEL, "Asincrono / massivo (in differita)"),
                ESERVICE_DETAILS_SECTION_SYNC_LABEL);
        assertRadioGroup("API technology", section.technology(),
                List.of(ESERVICE_DETAILS_SECTION_REST_LABEL, "SOAP"),
                ESERVICE_DETAILS_SECTION_REST_LABEL);
        assertRadioGroup("Data flow", section.mode(),
                List.of(ESERVICE_DETAILS_SECTION_DELIVER_LABEL, "Riceve"),
                ESERVICE_DETAILS_SECTION_DELIVER_LABEL);
        assertRadioGroup("Personal data", section.personalData(),
                List.of("Eroga dati personali", "Non eroga dati personali"));
    }

    private void assertRadioGroup(String name, RadioGroup group, List<String> expectedLabels, String... expectedSelectedLabels) {
        var buttons = group.radioButtons();
        assertThat(buttons).as("E-service creation: %s radio group options", name)
                .extracting(RadioButton::getLabel)
                .containsExactlyInAnyOrderElementsOf(expectedLabels);
        assertThat(buttons.stream()
                .filter(RadioButton::isSelected)
                .map(RadioButton::getLabel)
                .toList())
                .as("E-service creation: %s initially selected radio group options", name)
                .containsExactly(expectedSelectedLabels);
    }

    /**
     * Verifies the title, description, section titles, switch and checkbox labels, documentation
     * links and the default checked state of the delegation controls.
     *
     * @param section the delegations section
     */
    private void assertDelegationsSection(DelegationsSection section) {
        assertThat(section.title().read()).isEqualTo(DELEGATIONS_SECTION_TITLE);
        assertThat(section.description().read()).isEqualToNormalizingWhitespace(
                "Consulta la documentazione relativa al funzionamento delle deleghe");
        section.consumerDelegableTitle().readAndAssert(CONSUMER_DELEGABLE_TITLE);
        section.clientAccessDelegableTitle().readAndAssert(CLIENT_ACCESS_DELEGABLE_TITLE);
        section.consumerDelegableLabel().readAndAssert(
                "Autorizzo che i fruitori dell’e-service possano delegare altri enti ad iscriversi per conto proprio");
        assertThat(section.clientAccessDelegableLabel().read()).isEqualToNormalizingWhitespace(
                "Autorizzo che i delegati possano associare i propri client alle finalità che hanno creato in qualità di delegati alla fruizione. Consulta la documentazione per approfondire");
        assertThat(section.documentationLink().get(FindPolicy.PRESENT)).as("The delegations documentation link must be present").isPresent();
        assertThat(section.consumerDelegableSwitch().isChecked()).as("The delegation consumption must be enabled").isTrue();
        assertThat(section.clientAccessDelegableCheckbox().isChecked()).as("The access to data by delegation must be authorized").isTrue();
        assertThat(section.clientAccessDocLink().get(FindPolicy.PRESENT)).as("The link to the access by delegation documentation must be present").isPresent();
    }

    /**
     * Verifies the title, descriptions, switch label and links of the Signal Hub section, and that
     * the Signal Hub switch is disabled by default.
     *
     * @param section the Signal Hub section
     */
    private void assertSignalHubSection(SignalHubSection section) {
        assertThat(section.title().read()).isEqualTo(SIGNAL_HUB_SECTION_TITLE);
        assertThat(section.description().read()).isEqualToNormalizingWhitespace(
                "Signal Hub è una soluzione integrata che informa i fruitori quando i dati dell'e-service vengono modificati.");
        assertThat(section.designationDescription().read()).isEqualToNormalizingWhitespace(
                "Attivando la funzionalità designi PagoPa S.p.A. responsabile del trattamento dei dati facendo proprio il documento di designazione di PagoPa S.p.A. come responsabile del trattamento dei dati personali per \"Signal Hub\".");
        section.signalHubLabel().readAndAssert(SIGNAL_HUB_LABEL);
        assertThat(section.solutionLink().get(FindPolicy.PRESENT)).as("The link to the integrated solution must be present").isPresent();
        assertThat(section.designationPdfLink().get(FindPolicy.PRESENT)).as("The link to the designation document must be present").isPresent();
        assertThat(section.signalHubSwitch().isChecked()).as("Signal Hub must be disabled").isFalse();
    }

    /**
     * Verifies that the "Salva bozza e prosegui" button is enabled and shows the expected text.
     *
     * @param page the e-service creation page
     */
    private void assertSubmitAction(EServiceCreatePage page) {
        var button = page.generalDataForm().saveDraftButton();
        assertThat(button.isDisabled()).isFalse();
        button.readAndAssert(SUBMIT_BTN_TEXT);
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

                                    Assertions.assertThat(certifiedAttributeHref).as("The attribute link must be correct")//
                                    .isEqualTo("https://developer.pagopa.it/it/pdnd-interoperabilita/guides/manuale-operativo-pdnd-interoperabilita/v1.0/riferimenti-tecnici/attributi/gli-attributi-piu-utilizzati");
                                    Assertions.assertThat(certifiedAttributeDescription).as("The certified attribute description must be correct")
                                            .isEqualTo("Sono attributi riconosciuti da fonti certificate come IPA. Tra i più usati, per esempio, ci sono Comuni e loro Consorzi e Associazioni, Province e loro Consorzi e Associazioni, Gestori di Pubblici Servizi. Scopri di più su quali attributi usare.");
                                }
                        )
                ));
    }


}
