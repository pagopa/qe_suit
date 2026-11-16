package it.pagopa.interop.suite.integration;

import it.frontend.e2e.framework.core.capability.core.Gettable.FindPolicy;
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
import it.pagopa.interop.common.risk_analysis.domain.RiskAnalysisReviewMode;
import it.pagopa.interop.web.infrastructure.config.WebJUnitSuitConfig;
import it.pagopa.interop.web.riskanalysis.infrastructure.suit.page.RiskAnalysisApprovalPage;
import it.pagopa.utils.RandomUtils;
import it.pagopa.utils.async.PollingUtils;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies the RA-Approval page access-control behavior and its integration with the backend services.
 */
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
@DisplayName("Risk Analysis Approval Web Integration Tests")
public class WebIntegrationRiskAnalysisApprovalTest {

    private final WebBrowserContractValidator webContractValidator;
    private final InteropJourney interopJourney;
    private final Tenant consumer = Tenant.PAGO_PA;
    private final Tenant producer = Tenant.COMUNE_DI_MILANO;
    private final PurposeUseCase purposeUseCase;

    private static final String UNAUTHORIZED_TITLE = "Non autorizzato";
    private static final String UNAUTHORIZED_SUBTITLE = "Non possiedi le autorizzazioni necessarie per visualizzare la pagina.";
    private static final String ALTERNATIVE_ALERT = "L’analisi del rischio richiede l’approvazione di un solo valutatore, anche se è assegnata a più valutatori. Dopo l’approvazione, non sarà più modificabile e l’amministratore potrà pubblicare la finalità.";
    private static final String ALREADY_APPROVED_ERROR = "Questa analisi del rischio è già stata approvata da un altro valutatore. Ricarica la pagina per vedere l’analisi del rischio approvata.";

    @TestFactory
    @DisplayName("Reviewer views RA in waiting for approval")
    Stream<DynamicTest> authorizedUserTests() {
        String threeRevEserviceName = RandomUtils.randomAlphanumericName("risk-analysis-info");

        List<User> reviewers = User.getTenantUsers(consumer, UserRole.REVIEWER);
        assertThat(reviewers)
                .as("At least three reviewers must be configured for the consumer")
                .hasSizeGreaterThanOrEqualTo(3);

        List<User> assignedReviewers = reviewers.subList(0, 3);
        Purpose purpose = createToApproveRiskAnalysisWithReviewers(
                assignedReviewers,
                threeRevEserviceName
        );

        String riskAnalysisId = purpose.getId().toString();
        String expectedAssignedTo = assignedReviewers.stream()
                .map(this::reviewerDisplayName)
                .collect(Collectors.joining(", "));

        List<User> remainingReviewers = assignedReviewers.subList(0, assignedReviewers.size() - 2);
        String resizedAssignedTo = remainingReviewers.stream()
                .map(this::reviewerDisplayName)
                .collect(Collectors.joining(", "));
        LocalDate expectedDate = LocalDate.now(ZoneId.of("Europe/Rome"));

        return webContractValidator.as(reviewers.get(0), consumer)
                .on(RiskAnalysisApprovalPage.class, riskAnalysisId)
                .tests(Stream.of(
                        new WebScenario<>("Verify RA approval page content for one of the assigned reviewer",
                                page -> {
                                },
                                page -> {
                                    verifyAssignment(page, expectedDate, expectedAssignedTo);
                                    assertThat(page.alertMessage().read())
                                            .as("The alert message must match the expected alert")
                                            .isEqualTo(ALTERNATIVE_ALERT);

                                    updateRiskAnalysisDraft(remainingReviewers, purpose);
                                    page.navigateTo(riskAnalysisId);
                                    page.assertLoaded();
                                    verifyAssignment(page, expectedDate, resizedAssignedTo);

                                    updateRiskAnalysisDraft(assignedReviewers, purpose);
                                    page.navigateTo(riskAnalysisId);
                                    page.assertLoaded();
                                    verifyAssignment(page, expectedDate, expectedAssignedTo);

                                    verifyApprovalConflict(page, purpose, assignedReviewers.get(0), assignedReviewers.get(1));
                                })
                ));
    }

