package it.pagopa.infrastructure.reporting.contract.lifecycle;

import it.pagopa.infrastructure.reporting.contract.ContractReportGenerator;
import org.junit.platform.launcher.LauncherSession;
import org.junit.platform.launcher.LauncherSessionListener;

public class ContractReportLauncherSessionListener implements LauncherSessionListener {

    private final ContractReportGenerator generator;
    private ContractReportRunState runState;

    @SuppressWarnings("unused")
    public ContractReportLauncherSessionListener() {
        this(new ContractReportGenerator());
    }

    ContractReportLauncherSessionListener(ContractReportGenerator generator) {
        this.generator = generator;
    }

    @Override
    public void launcherSessionOpened(LauncherSession session) {
        runState = new ContractReportRunState();
        ContractReportPlanDetectionListener detector = new ContractReportPlanDetectionListener(runState);
        session.getLauncher().registerTestExecutionListeners(detector);
    }

    @Override
    public void launcherSessionClosed(LauncherSession session) {
        try {
            if (runState != null && runState.reportEnabled()) {
                generator.generateDefault();
            }
        } finally {
            runState = null;
        }
    }
}
