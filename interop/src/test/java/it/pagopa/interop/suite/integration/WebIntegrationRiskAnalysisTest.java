package it.pagopa.interop.suite.integration;

import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.common.purpose.domain.PurposeVersionState;
import it.pagopa.interop.common.risk_analysis.domain.RiskAnalysisReviewMode;
import it.pagopa.interop.web.infrastructure.config.WebJUnitSuitConfig;
import it.pagopa.interop.web.riskanalysis.infrastructure.suit.page.RiskAnalysisPage;
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

/**
 * Verifies that backend risk-analysis assignments and review outcomes appear in
 * the reviewer workspace, and that an administrator cannot access that workspace.
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
@DisplayName("Risk Analysis Web Integration Tests")
class WebIntegrationRiskAnalysisTest {

    private static final int INITIAL_RISK_ANALYSIS_METADATA_VERSION = 2;
    private static final String RISK_ANALYSIS_REJECTION_REASON = "Risk analysis rejected by contract test";
    private static final String UNAUTHORIZED_TITLE = "Non autorizzato";
    private static final String UNAUTHORIZED_SUBTITLE = "Non possiedi le autorizzazioni necessarie per visualizzare la pagina.";

    private final WebBrowserContractValidator webContractValidator;

    private final InteropJourney interopJourney;
    private final Tenant consumer = Tenant.PAGO_PA;
    private final User reviewer = User.getTenantUser(consumer, UserRole.REVIEWER);
    private final Tenant producer = Tenant.COMUNE_DI_MILANO;

    private final String olderEServiceName = RandomUtils.randomAlphanumericName("risk-analysis-compile-and-signed");
    private final String newerEServiceName = RandomUtils.randomAlphanumericName("risk-analysis-approve-and-rejected");

    /**
     * Creates two e-services with two purposes each: compilation and signed for the
     * older service, approval and rejected for the newer service. Checks statuses,
     * reviewer counts and newest-first ordering in both tabs.
     *
     * @return dynamic integration tests for the reviewer's pending and completed analyses
     */
    @TestFactory
    @DisplayName("Reviewer views assigned RA")
    Stream<DynamicTest> authorizedUserTests() {
        List<User> reviewers = User.getTenantUsers(consumer, UserRole.REVIEWER);
        assertThat(reviewers)
                .as("At least two reviewers must be configured for the consumer")
                .hasSizeGreaterThanOrEqualTo(2);

        EService olderEService = interopJourney.withConsumer(consumer, UserRole.ADMIN)
                .setUpRiskAnalysisToCompile(olderEServiceName, producer, consumer, reviewer)
                .get(EService.class);
        createAndSignRiskAnalysis(olderEService);

        EService newerEService = createToApproveRiskAnalysisWithTwoReviewers(reviewers, newerEServiceName);
        createAndRejectRiskAnalysis(newerEService);

        return webContractValidator.as(reviewer, consumer)
                .on(RiskAnalysisPage.class)
                .tests(Stream.of(
                        new WebScenario<>(
                                "Reviewer views assigned risk analyses",
                                page -> {
                                },
                                page -> {
                                    verifyTodoTab(page);

                                    page.completedTab().click();

                                    verifyCompletedTab(page);
                                }
                        )
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
        return webContractValidator.as(User.getTenantAdmin(producer), producer)
                .on(RiskAnalysisPage.class)
                .tests(Stream.of(
                        new WebScenario<>(
                                "An ADMIN user visualizes 'Unauthorized'",
                                page -> {
                                },
                                page -> {
                                    assertThat(page.tabs().get().isPresent()).isFalse();

                                    page.unauthorizedTitle().readAndAssert(UNAUTHORIZED_TITLE);
                                    page.unauthorizedSubtitle().readAndAssert(UNAUTHORIZED_SUBTITLE);
                                    assertThat(page.backToHomeBtn().isDisabled())
                                            .as("The 'Torna alla home' button must be enabled")
                                            .isFalse();
                                }
                        )
                ));
    }

    private EService createToApproveRiskAnalysisWithTwoReviewers(List<User> reviewers, String eServiceName) {
        List<User> assignedReviewers = reviewers.subList(0, 2);

        return interopJourney
                .withConsumer(consumer, UserRole.ADMIN)
                .setUpRiskAnalysisToApprove(
                        eServiceName,
                        producer,
                        consumer,
                        assignedReviewers
                )
                .get(EService.class);
    }

    private void verifyCompletedTab(RiskAnalysisPage page) {
        PollingUtils.pollUntil(
                () -> page.completedTab().isSelected() && !page.todoTab().isSelected(),
                Boolean.TRUE::equals,
                "The 'Concluse' tab was not selected after clicking it"
        );

        List<RiskAnalysisPage.RiskAnalysisRow> firstPageRows =
                page.readRecordsEserviceStatus();

        verifyStatusAndReviewerName(olderEServiceName, firstPageRows, "approvata");
        verifyStatusAndReviewerName(newerEServiceName, firstPageRows, "rifiutata");

        assertOlderAnalysisAppearsAfterNewer(
                firstPageRows,
                olderEServiceName,
                newerEServiceName
        );
    }

    private void verifyTodoTab(RiskAnalysisPage page) {
        assertThat(page.todoTab().isSelected())
                .as("The 'Da fare' tab must be selected")
                .isTrue();

        List<RiskAnalysisPage.RiskAnalysisRow> firstPageRows =
                page.readRecordsEserviceStatus();

        verifyStatusAndReviewerCount(olderEServiceName, firstPageRows, 1, "da compilare");
        verifyStatusAndReviewerCount(newerEServiceName, firstPageRows, 2, "da approvare");

        assertOlderAnalysisAppearsAfterNewer(
                firstPageRows,
                olderEServiceName,
                newerEServiceName
        );
    }

    private void verifyStatusAndReviewerCount(
            String eServiceName,
            List<RiskAnalysisPage.RiskAnalysisRow> rows,
            int expectedReviewerCount,
            String expectedStatus
    ) {
        RiskAnalysisPage.RiskAnalysisRow row = findRowForEService(rows, eServiceName);

        verifyStatus(row, expectedStatus);
        assertThat(row.reviewerOrTotReviewer())
                .as("The reviewer count for e-service '%s' must not be blank", eServiceName)
                .isNotBlank();
        assertThat(Integer.parseInt(row.reviewerOrTotReviewer()))
                .as("The reviewer count for e-service '%s' must equal %d", eServiceName, expectedReviewerCount)
                .isEqualTo(expectedReviewerCount);
    }

    private void verifyStatusAndReviewerName(
            String eServiceName,
            List<RiskAnalysisPage.RiskAnalysisRow> rows,
            String expectedStatus
    ) {
        RiskAnalysisPage.RiskAnalysisRow row = findRowForEService(rows, eServiceName);

        verifyStatus(row, expectedStatus);
        assertThat(row.reviewerOrTotReviewer())
                .as("The reviewer name for e-service '%s' must not be blank", eServiceName)
                .isNotBlank();

        String reviewerName = reviewerDisplayName(reviewer);
        assertThat(row.reviewerOrTotReviewer())
                .as("The reviewer name for e-service '%s' must equal '%s'", eServiceName, reviewerName)
                .isEqualTo(reviewerName);
    }

    private void verifyStatus(RiskAnalysisPage.RiskAnalysisRow row, String expectedStatus) {
        assertThat(row.status())
                .as("The risk analysis for e-service '%s' must have status '%s'", row.eserviceName(), expectedStatus)
                .isEqualToIgnoringCase(expectedStatus);
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

    /**
     * Finds the row of an e-service in the currently displayed table.
     *
     * @param rows         table rows read from the active tab
     * @param eServiceName name of the e-service to find
     * @return the matching row
     * @throws AssertionError if the e-service is absent
     */
    private RiskAnalysisPage.RiskAnalysisRow findRowForEService(
            List<RiskAnalysisPage.RiskAnalysisRow> rows,
            String eServiceName
    ) {
        return rows.stream()
                .filter(r -> eServiceName.equals(r.eserviceName()))
                .findFirst()
                .orElseThrow(() -> new AssertionError(
                        "E-service not found on the current page: " + eServiceName
                                + ". Found: " + rows.stream().map(RiskAnalysisPage.RiskAnalysisRow::eserviceName).toList()
                ));
    }

    private void createAndSignRiskAnalysis(EService eService) {
        createAdditionalRiskAnalysisToApprove(eService);
        interopJourney
                .withConsumer(consumer, reviewer).signRiskAnalysis(INITIAL_RISK_ANALYSIS_METADATA_VERSION);
    }

    private void createAndRejectRiskAnalysis(EService eService) {
        createAdditionalRiskAnalysisToApprove(eService);
        interopJourney
                .withConsumer(consumer, reviewer).rejectRiskAnalysis(RISK_ANALYSIS_REJECTION_REASON);
    }

    private void createAdditionalRiskAnalysisToApprove(EService eService) {
        interopJourney.withConsumer(consumer, UserRole.ADMIN)
                .createPurposeAndLinkToEService(eService, PurposeVersionState.DRAFT)
                .assignRiskAnalysisReviewers(
                        RiskAnalysisReviewMode.ADMIN_WRITES_REVIEWER_SIGNS,
                        List.of(reviewer.getUserId())
                )
                .submitRiskAnalysis();
    }

    /**
     * Checks newest-first ordering for two analyses expected to be present in the
     * supplied rows. The caller verifies their presence through their statuses.
     *
     * @param rows              table rows read from the relevant tab
     * @param olderEServiceName e-service name for the older analysis
     * @param newerEServiceName e-service name for the newer analysis
     */
    private static void assertOlderAnalysisAppearsAfterNewer(
            List<RiskAnalysisPage.RiskAnalysisRow> rows,
            String olderEServiceName,
            String newerEServiceName
    ) {
        List<String> eserviceNames = rows.stream()
                .map(RiskAnalysisPage.RiskAnalysisRow::eserviceName)
                .toList();
        assertThat(eserviceNames.indexOf(olderEServiceName))
                .as("The older RA for e-service '%s' must appear after the newer RA for e-service '%s' in descending date order",
                        olderEServiceName,
                        newerEServiceName)
                .isGreaterThan(eserviceNames.indexOf(newerEServiceName));
    }
}