    /**
     * Checks that the producer's administrator sees the unauthorized view
     * instead of the reviewer workspace.
     *
     * @return dynamic integration tests for unauthorized access
     */
    @TestFactory
    @DisplayName("Unauthorized user sees the error page")
    Stream<DynamicTest> unauthorizedUserTests() {
        String eServiceName = RandomUtils.randomAlphanumericName("risk-analysis-info-unauthorized");
        User reviewer = User.getTenantUser(consumer, UserRole.REVIEWER);

        Purpose purpose = createToApproveRiskAnalysisWithReviewers(
                List.of(reviewer),
                eServiceName
        );

        return webContractValidator.as(User.getTenantAdmin(producer), producer)
                .on(RiskAnalysisApprovalPage.class, purpose.getId().toString())
                .tests(Stream.of(
                        new WebScenario<>(
                                "An ADMIN user visualizes 'Unauthorized'",
                                page -> {},
                                page -> {
                                    page.unauthorizedTitle().readAndAssert(UNAUTHORIZED_TITLE);
                                    page.unauthorizedSubtitle().readAndAssert(UNAUTHORIZED_SUBTITLE);
                                    assertThat(page.backToHomeBtn().isDisabled())
                                            .as("The 'Torna alla home' button must be enabled")
                                            .isFalse();
                                }
                        )
                ));
    }

    private void updateRiskAnalysisDraft(List<User> updatedReviewers, Purpose purpose) {
        try {
            interopJourney.withConsumer(consumer, UserRole.ADMIN);
            purposeUseCase.assignRiskAnalysisReviewers(
                    purpose,
                    RiskAnalysisReviewMode.ADMIN_WRITES_REVIEWER_SIGNS,
                    updatedReviewers.stream().map(User::getUserId).toList()
            );
        } finally {
            interopJourney.withConsumer(consumer, updatedReviewers.get(0));
        }
    }

    private void verifyApprovalConflict(RiskAnalysisApprovalPage page, Purpose purpose, User currentReviewer, User otherReviewer) {
        try {
            interopJourney.withConsumer(consumer, otherReviewer);
            purposeUseCase.signRiskAnalysis(purpose);
        } finally {
            interopJourney.withConsumer(consumer, currentReviewer);
        }

        // Keep the first reviewer's page stale after the other reviewer's approval.
        page.approveButton().click();
        page.approvalConfirmationDialog().confirmBtn().click();
        Optional<String> errorMessage = PollingUtils.pollUntil(
                () -> page.approvalErrorAlert().get(FindPolicy.VISIBLE)
                        .map(WebPresentationElement::getText),
                message -> message.filter(text -> !text.isBlank()).isPresent(),
                Duration.ofSeconds(10),
                Duration.ofMillis(100),
                "A visible error alert with a non-blank message must appear when another reviewer has already approved the risk analysis"
        );
        assertThat(errorMessage.orElseThrow())
                .as("Approval conflict error")
                .startsWith(ALREADY_APPROVED_ERROR);
    }

    private void verifyAssignment(RiskAnalysisApprovalPage page, LocalDate expectedDate, String expectedAssignedTo) {

        String actualAssignedTo = page.generalInfoCard().assignedToValue().read();

        assertThat(actualAssignedTo).as("Assigned reviewers").isNotBlank();
        assertThat(actualAssignedTo.split("\\s*,\\s*"))
                .as("Assigned reviewers")
                .containsExactlyInAnyOrder(expectedAssignedTo.split("\\s*,\\s*"));
        verifyAssignmentDateValue(page, expectedDate);
    }

    private static void verifyAssignmentDateValue(RiskAnalysisApprovalPage page, LocalDate expectedDate) {
        String formattedDate = expectedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        assertThat(page.generalInfoCard().assignmentDateValue().read())
                .as("The assignment date must match the current date")
                .isEqualTo(formattedDate);
    }

    private String reviewerDisplayName(User reviewer) {
        return switch (reviewer) {
            case F_ALONSO -> "Fernando Alonso";
            case L_STROLL -> "Lance Stroll";
            case SILVERIO49 -> "Ione Coppola";
            default -> throw new IllegalArgumentException(
                    "No display name mapping configured for reviewer " + reviewer
            );
        };
    }

    private Purpose createToApproveRiskAnalysisWithReviewers(List<User> assignedReviewers, String pendingEServiceName) {

        return interopJourney
                .withConsumer(consumer, UserRole.ADMIN)
                .setUpRiskAnalysisToApprove(
                        pendingEServiceName,
                        producer,
                        consumer,
                        assignedReviewers
                )
                .get(Purpose.class);

    }
}
