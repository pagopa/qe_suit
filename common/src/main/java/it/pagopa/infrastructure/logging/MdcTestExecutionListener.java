package it.pagopa.infrastructure.logging;

import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;
import org.slf4j.MDC;

import java.util.Optional;

public class MdcTestExecutionListener implements TestExecutionListener {

    private TestPlan testPlan;

    @Override
    public void testPlanExecutionStarted(TestPlan testPlan) {
        this.testPlan = testPlan;
    }

    @Override
    public void executionStarted(TestIdentifier testIdentifier) {
        if (!testIdentifier.isTest()) {
            return;
        }

        String testExecutionId = testIdentifier.getDisplayName();
        String testCaseId = resolveTestCaseId(testIdentifier);

        MDC.put(TestMdcKeys.TEST_CASE_ID, testCaseId);
        MDC.put(TestMdcKeys.TEST_EXECUTION_ID, testExecutionId);
        MDC.put(TestMdcKeys.SCENARIO_NAME, testExecutionId);
    }

    @Override
    public void executionFinished(
            TestIdentifier testIdentifier,
            TestExecutionResult testExecutionResult
    ) {
        if (!testIdentifier.isTest()) {
            return;
        }

        MDC.remove(TestMdcKeys.TEST_CASE_ID);
        MDC.remove(TestMdcKeys.TEST_EXECUTION_ID);
        MDC.remove(TestMdcKeys.SCENARIO_NAME);
        MDC.remove(TestMdcKeys.SOURCE_FILE);
    }

    private String resolveTestCaseId(TestIdentifier testIdentifier) {
        if (testPlan == null) {
            return testIdentifier.getDisplayName();
        }

        TestIdentifier current = testIdentifier;

        while (true) {
            Optional<TestIdentifier> parent = testPlan.getParent(current);

            if (parent.isEmpty()) {
                return testIdentifier.getDisplayName();
            }

            TestIdentifier parentIdentifier = parent.get();

            /*
             * Nel caso di @TestFactory:
             *
             * getAgreementById()
             *   ├─ [pathParams] ...
             *   ├─ [pathParams] ...
             *   └─ [pathParams] ...
             *
             * il parent immediato del DynamicTest è il factory method.
             */
            if (parentIdentifier.isContainer()) {
                String displayName = parentIdentifier.getDisplayName();

                if (displayName.endsWith("()")) {
                    return stripParentheses(displayName);
                }
            }

            current = parentIdentifier;
        }
    }

    private String stripParentheses(String displayName) {
        return displayName.endsWith("()")
                ? displayName.substring(0, displayName.length() - 2)
                : displayName;
    }
}