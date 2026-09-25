package it.pagopa.interop.common.infrastructure.reporting.contract.lifecycle;

import it.pagopa.interop.common.infrastructure.reporting.contract.ContractReportGenerator;
import org.junit.jupiter.api.Test;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherSession;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ContractReportLauncherSessionListenerTest {

    @Test
    void launcherSessionClosedDoesNotInvokeGeneratorWhenReportDisabled() {
        SpyContractReportGenerator generator = new SpyContractReportGenerator();
        ContractReportLauncherSessionListener listener = new ContractReportLauncherSessionListener(generator);
        LauncherSession session = mockSession();
        listener.launcherSessionOpened(session);
        listener.launcherSessionClosed(session);
        assertFalse(generator.called);
    }

    @Test
    void launcherSessionClosedInvokesGeneratorWhenReportEnabled() {
        SpyContractReportGenerator generator = new SpyContractReportGenerator();
        ContractReportLauncherSessionListener listener = new ContractReportLauncherSessionListener(generator);
        Launcher launcher = mock(Launcher.class);
        LauncherSession session = mock(LauncherSession.class);
        when(session.getLauncher()).thenReturn(launcher);

        listener.launcherSessionOpened(session);
        ArgumentCaptor<TestExecutionListener> captor = ArgumentCaptor.forClass(TestExecutionListener.class);
        verify(launcher).registerTestExecutionListeners(captor.capture());

        var request = LauncherDiscoveryRequestBuilder.request()
                .selectors(selectClass(AnnotatedRunnerTest.class))
                .build();
        var plan = LauncherFactory.create().discover(request);
        captor.getValue().testPlanExecutionStarted(plan);

        listener.launcherSessionClosed(session);
        assertTrue(generator.called);
    }

    private LauncherSession mockSession() {
        Launcher launcher = mock(Launcher.class);
        LauncherSession session = mock(LauncherSession.class);
        when(session.getLauncher()).thenReturn(launcher);
        return session;
    }

    @GenerateContractReport
    static class AnnotatedRunnerTest {
        @Test
        void runs() {
        }
    }

    static class SpyContractReportGenerator extends ContractReportGenerator {
        private boolean called;

        @Override
        public void generateDefault() {
            called = true;
        }
    }
}
