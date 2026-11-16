package it.pagopa.interop.suite.contract;

import it.frontend.e2e.framework.core.capability.core.Gettable.FindPolicy;
import it.frontend.e2e.framework.core.capability.core.Readable;
import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.RadioButton;
import it.pagopa.infrastructure.suit.component.RadioGroup;
import it.pagopa.infrastructure.suit.component.Switch;
import it.pagopa.infrastructure.suit.component.TextField;
import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.common.purpose.domain.Purpose;
import it.pagopa.interop.web.infrastructure.config.WebJUnitSuitConfig;
import it.pagopa.interop.web.riskanalysis.infrastructure.suit.page.RiskAnalysisCompilationPage;
import it.pagopa.interop.web.riskanalysis.infrastructure.suit.page.RiskAnalysisCompilationPage.LabelledCheckbox;
import it.pagopa.utils.RandomUtils;
import it.pagopa.utils.async.PollingUtils;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.context.TestConstructor.AutowireMode.ALL;

/**
 * Live UI contract for compilation controls and conditional questionnaire branches.
 */
@SpringBootTest(
        classes = {TestBootApp.class, JunitContextConfig.class, WebJUnitSuitConfig.class},
        properties = "spring.profiles.include=junit"
)
@TestConstructor(autowireMode = ALL)
@RequiredArgsConstructor
@DisplayName("Risk Analysis Compilation Contract Tests")
class WebRiskAnalysisCompilationContractTest {

    private static final String INSTITUTIONAL_PURPOSE =
            "Per fini istituzionali che non richiedano prestazioni di elaborazioni aggiuntive";
    private static final String LEGAL_OBLIGATION = "Adempimento di un obbligo legale";
    private static final String LEGITIMATE_INTEREST = "Perseguimento del legittimo interesse";
    private static final String PUBLIC_INTEREST = "Esecuzione di un compito di interesse pubblico";
    private static final String MERE_CORRECTNESS =
            "È sufficiente che l’Erogatore verifichi la mera correttezza";
    private static final String NEW_PERSONAL_DATA = "È necessario ricevere ex novo";

    private final InteropJourney interopJourney;
    private final WebBrowserContractValidator webContractValidator;

    @TestFactory
    Stream<DynamicTest> verifyRiskAnalysisCompilationPageContent() {
        Tenant consumer = Tenant.PAGO_PA;
        User reviewer = User.getTenantUser(consumer, UserRole.REVIEWER);
        String purposeId = interopJourney.withConsumer(consumer, UserRole.ADMIN)
                .setUpRiskAnalysisToCompile(
                        RandomUtils.randomAlphanumericName("risk-analysis-compilation"),
                        Tenant.COMUNE_DI_MILANO, consumer, reviewer
                )
                .get(Purpose.class).getId().toString();

        return webContractValidator.as(reviewer, consumer)
                .on(RiskAnalysisCompilationPage.class, purposeId)
                .tests(Stream.of(
                        new WebScenario<>(
                                "Compilation layout, visible answers and conditional controls",
                                page -> {},
                                page -> {
                                    assertNavigationAndLayout(page, purposeId);
                                    assertPurposeQuestions(page);
                                    assertPersonalDataTypes(page);
                                    assertLegalBases(page);
                                    assertDataHandlingQuestions(page);
                                }
                        )
                ));
    }

