package it.pagopa.interop.suite.contract;

import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.common.agreement.domain.AgreementState;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
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
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
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
@DisplayName("Risk Analysis Web Contract Tests")
class WebRiskAnalysisContractTest {

    private static final int INITIAL_RISK_ANALYSIS_METADATA_VERSION = 2;
    private static final String RISK_ANALYSIS_REJECTION_REASON = "Risk analysis rejected by contract test";

    private final WebBrowserContractValidator webContractValidator;
    private final InteropJourney interopJourney;

    String PAGE_SUBTITLE = "Qui trovi le analisi del rischio assegnate a te. Se una finalità sarà eliminata, non la vedrai più in questa sezione.";
    String UNAUTHORIZED_TITLE = "Non autorizzato";
    String UNAUTHORIZED_SUBTITLE = "Non possiedi le autorizzazioni necessarie per visualizzare la pagina.";


    /**
     * Creates and submits a risk analysis assigned to a PagoPA reviewer, then verifies
     * the pending and completed views of the risk-analysis page.
     */
    @TestFactory
    @DisplayName("Reviewer access risks analysis page and sees the correct information")
    Stream<DynamicTest> authorizedUserTests() {
        User reviewer = User.getTenantUser(Tenant.PAGO_PA, UserRole.REVIEWER);
        String pendingEServiceName = RandomUtils.randomAlphanumericName("risk-analysis-pending");
        String pendingTobeCompletedEServiceName = RandomUtils.randomAlphanumericName("risk-analysis-pending-tobe-completed");
        String signedEServiceName = RandomUtils.randomAlphanumericName("risk-analysis-signed");
        String rejectedEServiceName = RandomUtils.randomAlphanumericName("risk-analysis-rejected");

        //SIGNED RISK ANALYSIS
        createAndSubmitRiskAnalysis(signedEServiceName, reviewer, RiskAnalysisReviewMode.ADMIN_WRITES_REVIEWER_SIGNS);
        interopJourney
                .withConsumer(Tenant.PAGO_PA, reviewer)
                .signRiskAnalysis(INITIAL_RISK_ANALYSIS_METADATA_VERSION);

      /*  createAndSubmitRiskAnalysis(rejectedEServiceName, reviewer, RiskAnalysisReviewMode.ADMIN_WRITES_REVIEWER_SIGNS);
        interopJourney
                .withConsumer(Tenant.PAGO_PA, reviewer)
                .rejectRiskAnalysis(RISK_ANALYSIS_REJECTION_REASON);*/

        //createAndSubmitRiskAnalysis(pendingEServiceName, reviewer, RiskAnalysisReviewMode.ADMIN_WRITES_REVIEWER_SIGNS);

        return webContractValidator.as(reviewer, Tenant.PAGO_PA)
                .on(RiskAnalysisPage.class)
                .tests(Stream.of(
                        new WebScenario<>(
                                "Reviewer visualize its risk analysis",
                                page -> {},
                                page -> {
                                    page.title().readAndAssert(RiskAnalysisPage.PAGE_TITLE);
                                    page.subtitle().readAndAssert(PAGE_SUBTITLE);

                                    assertThat(page.tabs().get().isPresent()).as("The Tabs component must be present").isTrue();
                                    assertThat(page.todoTab().isDisabled()).as("The 'Da fare' tab must be enabled").isFalse();
                                    assertThat(page.completedTab().isDisabled()).as("The 'Concluse' tab must be enabled").isFalse();

                                    assertThat(page.eserviceFilter().get().isPresent()).isTrue();
                                    assertThat(page.statusFilter().get().isPresent()).isTrue();

                                    assertThat(page.riskAnalysisTable().get().isPresent()).as("Table must be present").isTrue();

                                  /*  assertThat(containsEService(page, pendingEServiceName))
                                            .as("The pending risk analysis must appear in the 'Da fare' tab")
                                            .isTrue();
                                    assertThat(statusesForRows(page))
                                            .as("Every risk analysis in the 'Da fare' tab must be 'da compilare' or 'da approvare'")
                                            .allMatch(status -> status.matches("(?i)da compilare|da approvare"));

                                    String pendingStatus = statusForEService(page, pendingEServiceName);
                                    assertThat(pendingStatus)
                                            .as("The pending risk analysis must be 'da approvare'")
                                            .matches("(?i)da approvare");*/

                                    page.completedTab().click();
                                    PollingUtils.pollUntil(
                                            () -> page.completedTab().isSelected() && !page.todoTab().isSelected(),
                                            Boolean.TRUE::equals,
                                            "The 'Concluse' tab was not selected after clicking it"
                                    );
                                   /* assertThat(page.riskAnalysisTable().get().isPresent()).isTrue();
                                    assertThat(statusesForRows(page))
                                            .as("Every risk analysis in the 'Concluse' tab must be 'rifiutata' or 'approvata'")
                                            .allMatch(status -> status.matches("(?i)rifiutata|approvata"));*/
                                    assertThat(containsEService(page, signedEServiceName))
                                            .as("The signed risk analysis must appear in the 'Concluse' tab")
                                            .isTrue();
                                   /* assertThat(containsEService(page, rejectedEServiceName))
                                            .as("The rejected risk analysis must appear in the 'Concluse' tab")
                                            .isTrue();
                                    assertThat(containsEService(page, pendingEServiceName))
                                            .as("The test risk analysis must not appear in the 'Concluse' tab while it is pending")
                                            .isFalse();*/
                                }
                        )
                ));
    }

