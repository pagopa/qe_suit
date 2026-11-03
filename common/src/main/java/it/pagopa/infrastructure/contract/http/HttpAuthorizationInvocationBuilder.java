package it.pagopa.infrastructure.contract.http;

import io.restassured.response.Response;
import it.pagopa.infrastructure.logging.TestMdcKeys;
import org.junit.jupiter.api.DynamicTest;
import org.slf4j.MDC;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * Prodotto cartesiano identita' x API, piu' l'esecuzione del singolo caso.
 * <p>
 * Le due raccolte sono materializzate alla dichiarazione, i supplier sono invocati solo
 * nell'esecuzione del caso: costruire o consumare lo stream dei test non autentica nulla e non
 * emette richieste HTTP.
 * <p>
 * A differenza del percorso di fuzzing, dove {@link HttpContractRuntimeCaseExecutor} ricostruisce
 * il caso mutato interrogando i {@code FuzzEngine}, qui l'esecuzione e' lineare e non ha stato
 * proprio: sta in questa classe invece che in un executor dedicato.
 */
final class HttpAuthorizationInvocationBuilder
        implements HttpContractStages.AuthorizationApiCallsStage,
        HttpContractStages.AuthorizationTestsStage {

    private static final int DEFAULT_EXPECTED_STATUS = 403;

    private final List<HttpContractIdentity> identities;
    private final OpenApiOperationAdapter operationAdapter;

    private List<Supplier<?>> operationSuppliers;
    private int expectedStatus = DEFAULT_EXPECTED_STATUS;

    HttpAuthorizationInvocationBuilder(
            List<HttpContractIdentity> identities,
            OpenApiOperationAdapter operationAdapter
    ) {
        this.identities = requireNonEmpty(identities, "identities");
        this.operationAdapter = operationAdapter;
    }

    @Override
    public HttpContractStages.AuthorizationTestsStage apiCalls(Supplier<?>... operationSuppliers) {
        if (operationSuppliers == null) {
            throw new ContractHttpException("api call suppliers must not be null");
        }
        // Arrays.asList e non List.of: i null arrivano alla validazione, che li segnala con
        // l'indice invece di far uscire un NullPointerException nudo.
        this.operationSuppliers = requireNonEmpty(Arrays.asList(operationSuppliers), "api call supplier");
        return this;
    }

    @Override
    public HttpContractStages.AuthorizationTestsStage expectedStatus(int status) {
        if (status < 100 || status > 599) {
            throw new ContractHttpException("expectedStatus must be a valid HTTP status, was " + status);
        }
        this.expectedStatus = status;
        return this;
    }

    @Override
    public Stream<DynamicTest> tests() {
        if (operationSuppliers == null) {
            throw new ContractHttpException("apiCalls(...) must be declared before tests()");
        }

        // Congelato qui: lo stream e' lazy, e expectedStatus resta modificabile dallo stage.
        int status = expectedStatus;

        return identities.stream().flatMap(identity ->
                IntStream.range(0, operationSuppliers.size())
                        .mapToObj(apiIndex -> toDynamicTest(identity, apiIndex, status))
        );
    }

    private DynamicTest toDynamicTest(HttpContractIdentity identity, int apiIndex, int status) {
        String apiLabel = "api#" + (apiIndex + 1);
        String name = "[" + identity.name() + "] " + apiLabel;
        return dynamicTest(
                name,
                () -> execute(name, identity, apiLabel, status, operationSuppliers.get(apiIndex))
        );
    }

    /**
     * L'ordine autenticazione -> supplier -> esecuzione non e' casuale: il
     * {@code reqSpecSupplier} del client generato viene invocato alla costruzione dell'operazione
     * e congela in quel momento il token dell'identita' corrente del thread. Invertire i primi
     * due passi farebbe partire la richiesta con l'identita' sbagliata.
     */
    private void execute(
            String testName,
            HttpContractIdentity identity,
            String apiLabel,
            int expectedStatus,
            Supplier<?> operationSupplier
    ) {
        String previousScenarioName = MDC.get(TestMdcKeys.SCENARIO_NAME);
        MDC.put(TestMdcKeys.SCENARIO_NAME, testName);
        try {
            String where = "identity " + identity.name() + ", " + apiLabel;

            run(() -> {
                identity.authentication().authenticate();
                return null;
            }, "authentication failed for " + where);

            Object operation = run(operationSupplier::get, "api call supplier failed for " + where);
            if (operation == null) {
                throw new ContractHttpException("api call supplier returned null for " + where);
            }

            // Richiesta vuota: il bind dell'adapter diventa un no-op e l'operazione parte
            // esattamente come l'ha preparata il chiamante nel supplier.
            Response response = operationAdapter.execute(
                    operation,
                    new HttpContractRequest(null, false, null, null)
            );

            if (response.getStatusCode() != expectedStatus) {
                throw HttpContractFailureDiagnostics.statusMismatch(
                        identity,
                        apiLabel,
                        operation,
                        expectedStatus,
                        response
                );
            }
        } finally {
            // Ripristina il contesto del @TestFactory padre invece di azzerarlo: i casi dinamici
            // successivi possono condividere lo stesso thread.
            if (previousScenarioName != null) {
                MDC.put(TestMdcKeys.SCENARIO_NAME, previousScenarioName);
            } else {
                MDC.remove(TestMdcKeys.SCENARIO_NAME);
            }
        }
    }

    /**
     * Gli errori del chiamante (token assente, supplier che lancia) sono errori di configurazione:
     * vanno distinti dagli esiti HTTP, che sono {@code AssertionError}.
     */
    private static <T> T run(Supplier<T> action, String message) {
        try {
            return action.get();
        } catch (ContractHttpException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            throw new ContractHttpException(message, exception);
        }
    }

    /**
     * Una dichiarazione senza utenze o senza chiamate non proverebbe nulla: e' un errore di
     * configurazione, non una suite vuota che passa.
     */
    private static <T> List<T> requireNonEmpty(List<T> values, String what) {
        if (values == null) {
            throw new ContractHttpException(what + " must not be null");
        }
        if (values.isEmpty()) {
            throw new ContractHttpException(what + " must not be empty");
        }
        for (int index = 0; index < values.size(); index++) {
            if (values.get(index) == null) {
                throw new ContractHttpException(what + " at index " + index + " must not be null");
            }
        }
        return List.copyOf(values);
    }
}
