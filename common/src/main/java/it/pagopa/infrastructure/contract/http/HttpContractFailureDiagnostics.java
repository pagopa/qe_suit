package it.pagopa.infrastructure.contract.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;

final class HttpContractFailureDiagnostics {
    private HttpContractFailureDiagnostics() {
    }

    static AssertionError enrich(Throwable cause, GeneratedContractCase testCase, HttpContractRequest request, Response response, ObjectMapper objectMapper) {
        StringBuilder message = new StringBuilder("HTTP contract expectation failed");
        message.append("\n- scope: ").append(testCase.scope());
        message.append("\n- scenario: ").append(testCase.mutation().scenario());
        message.append("\n- targetPath: ").append(testCase.target().isRoot() ? "<root>" : testCase.target());
        message.append("\n- mutationKind: ").append(testCase.mutation().kind());
        message.append("\n- mutationValue: ").append(testCase.mutation().value());
        message.append("\n- expectationOrigin: ").append(testCase.expectationOrigin());
        message.append("\n- responseStatus: ").append(readSafely(() -> String.valueOf(response.getStatusCode())));
        message.append("\n- responseBody: ").append(readSafely(response::asString));
        message.append("\n- payload: ").append(renderJson(request.payload(), objectMapper));
        message.append("\n- pathParams: ").append(renderJson(request.pathParams(), objectMapper));
        return new AssertionError(message.toString(), cause);
    }

    /**
     * Variante per la modalita' autorizzazione, che non ha scope, scenario ne' mutazione da
     * riportare. L'operazione e' descritta dalle costanti {@code REQ_METHOD}/{@code REQ_URI} che
     * le classi {@code *Oper} generate espongono: leggibili solo dall'istanza, quindi solo qui,
     * dopo che il supplier l'ha costruita.
     * <p>
     * Non vengono stampati gli header di richiesta, dove vive il bearer token.
     */
    static AssertionError statusMismatch(
            HttpContractIdentity identity,
            String apiLabel,
            Object operation,
            int expectedStatus,
            Response response
    ) {
        StringBuilder message = new StringBuilder("HTTP authorization expectation failed");
        message.append("\n- identity: ").append(identity.name());
        message.append("\n- apiCall: ").append(apiLabel);
        message.append("\n- operation: ").append(describeOperation(operation));
        message.append("\n- expectedStatus: ").append(expectedStatus);
        message.append("\n- actualStatus: ").append(readSafely(() -> String.valueOf(response.getStatusCode())));
        message.append("\n- responseBody: ").append(readSafely(response::asString));
        return new AssertionError(message.toString());
    }

    private static String describeOperation(Object operation) {
        try {
            return operation.getClass().getField("REQ_METHOD").get(operation)
                    + " " + operation.getClass().getField("REQ_URI").get(operation);
        } catch (ReflectiveOperationException | RuntimeException exception) {
            return operation.getClass().getSimpleName();
        }
    }

    private static String renderJson(Object value, ObjectMapper objectMapper) {
        if (value == null) return "null";
        return readSafely(() -> objectMapper.writeValueAsString(value));
    }

    private static String readSafely(SupplierWithException supplier) {
        try {
            return supplier.get();
        } catch (Exception exception) {
            return "<unavailable: " + exception.getClass().getSimpleName() + ">";
        }
    }

    @FunctionalInterface
    private interface SupplierWithException {
        String get() throws Exception;
    }
}