    private static void assertNavigationAndLayout(RiskAnalysisCompilationPage page, String purposeId) {
        page.title().readAndAssert("Compilazione analisi del rischio");
        page.requiredFieldsNotice().readAndAssert("*Campi obbligatori");
        var introduction = page.introductionCard();
        introduction.title().readAndAssert("Analisi del rischio");
        introduction.description().readAndAssert(
                "Le domande del questionario varieranno in base alle risposte fornite man mano. "
                        + "Modificando la risposta a una domanda precedente, le successive domande potrebbero variare."
        );
        introduction.personalDataTreatmentLabel().readAndAssert("Trattamento dati personali");
        assertThat(introduction.personalDataTreatmentValue().read()).isNotBlank();
        assertThat(page.personalDataWarning().isWarning()).isTrue();
        page.personalDataWarning().message()
                .readAndAssert("Non inserire dati personali all'interno dei campi di testo libero");

        assertThat(page.breadcrumbs().items().stream().map(Readable::read).toList())
                .containsExactly("Analisi del rischio", "Informazioni finalità", "Compilazione analisi del rischio");
        assertThat(page.breadcrumbs().links()).hasSize(2);
        assertThat(page.breadcrumbs().links().get(0).getHref()).endsWith("/analisi-del-rischio");
        assertThat(page.breadcrumbs().links().get(1).getHref()).endsWith("/analisi-del-rischio/" + purposeId);
        page.backToRiskAnalysesLink().readAndAssert("Torna alle analisi del rischio");
        assertThat(page.backToRiskAnalysesLink().getHref()).endsWith("/analisi-del-rischio");
        assertThat(page.backToRiskAnalysesLink().get(FindPolicy.CLICKABLE)).isPresent();

        assertThat(page.backButton().get(FindPolicy.CLICKABLE)).isPresent();
        page.saveDraftAndContinueButton().readAndAssert("Salva bozza e prosegui");
        assertThat(page.saveDraftAndContinueButton().get(FindPolicy.CLICKABLE)).isPresent();
    }

    private static void assertPurposeQuestions(RiskAnalysisCompilationPage page) {
        // Purpose question and conditional text field
        var purpose = page.purposeCard();
        assertThat(purpose.question().read()).startsWith("Indicare per quale finalità");
        assertRadioOptions(purpose.answers(), INSTITUTIONAL_PURPOSE, "Altro");
        selectAndAssert(purpose.answers(), INSTITUTIONAL_PURPOSE);
        assertTextField(page.institutionalPurposeCard().field(), "Fine istituzionale");
        selectAndAssert(purpose.answers(), "Altro");
        assertHidden(page.institutionalPurposeCard().field());
        selectAndAssert(purpose.answers(), INSTITUTIONAL_PURPOSE);
        assertTextField(page.institutionalPurposeCard().field(), "Fine istituzionale");

    }

    private static void assertPersonalDataTypes(RiskAnalysisCompilationPage page) {
        assertYesNoOptions(page.usesPersonalDataCard().answers());
        selectAndAssert(page.usesPersonalDataCard().answers(), "Sì");

        List<LabelledCheckbox> personalDataTypes = page.personalDataTypesCard().answers();
        assertCheckboxLabels(personalDataTypes,
                "Dati personali comuni non identificativi",
                "Dati personali comuni identificativi",
                "Dati di minori ex art. 8 del GDPR",
                "Dati particolari ex art. 9 del GDPR",
                "Dati giudiziari ex art. 10 del GDPR",
                "Altro"
        );
        personalDataTypes.forEach(WebRiskAnalysisCompilationContractTest::assertCheckbox);
        LabelledCheckbox other = checkboxStartingWith(personalDataTypes, "Altro");
        other.check();
        assertTextField(page.otherPersonalDataTypesCard().field(), "Altre tipologie di dati personali");
        other.uncheck();
        assertHidden(page.otherPersonalDataTypesCard().field());
    }

