package it.pagopa.interop.suite.contract;

import it.frontend.e2e.framework.core.capability.core.Gettable;
import it.frontend.e2e.framework.web.binder.WebBinder;
import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.common.purpose.application.PurposeUseCase;
import it.pagopa.interop.common.purpose.domain.Purpose;
import it.pagopa.interop.common.risk_analysis.application.RiskAnalysisDataFactory;
import it.pagopa.interop.common.risk_analysis.application.RiskAnalysisGateway;
import it.pagopa.interop.common.risk_analysis.domain.RiskAnalysisForm;
import it.pagopa.interop.web.infrastructure.config.WebJUnitSuitConfig;
import it.pagopa.interop.web.reviewer_riskanalysis.infrastructure.component.RiskAnalysisCompleteCard;
import it.pagopa.interop.web.reviewer_riskanalysis.infrastructure.component.RiskAnalysisGeneralInfoCard;
import it.pagopa.interop.web.reviewer_riskanalysis.infrastructure.suit.page.RiskAnalysisCompilationPage;
import it.pagopa.interop.web.reviewer_riskanalysis.infrastructure.suit.page.RiskAnalysisSummaryPage;
import it.pagopa.utils.RandomUtils;
import it.pagopa.utils.async.PollingUtils;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.context.TestConstructor.AutowireMode.ALL;

@SpringBootTest(
        classes = {
                TestBootApp.class,
                JunitContextConfig.class,
                WebJUnitSuitConfig.class
        },
        properties = "spring.profiles.include=junit"
)
@TestConstructor(autowireMode = ALL)
@RequiredArgsConstructor
@DisplayName("UI Contract Test - Risk Analysis Summary Page")
class WebReviewerRiskAnalysisSummaryContractTest {

    private static final String PAGE_TITLE = "Riepilogo";
    private static final String INSTITUTIONAL_PURPOSE = "Fine istituzionale compilato correttamente";
    private static final String ALERT_INFO_TEXT = "Quando approvi l’analisi del rischio, non potrai più modificarla e l’amministratore potrà pubblicare la finalità.";

    private final PurposeUseCase purposeUseCase;
    private final RiskAnalysisDataFactory riskAnalysisDataFactory;
    private final RiskAnalysisGateway riskAnalysisGateway;
    private final InteropJourney interopJourney;
    private final Tenant consumer = Tenant.PAGO_PA;
    private final Tenant producer = Tenant.COMUNE_DI_MILANO;

    private final WebBrowserContractValidator webContractValidator;

    private static final String INFO_PURPOSE_NAME = "Nome della finalità";
    private static final String INFO_PROVIDER = "Erogatore";
    private static final String INFO_ESTIMATES_TITLE = "Stima e soglie di chiamate API";
    private static final String INFO_ASSIGNMENT_TITLE = "Assegnazione";
    private static final String INFO_ASSIGNMENT_DATE = "Data assegnazione";
    private static final String INFO_ASSIGNED_TO = "Assegnata a";

    private static final String RISK_TITLE = "Analisi del rischio";
    private static final String Q_PURPOSE = "Indicare per quale finalità si intende accedere ai dati messi a disposizione con la fruizione del presente E-service";
    private static final String Q_SPECIFIC_PURPOSE = "Specificare il fine perseguito per fini istituzionali che non richiedano prestazioni di elaborazioni aggiuntive";
    private static final String Q_PERSONAL_DATA = "Indicare se si accede a dati personali";
    private static final String Q_THIRD_PARTY_DATA = "Indicare se si accede a dati non personali di terzi (quindi a dati non personali che non sono riferibili al Fruitore stesso)";


    @TestFactory
    Stream<DynamicTest> testVerifyRiskAnalysisSummaryPageContent() {
        String eServiceName = RandomUtils.randomAlphanumericName("e-service_RA_to_compile");
        List<User> reviewers = User.getTenantUsers(consumer, UserRole.REVIEWER);

        Purpose purpose = createToCompileRiskAnalysisWithReviewer(reviewers.get(0), eServiceName);
        User reviewer = reviewers.get(0);
        saveInitialDraft(purpose, reviewer);

        return webContractValidator
                .as(reviewer, consumer)
                .on(RiskAnalysisCompilationPage.class, purpose.getId().toString())
                .tests(Stream.of(
                        new WebScenario<>(
                                "Save draft and continue to the Risk Analysis Summary Page",
                                page -> page.saveDraftAndContinueButton().click(),
                                ignored -> {
                                    var summaryPage = new WebBinder().bind(RiskAnalysisSummaryPage.class);
                                    summaryPage.assertLoaded();
                                    assertPageData(summaryPage);
                                }
                        )
                ));
    }

    private void saveInitialDraft(Purpose purpose, User reviewer) {
        interopJourney.withConsumer(consumer, reviewer);
        Map<String, List<String>> answers = new HashMap<>(riskAnalysisDataFactory.getTemplateForTenant(consumer, true));
        answers.put("institutionalPurpose", List.of(INSTITUTIONAL_PURPOSE));
        RiskAnalysisForm form = RiskAnalysisForm.builder()
                .version(riskAnalysisGateway.getLatestRiskAnalysisConfig(consumer).getVersion())
                .answers(answers)
                .build();
        purposeUseCase.saveRiskAnalysisForm(purpose, form);
    }

