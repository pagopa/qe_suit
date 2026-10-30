package it.pagopa.infrastructure.logging;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.lang.reflect.Method;

/**
 * Provides execution MDC for Jupiter test methods and factory lifecycle.
 * Leaf-test outcomes (including every DynamicTest) are reported by
 * {@link MdcTestExecutionListener}; the factory's own lifecycle must never be
 * mistaken for the pass/fail result of its generated children.
 */
public class MdcTestContextExtension implements BeforeEachCallback, AfterEachCallback {

    private static final Logger LOG = LoggerFactory.getLogger(MdcTestContextExtension.class);

    @Override
    public void beforeEach(ExtensionContext context) {
        MDC.clear();
        String testCaseId = context.getTestMethod()
                .map(Method::getName)
                .orElseGet(context::getDisplayName);
        String testExecutionId = context.getDisplayName();
        String sourceFile = context.getTestClass()
                .map(Class::getSimpleName)
                .orElse("unknown");
        MDC.put(TestMdcKeys.TEST_CASE_ID, testCaseId);
        MDC.put(TestMdcKeys.TEST_EXECUTION_ID, testExecutionId);
        MDC.put(TestMdcKeys.SCENARIO_NAME, testExecutionId);
        MDC.put(TestMdcKeys.SOURCE_FILE, sourceFile);
        LOG.info("=== TEST LIFECYCLE START: {} | Source: {} ===", testExecutionId, sourceFile);
    }

    @Override
    public void afterEach(ExtensionContext context) {
        try {
            // Do not report PASSED/FAILED here: @TestFactory callbacks are not
            // invoked once per DynamicTest. The launcher listener owns outcomes.
            LOG.info("=== TEST LIFECYCLE END: {} ===", context.getDisplayName());
        } finally {
            MDC.clear();
        }
    }
}
