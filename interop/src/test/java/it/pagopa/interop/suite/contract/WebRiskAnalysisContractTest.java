package it.pagopa.interop.suite.contract;

import it.frontend.e2e.framework.core.capability.core.Gettable;
import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.web.infrastructure.config.WebJUnitSuitConfig;
import it.pagopa.interop.web.riskanalysis.infrastructure.suit.page.RiskAnalysisPage;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies the reviewer-facing risk-analysis page.
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
@DisplayName("Risk Analysis Web Contract Tests")
class WebRiskAnalysisContractTest {

    private static final String PAGE_SUBTITLE = "Qui trovi le analisi del rischio assegnate a te. Se una finalità sarà eliminata, non la vedrai più in questa sezione.";
    private static final String NO_ASSIGNED_RISK_ANALYSIS_MESSAGE = "Per ora non hai analisi del rischio assegnate";

    private final WebBrowserContractValidator webContractValidator;
    /**
     * Verifies the risk-analysis page when the reviewer has no assigned analyses
     * and when assigned analyses are available.
     *
     * @return the dynamic contract test for the risk-analysis page states
     */
    @TestFactory
    @DisplayName("Risk analysis page shows the appropriate empty or populated state")
    Stream<DynamicTest> contractPageTests() {
        User reviewer = User.getTenantUser(Tenant.SOGECAP, UserRole.REVIEWER);
        return webContractValidator.as(reviewer, Tenant.SOGECAP)
                .on(RiskAnalysisPage.class)
                .tests(Stream.of(
                        new WebScenario<>(
                                "Reviewer visualize its risk analysis",
                                page -> {
                                },
                                page -> {
                                    page.title().readAndAssert(RiskAnalysisPage.PAGE_TITLE);
                                    page.subtitle().readAndAssert(PAGE_SUBTITLE);

                                    if (page.noAssignedRiskAnalysisMessage().exists(Gettable.FindPolicy.PRESENT)) {
                                        page.noAssignedRiskAnalysisMessage()
                                                .readAndAssert(NO_ASSIGNED_RISK_ANALYSIS_MESSAGE);
                                    } else {
                                        assertThat(page.tabs().get().isPresent())
                                                .as("The Tabs component must be present").isTrue();
                                        assertThat(page.todoTab().isDisabled())
                                                .as("The 'Da fare' tab must be enabled").isFalse();
                                        assertThat(page.completedTab().isDisabled())
                                                .as("The 'Concluse' tab must be enabled").isFalse();
                                        assertThat(page.eserviceFilter().get().isPresent()).isTrue();
                                        assertThat(page.statusFilter().get().isPresent()).isTrue();
                                        assertThat(page.riskAnalysisTable().get().isPresent())
                                                .as("The risk analysis table must be present").isTrue();
                                    }
                                })
                ));
    }
}
