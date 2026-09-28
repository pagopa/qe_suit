package it.pagopa.infrastructure.logging;

import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.slf4j.MDC;

public class MdcTestExecutionListener implements TestExecutionListener {

    @Override
    public void executionStarted(TestIdentifier testIdentifier) {
        if (!testIdentifier.isTest()) {
            return;
        }

        String displayName = testIdentifier.getDisplayName();

        MDC.put(TestMdcKeys.TEST_EXECUTION_ID, displayName);
        MDC.put(TestMdcKeys.SCENARIO_NAME, displayName);
    }

    @Override
    public void executionFinished(
            TestIdentifier testIdentifier,
            TestExecutionResult testExecutionResult
    ) {
        if (!testIdentifier.isTest()) {
            return;
        }

        MDC.remove(TestMdcKeys.TEST_EXECUTION_ID);
        MDC.remove(TestMdcKeys.SCENARIO_NAME);
    }
}