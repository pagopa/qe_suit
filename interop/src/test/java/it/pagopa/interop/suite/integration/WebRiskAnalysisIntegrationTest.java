package it.pagopa.interop.suite.integration;

import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRole;
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
 * Verifies the reviewer-facing RA page, its access-control behavior and its integration with the backend services.
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
class WebRiskAnalysisIntegrationTest {

    private static final int INITIAL_RISK_ANALYSIS_METADATA_VERSION = 2;
    private static final String RISK_ANALYSIS_REJECTION_REASON = "Risk analysis rejected by contract test";
    private static final String UNAUTHORIZED_TITLE = "Non autorizzato";
    private static final String UNAUTHORIZED_SUBTITLE = "Non possiedi le autorizzazioni necessarie per visualizzare la pagina.";

    private final WebBrowserContractValidator webContractValidator;

    private final InteropJourney interopJourney;
    private final Tenant consumer = Tenant.SOGECAP;
    private final User reviewer = User.getTenantUser(consumer, UserRole.REVIEWER);
    private final Tenant producer = Tenant.COMUNE_DI_MILANO;

    /**
     * Creates and submits a risk analysis assigned to a reviewer, then verifies
     * the pending and completed views of the risk-analysis page.
     *
     * @return the dynamic contract test for the reviewer's pending and completed analyses
     */
    @TestFactory
    @DisplayName("Reviewer views assigned RA")
    Stream<DynamicTest> authorizedUserTests() {
        User reviewer = User.getTenantUser(consumer, UserRole.REVIEWER);
        String pendingEServiceName = RandomUtils.randomAlphanumericName("risk-analysis-pending");
        String pendingToBeCompletedEServiceName = RandomUtils.randomAlphanumericName("risk-analysis-pending-tobe-completed");
        String signedEServiceName = RandomUtils.randomAlphanumericName("risk-analysis-signed");
        String rejectedEServiceName = RandomUtils.randomAlphanumericName("risk-analysis-rejected");

        interopJourney.withConsumer(consumer, UserRole.ADMIN)
                .setUpRiskAnalysisToCompile(pendingToBeCompletedEServiceName, producer, consumer, reviewer);
        interopJourney.setUpRiskAnalysisToApprove(pendingEServiceName, producer, consumer, reviewer);
        createAndSignRiskAnalysis(signedEServiceName);
        createAndRejectRiskAnalysis(rejectedEServiceName);

        return webContractValidator.as(reviewer, consumer)
                .on(RiskAnalysisPage.class)
                .tests(Stream.of(
                        new WebScenario<>(
                                "Reviewer visualize its risk analysis",
                                page -> {
                                },
                                page -> {
                                    daFareTabVerify(page, pendingToBeCompletedEServiceName, pendingEServiceName);

                                    page.completedTab().click();


                                    concluseTabVerify(page, signedEServiceName, rejectedEServiceName);
                                }
                        )
                ));
    }

    private void concluseTabVerify(RiskAnalysisPage page, String signedEServiceName, String rejectedEServiceName) {
        PollingUtils.pollUntil(
                () -> page.completedTab().isSelected() && !page.todoTab().isSelected(),
                Boolean.TRUE::equals,
                "The 'Concluse' tab was not selected after clicking it"
        );

        List<RiskAnalysisPage.RiskAnalysisRow> firstPageRowsConcluse = page.readRecordsEserviceStatus();

        String signedStatus = statusForEService(firstPageRowsConcluse, signedEServiceName);
        assertThat(signedStatus)
                .as("The signed risk analysis for e-service '%s' must be on the first page with status 'approvata'",
                        signedEServiceName)
                .matches("(?i)approvata");

        String rejectedStatus = statusForEService(firstPageRowsConcluse, rejectedEServiceName);
        assertThat(rejectedStatus)
                .as("The rejected risk analysis for e-service '%s' must be on the first page with status 'rifiutata'",
                        rejectedEServiceName)
                .matches("(?i)rifiutata");

        assertOlderAnalysisAppearsAfterNewer(
                firstPageRowsConcluse,
                signedEServiceName,
                rejectedEServiceName
        );
    }

    private void daFareTabVerify(RiskAnalysisPage page, String pendingToBeCompletedEServiceName, String pendingEServiceName) {
        assertThat(page.todoTab().isSelected())
                .as("The 'Da fare' tab must be selected")
                .isTrue();

        List<RiskAnalysisPage.RiskAnalysisRow> firstPageRows = page.readRecordsEserviceStatus();

        String pendingToBeCompletedStatus =
                statusForEService(firstPageRows, pendingToBeCompletedEServiceName);
        assertThat(pendingToBeCompletedStatus)
                .as("The pending-to-be-complete risk analysis for e-service '%s' must be on the first page with status 'da compilare'",
                        pendingToBeCompletedEServiceName)
                .matches("(?i)da compilare");

        String pendingStatus = statusForEService(firstPageRows, pendingEServiceName);
        assertThat(pendingStatus)
                .as("The pending risk analysis for e-service '%s' must be on the first page with status 'da approvare'",
                        pendingEServiceName)
                .matches("(?i)da approvare");

        assertOlderAnalysisAppearsAfterNewer(
                firstPageRows,
                pendingToBeCompletedEServiceName,
                pendingEServiceName
        );
    }

    /**
     * Verifies that an administrator from a tenant without reviewer access sees
     * the unauthorized page instead of the risk-analysis workspace.
     *
     * @return the dynamic contract test for unauthorized access
     */
    @TestFactory
    @DisplayName("Unauthorized user sees the error page")
    Stream<DynamicTest> unauthorizedUserTests() {
        return webContractValidator.as(User.getTenantAdmin(producer),producer)
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


    /**
     * Finds the status of an e-service in the currently displayed table rows.
     *
     * @param rows table rows read from the active tab
     * @param eServiceName name of the e-service whose status is requested
     * @return the matching status, or an empty string if the e-service is absent
     */
    private String statusForEService(List<RiskAnalysisPage.RiskAnalysisRow> rows, String eServiceName) {
        return rows.stream()
                .filter(row -> eServiceName.equals(row.eserviceName()))
                .map(RiskAnalysisPage.RiskAnalysisRow::status)
                .findFirst()
                .orElse("");
    }

    /**
     * Creates, submits, and signs a risk analysis as the reviewer.
     *
     * @param eServiceName name assigned to the e-service associated with the analysis
     */
    private void createAndSignRiskAnalysis(String eServiceName) {
        interopJourney.setUpRiskAnalysisToApprove(eServiceName, producer, consumer, reviewer);
        interopJourney
                .withConsumer(consumer, reviewer).signRiskAnalysis(INITIAL_RISK_ANALYSIS_METADATA_VERSION);
    }

    /**
     * Creates, submits, and rejects a risk analysis as the reviewer.
     *
     * @param eServiceName name assigned to the e-service associated with the analysis
     */
    private void createAndRejectRiskAnalysis(String eServiceName) {
        interopJourney.setUpRiskAnalysisToApprove(eServiceName, producer, consumer, reviewer);
        interopJourney
                .withConsumer(consumer, reviewer).rejectRiskAnalysis(RISK_ANALYSIS_REJECTION_REASON);
    }

    /**
     * Asserts that an older analysis appears after a newer analysis in the displayed
     * rows.
     *
     * @param rows table rows read from the relevant tab
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