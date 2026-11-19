package it.pagopa.interop.suite.integration;

import it.frontend.e2e.framework.core.capability.core.Gettable;
import it.frontend.e2e.framework.web.binder.WebBinder;
import it.frontend.e2e.framework.web.model.WebPresentationElement;
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

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies rejection of an approval after another reviewer updates the draft,
 * without reloading the first reviewer's summary page.
 * The expected rejection and provisional error text still require live confirmation.
 */
@SpringBootTest(
        classes = {TestBootApp.class, JunitContextConfig.class, WebJUnitSuitConfig.class},
        properties = "spring.profiles.include=junit"
)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
@DisplayName("Risk Analysis Summary Web Integration Tests")
class WebIntegrationReviewerRiskAnalysisSummaryTest {

    private static final String ALTERNATIVE_ALERT = "L’analisi del rischio richiede l’approvazione di un solo valutatore, anche se è assegnata a più valutatori. Dopo l’approvazione, non sarà più modificabile e l’amministratore potrà pubblicare la finalità.";
    private static final String ALREADY_MODIFIED_ERROR = "Un altro valutatore ha modificato questa analisi del rischio. Ricarica la pagina per vedere la versione aggiornata e decidi se approvarla.";
    private static final String INSTITUTIONAL_PURPOSE = "institutionalPurpose";
    private static final String FIRST_REVIEWER_ANSWER = "Fine istituzionale modificato dal primo revisore";
    private static final String SECOND_REVIEWER_ANSWER = "Fine istituzionale modificato dal secondo revisore";
    private final PurposeUseCase purposeUseCase;
    private final InteropJourney interopJourney;
    private final Tenant consumer = Tenant.PAGO_PA;
    private final Tenant producer = Tenant.COMUNE_DI_MILANO;
    private final RiskAnalysisDataFactory riskAnalysisDataFactory;
    private final RiskAnalysisGateway riskAnalysisGateway;

    private final WebBrowserContractValidator webContractValidator;


    @TestFactory
    Stream<DynamicTest> riskAnalysisSummaryContractTests() {
        String eServiceName = RandomUtils.randomAlphanumericName("e-service_RA_to_compile");
        List<User> reviewers = User.getTenantUsers(consumer, UserRole.REVIEWER);
        assertThat(reviewers).as("The conflict requires two reviewers").hasSizeGreaterThanOrEqualTo(2);
        User firstReviewer = reviewers.get(0);
        User secondReviewer = reviewers.get(1);

        Purpose purpose = createToCompileRiskAnalysisWithReviewers(List.of(firstReviewer, secondReviewer), eServiceName);
        saveInitialDraft(purpose, firstReviewer);

        return webContractValidator
                .as(firstReviewer, consumer)
                .on(RiskAnalysisCompilationPage.class, purpose.getId().toString())
                .tests(Stream.of(
                        new WebScenario<>(
                                "Reject approval after another reviewer updates the saved draft",
                                page -> {
                                    page.institutionalPurposeCard().field().cleanAndWrite(FIRST_REVIEWER_ANSWER);
                                    page.saveDraftAndContinueButton().click();
                                },
                                ignored -> {
                                    var summaryPage = new WebBinder().bind(RiskAnalysisSummaryPage.class);
                                    summaryPage.assertLoaded();
                                    summaryPage.infoAlert().message().readAndAssert(ALTERNATIVE_ALERT);
                                    verifyConflictUpdate(summaryPage, purpose, firstReviewer, secondReviewer);
                                }
                        )
                ));
    }

    private void verifyConflictUpdate(
            RiskAnalysisSummaryPage page, Purpose purpose, User firstReviewer, User secondReviewer
    ) {
        RiskAnalysisForm savedDraft = PollingUtils.pollUntil(
                () -> purposeUseCase.getRiskAnalysisForm(purpose),
                form -> List.of(FIRST_REVIEWER_ANSWER).equals(form.getAnswers().get(INSTITUTIONAL_PURPOSE)),
                "The first reviewer's draft saved from the compilation page must be visible"
        );

        Map<String, List<String>> answers = new HashMap<>(savedDraft.getAnswers());
        answers.put(INSTITUTIONAL_PURPOSE, List.of(SECOND_REVIEWER_ANSWER));
        try {
            interopJourney.withConsumer(consumer, secondReviewer);
            purposeUseCase.saveRiskAnalysisForm(purpose, savedDraft.toBuilder().answers(answers).build());
        } finally {
            interopJourney.withConsumer(consumer, firstReviewer);
        }
        page.approveButton().click();
        page.approvalConfirmationDialog().confirmBtn().click();
        Optional<String> errorMessage = PollingUtils.pollUntil(
                () -> page.approvalErrorAlert().get(Gettable.FindPolicy.VISIBLE)
                        .map(WebPresentationElement::getText),
                message -> message.filter(text -> !text.isBlank()).isPresent(),
                Duration.ofSeconds(10),
                Duration.ofMillis(200),
                "A visible error alert must appear when another reviewer has modified the draft"
        );
        assertThat(errorMessage.orElseThrow())
                .as("Approval conflict error")
                .startsWith(ALREADY_MODIFIED_ERROR);

    }


    private Purpose createToCompileRiskAnalysisWithReviewers(List<User> assignedReviewers, String pendingEServiceName) {
        return interopJourney
                .withConsumer(consumer, UserRole.ADMIN)
                .setUpRiskAnalysisToCompile(
                        pendingEServiceName,
                        producer,
                        consumer,
                        assignedReviewers
                )
                .get(Purpose.class);


    }

    private void saveInitialDraft(Purpose purpose, User reviewer) {
        interopJourney.withConsumer(consumer, reviewer);
        RiskAnalysisForm form = RiskAnalysisForm.builder()
                .version(riskAnalysisGateway.getLatestRiskAnalysisConfig(consumer).getVersion())
                .answers(riskAnalysisDataFactory.getTemplateForTenant(consumer, true))
                .build();
        purposeUseCase.saveRiskAnalysisForm(purpose, form);
    }
}