    private void assertPageData(RiskAnalysisSummaryPage page) {
        assertHeaderAndActions(page);
        assertGeneralInfoCard(page.generalInfoCard());
        assertRiskAnalysisCard(page.riskAnalysisCard());
    }

    private void assertHeaderAndActions(RiskAnalysisSummaryPage page) {
        assertThat(page.breadcrumbs().get().isPresent())
                .as("Breadcrumbs must be present on the page")
                .isTrue();

        assertThat(page.backButton().isDisabled())
                .as("Back button must be enabled")
                .isFalse();

        page.pageTitle().readAndAssert(PAGE_TITLE);

        page.infoAlert().message().readAndAssert(ALERT_INFO_TEXT);

        assertThat(page.editDraftButton().isDisabled())
                .as("Edit draft button must be enabled")
                .isFalse();

        assertThat(page.approveButton().isDisabled())
                .as("Approve risk analysis button must be enabled")
                .isFalse();
    }

    private void assertGeneralInfoCard(RiskAnalysisGeneralInfoCard card) {
        card.accordionTitle().readAndAssert("Informazioni generali");

        assertThat(card.get().isPresent()).as("Card Informazioni Generali not found").isTrue();

        card.purposeNameLabel().readAndAssert(INFO_PURPOSE_NAME);
        assertNotBlank(card.purposeNameValue().read(), INFO_PURPOSE_NAME);

        card.descriptionLabel().readAndAssert("Descrizione");
        assertNotBlank(card.descriptionValue().read(), "Descrizione");

        card.eServiceLabel().readAndAssert("E-service associato");
        assertNotBlank(card.eServiceLink().read(), "Nome e-service");
        assertNotBlank(card.eServiceLink().getHref(), "Href e-service");
        assertThat(card.eServiceLink().get(Gettable.FindPolicy.CLICKABLE))
                .as("E-service link must be visible and clickable")
                .isPresent();

        card.providerLabel().readAndAssert(INFO_PROVIDER);
        assertNotBlank(card.providerValue().read(), INFO_PROVIDER);

        card.thresholdSubtitle().readAndAssert(INFO_ESTIMATES_TITLE);

        card.estimatedRequestLabel().readAndAssert("Stima richiesta dal fruitore");
        assertNotBlank(card.estimatedRequestValue().read(), "Stima richiesta dal fruitore");

        card.thresholdLabel().readAndAssert("Soglia per fruitore prevista dall'erogatore");
        assertNotBlank(card.thresholdValue().read(), "Soglia per fruitore");

        card.totalThresholdLabel().readAndAssert("Soglia totale prevista dall'erogatore");
        assertNotBlank(card.totalThresholdValue().read(), "Soglia totale");

        card.assignmentSubtitle().readAndAssert(INFO_ASSIGNMENT_TITLE);
        card.assignmentDateLabel().readAndAssert(INFO_ASSIGNMENT_DATE);
        assertNotBlank(card.assignmentDateValue().read(), INFO_ASSIGNMENT_DATE);

        card.assignedToLabel().readAndAssert(INFO_ASSIGNED_TO);
        assertNotBlank(card.assignedToValue().read(), INFO_ASSIGNED_TO);
    }

    private void assertRiskAnalysisCard(RiskAnalysisCompleteCard card) {
        if (card.header().get(Gettable.FindPolicy.PRESENT).isPresent()) {
            card.header().click();
            PollingUtils.pollUntil(
                    () -> RISK_TITLE.equals(card.title().read()),
                    Boolean.TRUE::equals,
                    "La card Analisi del rischio non si è aperta"
            );
        }

        card.accordionTitle().readAndAssert(RISK_TITLE);

        assertThat(card.get().isPresent()).as("Card Analisi del Rischio not found").isTrue();
        card.title().readAndAssert(RISK_TITLE);

        card.purposeQuestionLabel().readAndAssert(Q_PURPOSE);
        assertNotBlank(card.purposeAnswerLabel().read(), "Purpose Answer");

        card.specificPurposeQuestionLabel().readAndAssert(Q_SPECIFIC_PURPOSE);
        card.specificPurposeAnswerLabel().readAndAssert(INSTITUTIONAL_PURPOSE);
        assertNotBlank(card.purposeNoteLabel().read(), "Purpose Note");

        card.personalDataQuestionLabel().readAndAssert(Q_PERSONAL_DATA);
        assertNotBlank(card.personalDataAnswerLabel().read(), "Personal Data Answer");
        assertNotBlank(card.personalDataNoteLabel().read(), "Personal Data Note");

        card.thirdPartyDataQuestionLabel().readAndAssert(Q_THIRD_PARTY_DATA);
        assertNotBlank(card.thirdPartyDataAnswerLabel().read(), "Third Party Data Answer");
    }

    private static void assertNotBlank(String value, String description) {
        assertThat(value).as("%s deve essere valorizzato", description).isNotBlank();
    }

    private Purpose createToCompileRiskAnalysisWithReviewer(User assignedReviewer, String pendingEServiceName) {

        return interopJourney
                .withConsumer(consumer, UserRole.ADMIN)
                .setUpRiskAnalysisToCompile(
                        pendingEServiceName,
                        producer,
                        consumer,
                        List.of(assignedReviewer)
                )
                .get(Purpose.class);


    }
}