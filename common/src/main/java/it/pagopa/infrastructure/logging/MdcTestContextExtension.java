package it.pagopa.infrastructure.logging;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.lang.reflect.Method;

/**
 * Popola l'MDC per i test JUnit, inclusi i test dinamici generati tramite {@code @TestFactory}.
 *
 * <p>
 * Convenzione:
 * </p>
 *
 * <ul>
 *     <li>{@code testCaseId}: identifica il caso di test logico, tipicamente il metodo JUnit
 *     o il metodo factory.</li>
 *     <li>{@code testExecutionId}: identifica la singola esecuzione concreta, quindi il
 *     display name del test o del dynamic test.</li>
 *     <li>{@code scenarioName}: nome leggibile dell'esecuzione corrente.</li>
 *     <li>{@code sourceFile}: classe di test di origine.</li>
 * </ul>
 *
 * <p>
 * La distinzione tra {@code testCaseId} e {@code testExecutionId} è particolarmente utile
 * con {@code @TestFactory}, dove più dynamic test possono essere prodotti dallo stesso
 * metodo factory ma avere display name differenti.
 * </p>
 */
public class MdcTestContextExtension implements BeforeEachCallback, AfterEachCallback {

    private static final Logger LOG =
            LoggerFactory.getLogger(MdcTestContextExtension.class);

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

        LOG.info(
                "=== TEST START: {} | Source: {} ===",
                testExecutionId,
                sourceFile
        );
    }

    @Override
    public void afterEach(ExtensionContext context) {
        try {
            String status = context.getExecutionException().isPresent()
                    ? "FAILED"
                    : "PASSED";

            LOG.info(
                    "=== TEST END: {} | Status: {} ===",
                    context.getDisplayName(),
                    status
            );
        } finally {
            MDC.clear();
        }
    }
}