    private static void assertLegalBases(RiskAnalysisCompilationPage page) {
        List<LabelledCheckbox> legalBases = page.legalBasesCard().answers();
        assertCheckboxLabels(legalBases,
                "Consenso dell’interessato",
                "Esecuzione di un contratto",
                LEGAL_OBLIGATION,
                "Salvaguardia degli interessi vitali",
                PUBLIC_INTEREST,
                LEGITIMATE_INTEREST
        );
        legalBases.forEach(WebRiskAnalysisCompilationContractTest::assertCheckbox);
        LabelledCheckbox legalObligation = checkboxStartingWith(legalBases, LEGAL_OBLIGATION);
        legalObligation.check();
        assertTextField(page.legalObligationReferenceCard().field(), "Riferimento all'obbligo legale");
        legalObligation.uncheck();
        assertHidden(page.legalObligationReferenceCard().field());

        LabelledCheckbox legitimateInterest = checkboxStartingWith(legalBases, LEGITIMATE_INTEREST);
        legitimateInterest.check();
        assertTextField(page.legitimateInterestReferenceCard().field(), "Riferimento al legittimo interesse");
        legitimateInterest.uncheck();
        assertHidden(page.legitimateInterestReferenceCard().field());

        LabelledCheckbox publicInterest = checkboxStartingWith(legalBases, PUBLIC_INTEREST);
        publicInterest.check();
        var publicInterestCard = page.publicInterestCard();
        assertRadioOptions(publicInterestCard.answers(), "Norma di legge", "Atto amministrativo generale", "Adempimento di un compito svolto nel pubblico interesse");
        selectAndAssert(publicInterestCard.answers(), "Norma di legge");
        assertTextField(page.ruleOfLawCard().field(), "Norma di legge");
        selectAndAssert(publicInterestCard.answers(), "Atto amministrativo generale");
        assertHidden(page.ruleOfLawCard().field());
        publicInterest.uncheck();
        PollingUtils.pollUntil(
                () -> publicInterestCard.exists(FindPolicy.VISIBLE),
                visible -> !visible,
                "Public-interest controls must disappear when the legal basis is deselected"
        );
    }

    private static void assertDataHandlingQuestions(RiskAnalysisCompilationPage page) {
        assertYesNoOptions(page.dataQuantityCard().answers());
        assertDeliveryMethodOptions(page);
        var policy = page.policyProvidedCard();
        var policyMedium = page.policyMediumCard();
        assertYesNoOptions(policy.answers());
        selectAndAssert(policy.answers(), "Sì");
        assertRadioOptions(policyMedium.answers(), "Cartacea", "Online");
        selectAndAssert(policyMedium.answers(), "Cartacea");
        selectAndAssert(policyMedium.answers(), "Online");
        selectAndAssert(policy.answers(), "No");
        PollingUtils.pollUntil(
                () -> policyMedium.exists(FindPolicy.VISIBLE),
                visible -> !visible,
                "Policy-medium controls must disappear when no policy is provided"
        );

        assertSwitch(page.integrityAndDiscretionCard().confirmation());
        assertYesNoOptions(page.dpiaCard().answers());
        assertYesNoOptions(page.dataDownloadCard().answers());
        var purposePursuit = page.purposePursuitCard();
        assertRadioOptions(purposePursuit.answers(), MERE_CORRECTNESS, NEW_PERSONAL_DATA);
        selectAndAssert(purposePursuit.answers(), NEW_PERSONAL_DATA);
        selectAndAssert(purposePursuit.answers(), MERE_CORRECTNESS);
        assertSwitch(page.catalogueCheckCard().confirmation());
        assertSwitch(page.gdprDeclarationCard().confirmation());

        selectAndAssert(page.usesPersonalDataCard().answers(), "No");
        PollingUtils.pollUntil(
                () -> page.dataQuantityCard().exists(FindPolicy.VISIBLE),
                visible -> !visible,
                "Personal-data follow-up questions must disappear when no personal data is accessed"
        );
    }

    private static LabelledCheckbox checkboxStartingWith(List<LabelledCheckbox> checkboxes, String label) {
        List<LabelledCheckbox> matches = checkboxes.stream()
                .filter(checkbox -> checkbox.description().read().startsWith(label))
                .toList();
        assertThat(matches).as("Unique checkbox labelled '%s'", label).hasSize(1);
        return matches.get(0);
    }

    private static void assertCheckboxLabels(List<LabelledCheckbox> checkboxes, String... labels) {
        assertThat(checkboxes).hasSize(labels.length);
        for (String label : labels) {
            checkboxStartingWith(checkboxes, label);
        }
    }