    private boolean containsEService(RiskAnalysisPage page, String eServiceName) {
        return page.readAllRows().stream()
                .map(row -> row.get(1))
                .anyMatch(eServiceName::equals);
    }

    private List<String> statusesForRows(RiskAnalysisPage page) {
        return page.readAllRows().stream()
                .map(row -> row.get(4).trim())
                .toList();
    }

    private String statusForEService(RiskAnalysisPage page, String eServiceName) {
        return page.readAllRows().stream()
                .filter(row -> eServiceName.equals(row.get(1)))
                .map(row -> row.get(4).trim())
                .findFirst()
                .orElse("");
    }

    private void createAndSubmitRiskAnalysis(String eServiceName, User reviewer, RiskAnalysisReviewMode reviewMode) {
        createPurposePrerequisites(eServiceName);
        assignAndSubmitRiskAnalysis(
                Tenant.PAGO_PA,
                reviewMode,
                reviewer
        );
    }

    /**
     * Creates the e-service, active agreement, and draft purpose required by the
     * authorized reviewer scenario. Risk-analysis assignment is performed separately.
     */
    private void createPurposePrerequisites(String eServiceName) {
        interopJourney
                .withProducer(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN)
                .createEService(command -> command.name(eServiceName), EServiceDescriptorState.PUBLISHED)
                .withConsumer(Tenant.PAGO_PA, UserRole.ADMIN)
                .linkAgreement(AgreementState.ACTIVE)
                .createPurpose(PurposeVersionState.DRAFT);
    }

    /**
     * Assigns the purpose's risk analysis to the reviewer using the requested producer
     * and review mode, then submits it.
     */
    private void assignAndSubmitRiskAnalysis(
            Tenant producer,
            RiskAnalysisReviewMode reviewMode,
            User reviewer
    ) {
        interopJourney
                .withProducer(producer, UserRole.ADMIN)
                .assignRiskAnalysisReviewers(reviewMode, List.of(reviewer.getUserId()))
                .submitRiskAnalysis();
    }

    /**
     * Verifies that an administrator from a tenant without reviewer access sees
     * the unauthorized page instead of the risk-analysis workspace.
     */
    @TestFactory
    @DisplayName("Utente non autorizzato visualizza la pagina di errore")
    Stream<DynamicTest> unauthorizedUserTests() {
        return webContractValidator.as(User.getTenantAdmin(Tenant.COMUNE_DI_MILANO), Tenant.COMUNE_DI_MILANO)
                .on(RiskAnalysisPage.class)
                .tests(Stream.of(
                        new WebScenario<>(
                                "An ADMIN user visualizes 'Non autorizzato'",
                                page -> {},
                                page -> {
                                    assertThat(page.tabs().get().isPresent()).isFalse();

                                    page.unauthorizedTitle().readAndAssert(UNAUTHORIZED_TITLE);
                                    page.unauthorizedSubtitle().readAndAssert(UNAUTHORIZED_SUBTITLE);
                                    assertThat(page.backToHomeBtn().isDisabled()).as("Il bottone 'Torna alla home' deve essere cliccabile").isFalse();
                                }
                        )
                ));
    }
}