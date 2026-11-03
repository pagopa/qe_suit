package it.pagopa.infrastructure.logging;

import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.Optional;

/**
 * Logs the authoritative JUnit outcome of each leaf test, including DynamicTest instances.
 */
public class MdcTestExecutionListener implements TestExecutionListener {

    private static final Logger LOG = LoggerFactory.getLogger(MdcTestExecutionListener.class);

    private volatile TestPlan testPlan;

    @Override
    public void testPlanExecutionStarted(TestPlan testPlan) {
        this.testPlan = testPlan;
    }

    @Override
    public void testPlanExecutionFinished(TestPlan testPlan) {
        this.testPlan = null;
    }

    @Override
    public void executionStarted(TestIdentifier testIdentifier) {
        if (!testIdentifier.isTest()) {
            return;
        }
        // MDC is thread-local; do not assume launcher callbacks share a thread
        // with the test executable. Populate it for the event, then restore it.
        withTestContext(testIdentifier, () -> LOG.info(
                "=== DYNAMIC TEST START: {} | Case: {} ===",
                testIdentifier.getDisplayName(), resolveTestCaseId(testIdentifier)));
    }

    @Override
    public void executionFinished(TestIdentifier testIdentifier, TestExecutionResult result) {
        if (!testIdentifier.isTest()) {
            // A @TestFactory is a JUnit container: it may fail while creating
            // DynamicTests, before a single child test can be registered.
            // Do not report successful containers as successful tests.
            if (isFactoryLikeContainer(testIdentifier)
                    && result.getStatus() == TestExecutionResult.Status.FAILED) {
                withTestContext(testIdentifier, () -> {
                    Throwable cause = result.getThrowable().orElse(null);
                    if (cause == null) {
                        LOG.error("=== TEST FACTORY FAILED: {} | Status: FAILED ===",
                                testIdentifier.getDisplayName());
                    } else {
                        LOG.error("=== TEST FACTORY FAILED: {} | Status: FAILED ===",
                                testIdentifier.getDisplayName(), cause);
                    }
                });
            }
            return;
        }
        withTestContext(testIdentifier, () -> {
            String name = testIdentifier.getDisplayName();
            String caseId = resolveTestCaseId(testIdentifier);
            switch (result.getStatus()) {
                case SUCCESSFUL -> LOG.info(
                        "=== DYNAMIC TEST END: {} | Case: {} | Status: PASSED ===", name, caseId);
                case FAILED -> {
                    Throwable cause = result.getThrowable().orElse(null);
                    if (cause == null) {
                        LOG.error("=== DYNAMIC TEST END: {} | Case: {} | Status: FAILED ===", name, caseId);
                    } else {
                        LOG.error("=== DYNAMIC TEST END: {} | Case: {} | Status: FAILED ===", name, caseId, cause);
                    }
                }
                case ABORTED -> {
                    Throwable cause = result.getThrowable().orElse(null);
                    if (cause == null) {
                        LOG.warn("=== DYNAMIC TEST END: {} | Case: {} | Status: ABORTED ===", name, caseId);
                    } else {
                        LOG.warn("=== DYNAMIC TEST END: {} | Case: {} | Status: ABORTED ===", name, caseId, cause);
                    }
                }
            }
        });
    }

    private boolean isFactoryLikeContainer(TestIdentifier identifier) {
        // Jupiter's factory method containers are displayed as methodName().
        // Exclude suite/class containers; use the failed result to prevent noise.
        return identifier.isContainer() && identifier.getDisplayName().endsWith("()");
    }

    private void withTestContext(TestIdentifier identifier, Runnable action) {
        // Preserve unrelated MDC fields and the caller's MDC (important for parallel runs).
        var old = MDC.getCopyOfContextMap();
        try {
            MDC.put(TestMdcKeys.TEST_CASE_ID, resolveTestCaseId(identifier));
            MDC.put(TestMdcKeys.TEST_EXECUTION_ID, identifier.getUniqueId());
            MDC.put(TestMdcKeys.SCENARIO_NAME, identifier.getDisplayName());
            action.run();
        } finally {
            if (old == null) MDC.clear();
            else MDC.setContextMap(old);
        }
    }

    private String resolveTestCaseId(TestIdentifier identifier) {
        TestPlan plan = testPlan;
        if (plan == null) return identifier.getDisplayName();
        TestIdentifier current = identifier;
        while (true) {
            Optional<TestIdentifier> parent = plan.getParent(current);
            if (parent.isEmpty()) return identifier.getDisplayName();
            TestIdentifier p = parent.get();
            if (p.isContainer() && p.getDisplayName().endsWith("()")) {
                String name = p.getDisplayName();
                return name.substring(0, name.length() - 2);
            }
            current = p;
        }
    }
}
