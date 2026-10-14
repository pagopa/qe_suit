package it.pagopa.infrastructure.logging;

/**
 * Chiavi MDC condivise tra suite Cucumber e test JUnit.
 * Vengono esposte come colonne dedicate nel pattern di logback, cosi' da poter
 * raggruppare i log senza dover parsare il testo del messaggio.
 */
public final class TestMdcKeys {

    /** Identificativo stabile del caso di test (es. AGREEMENT_DEPRECATED_DESCRIPTOR_1, createAgreement). */
    public static final String TEST_CASE_ID = "testCaseId";

    /** Nome esteso dello scenario o del singolo caso dinamico. */
    public static final String SCENARIO_NAME = "scenarioName";

    /** Origine del test: feature file per Cucumber, classe per JUnit. */
    public static final String SOURCE_FILE = "sourceFile";

    /** Identificativo unico dell'esecuzione del test (es. AGREEMENT_DEPRECATED_DESCRIPTOR_1#example-1). */
    public static final String TEST_EXECUTION_ID = "testExecutionId";

    private TestMdcKeys() {
    }
}
