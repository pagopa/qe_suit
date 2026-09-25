package it.pagopa.interop.common.infrastructure.reporting.contract.lifecycle;

import it.pagopa.interop.suite.contract.BffAgreementContractTest;
import org.junit.jupiter.api.Test;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.TestPlan;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

class ContractReportPlanDetectionListenerTest {

    @Test
    void annotatedRunnerEnablesReport() {
        ContractReportRunState state = new ContractReportRunState();
        ContractReportPlanDetectionListener listener = new ContractReportPlanDetectionListener(state);
        listener.testPlanExecutionStarted(discoverPlan(AnnotatedRunnerTest.class));
        assertTrue(state.reportEnabled());
    }

    @Test
    void nonAnnotatedRunnerDoesNotEnableReport() {
        ContractReportRunState state = new ContractReportRunState();
        ContractReportPlanDetectionListener listener = new ContractReportPlanDetectionListener(state);
        listener.testPlanExecutionStarted(discoverPlan(PlainRunnerTest.class));
        assertFalse(state.reportEnabled());
    }

    @Test
    void singleContractTestDoesNotEnableReport() {
        ContractReportRunState state = new ContractReportRunState();
        ContractReportPlanDetectionListener listener = new ContractReportPlanDetectionListener(state);
        listener.testPlanExecutionStarted(discoverPlan(BffAgreementContractTest.class));
        assertFalse(state.reportEnabled());
    }

    @Test
    void normalUnitTestDoesNotEnableReport() {
        ContractReportRunState state = new ContractReportRunState();
        ContractReportPlanDetectionListener listener = new ContractReportPlanDetectionListener(state);
        listener.testPlanExecutionStarted(discoverPlan(UnitLikeTest.class));
        assertFalse(state.reportEnabled());
    }

    @Test
    void runStateIsIsolatedBetweenSessions() {
        ContractReportRunState first = new ContractReportRunState();
        ContractReportRunState second = new ContractReportRunState();
        new ContractReportPlanDetectionListener(first).testPlanExecutionStarted(discoverPlan(AnnotatedRunnerTest.class));
        new ContractReportPlanDetectionListener(second).testPlanExecutionStarted(discoverPlan(PlainRunnerTest.class));
        assertTrue(first.reportEnabled());
        assertFalse(second.reportEnabled());
    }

    private TestPlan discoverPlan(Class<?> testClass) {
        Launcher launcher = LauncherFactory.create();
        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                .selectors(selectClass(testClass))
                .build();
        return launcher.discover(request);
    }

    @GenerateContractReport
    static class AnnotatedRunnerTest {
        @Test
        void runs() {
        }
    }

    static class PlainRunnerTest {
        @Test
        void runs() {
        }
    }

    static class UnitLikeTest {
        @Test
        void runs() {
        }
    }
}