    private static void assertDeliveryMethodOptions(RiskAnalysisCompilationPage page) {
        var deliveryMethod = page.deliveryMethodCard();
        deliveryMethod.select().click();
        List<String> options = deliveryMethod.options().stream().map(Button::read).toList();
        assertThat(options)
                .isNotEmpty()
                .doesNotHaveDuplicates()
                .contains("In chiaro", "In forma pseudoanonimizzata", "In forma aggregata", "In forma anonimizzata");
        options.forEach(label -> assertThat(label).isNotBlank());
        selectDeliveryMethodOption(page, options.get(0));

        for (String label : options) {
            deliveryMethod.select().click();
            selectDeliveryMethodOption(page, label);
            deliveryMethod.select().readAndAssert(label);
        }
    }

    private static void selectDeliveryMethodOption(RiskAnalysisCompilationPage page, String label) {
        page.deliveryMethodCard().options().stream()
                .filter(option -> label.equals(option.read()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Delivery-method option not found: " + label))
                .click();
    }

    private static void assertTextField(TextField field, String description) {
        var element = field.get(FindPolicy.CLICKABLE).orElseThrow(
                () -> new AssertionError("Text field not visible and enabled: " + description)
        );
        assertThat(element.getAttributes().get("maxlength")).isEqualTo("2000");
        String value = "Verifica contratto: " + description;
        field.fill(value);
        field.readAndAssert(value);
    }

    private static void assertHidden(TextField field) {
        PollingUtils.pollUntil(
                () -> field.exists(FindPolicy.VISIBLE),
                visible -> !visible,
                "Conditional text field must disappear when its enabling answer is deselected"
        );
    }

    private static void assertYesNoOptions(RadioGroup group) {
        assertRadioOptions(group, "Sì", "No");
        String initialAnswer = group.getSelected();
        selectAndAssert(group, "Sì");
        selectAndAssert(group, "No");
        if (initialAnswer != null) {
            selectAndAssert(group, initialAnswer);
        }
    }

    private static void assertCheckbox(LabelledCheckbox checkbox) {
        assertThat(checkbox.isDisabled()).isFalse();
        assertThat(checkbox.description().read()).isNotBlank();
        boolean initialValue = checkbox.isChecked();
        checkbox.check();
        assertThat(checkbox.isChecked()).as("Checked answer '%s'", checkbox.description().read()).isTrue();
        checkbox.uncheck();
        assertThat(checkbox.isChecked()).as("Unchecked answer '%s'", checkbox.description().read()).isFalse();
        checkbox.setChecked(initialValue);
        assertThat(checkbox.isChecked()).isEqualTo(initialValue);
    }

    private static void assertRadioOptions(RadioGroup group, String... labelPrefixes) {
        List<RadioButton> answers = group.radioButtons();
        assertThat(answers).hasSize(labelPrefixes.length);
        for (int index = 0; index < labelPrefixes.length; index++) {
            assertThat(answers.get(index).getLabel()).startsWith(labelPrefixes[index]);
        }
        answers.forEach(answer -> assertThat(answer.isDisabled()).isFalse());
    }

    private static void selectAndAssert(RadioGroup group, String labelPrefix) {
        List<RadioButton> matches = group.radioButtons().stream()
                .filter(answer -> answer.getLabel().startsWith(labelPrefix))
                .toList();
        assertThat(matches).as("Unique radio answer starting with '%s'", labelPrefix).hasSize(1);
        String label = matches.get(0).getLabel();
        group.select(label);
        assertThat(group.getSelected()).isEqualTo(label);
    }

    private static void assertSwitch(Switch control) {
        assertThat(control.get(FindPolicy.VISIBLE)).isPresent();
        assertThat(control.isDisabled()).isFalse();
        boolean initialState = control.isChecked();
        control.setChecked(!initialState);
        assertThat(control.isChecked()).isEqualTo(!initialState);
        control.setChecked(initialState);
        assertThat(control.isChecked()).isEqualTo(initialState);
    }
}
