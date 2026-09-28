package it.pagopa.infrastructure.logging;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.lang.reflect.Method;

/**
 * Popola l'MDC per i test JUnit, inclusi i contract test basati su {@code @TestFactory}.
 * <p>
 * Va registrata come extension: i metodi annotati {@code @BeforeEach} dichiarati in una
 * {@code @TestConfiguration} non vengono invocati da JUnit, perche' il ciclo di vita viene
 * risolto solo sulla gerarchia della classe di test e sulle extension registrate.
 */
public class MdcTestContextExtension implements BeforeEachCallback, AfterEachCallback {

    private static final Logger LOG = LoggerFactory.getLogger(MdcTestContextExtension.class);

    @Override
    public void beforeEach(ExtensionContext context) {
        String testCaseId = context.getTestMethod()
                .map(Method::getName)
                .orElseGet(context::getDisplayName);

        String sourceFile = context.getTestClass()
                .map(Class::getSimpleName)
                .orElse("unknown");

        MDC.put(TestMdcKeys.TEST_CASE_ID, testCaseId);
        MDC.put(TestMdcKeys.SCENARIO_NAME, context.getDisplayName());
        MDC.put(TestMdcKeys.SOURCE_FILE, sourceFile);

        LOG.info("=== SCENARIO START: {} | Source: {} ===", context.getDisplayName(), sourceFile);
    }

    @Override
    public void afterEach(ExtensionContext context) {
        String status = context.getExecutionException().isPresent() ? "FAILED" : "PASSED";
        LOG.info("=== SCENARIO END: {} | Status: {} ===", context.getDisplayName(), status);

        MDC.remove(TestMdcKeys.TEST_CASE_ID);
        MDC.remove(TestMdcKeys.SCENARIO_NAME);
        MDC.remove(TestMdcKeys.SOURCE_FILE);
    }
}
