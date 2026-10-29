package it.pagopa.infrastructure.contract.http;

import it.pagopa.infrastructure.reporting.contract.target.ContractCaseTargetRegistry;
import org.junit.jupiter.api.DynamicTest;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.junit.jupiter.api.DynamicTest.dynamicTest;

final class HttpAuthorizationExpectationsStage implements HttpAuthorizationStages.ExpectationsStage {
    static final int DEFAULT_EXPECTED_STATUS = 403;

    private final HttpAuthorizationCaseExecutor executor;
    private final List<HttpContractIdentity> identities;
    private final List<Supplier<?>> operations;
    private int expectedStatus = DEFAULT_EXPECTED_STATUS;

    HttpAuthorizationExpectationsStage(
            OpenApiOperationAdapter operationAdapter,
            List<HttpContractIdentity> identities,
            List<Supplier<?>> operations
    ) {
        this.executor = new HttpAuthorizationCaseExecutor(operationAdapter);
        this.identities = identities;
        this.operations = operations;
    }

    @Override
    public HttpAuthorizationStages.ExpectationsStage expectedStatus(int status) {
        if (status < 100 || status > 599) {
            throw new ContractHttpException("expected status must be a valid HTTP status code: " + status);
        }
        this.expectedStatus = status;
        return this;
    }

    @Override
    public Stream<DynamicTest> tests() {
        int status = expectedStatus;
        Map<String, Integer> labelOccurrences = new HashMap<>();
        identities.forEach(identity -> labelOccurrences.merge(label(identity), 1, Integer::sum));

        List<DynamicTest> tests = new ArrayList<>();
        for (int identityIndex = 0; identityIndex < identities.size(); identityIndex++) {
            HttpContractIdentity identity = identities.get(identityIndex);
            String identityLabel = label(identity);
            boolean repeatedLabel = labelOccurrences.get(identityLabel) > 1;
            for (int apiIndex = 0; apiIndex < operations.size(); apiIndex++) {
                String name = "[authorization] " + identityLabel + " / API"
                        + (repeatedLabel ? " | identity #" + (identityIndex + 1) : "")
                        + " | api #" + (apiIndex + 1)
                        + " | expected " + status;
                HttpAuthorizationCaseExecutor.AuthorizationCase testCase = new HttpAuthorizationCaseExecutor.AuthorizationCase(
                        UUID.randomUUID().toString(),
                        name,
                        identityIndex + 1,
                        apiIndex + 1,
                        identity,
                        operations.get(apiIndex),
                        status
                );
                tests.add(dynamicTest(
                        name,
                        ContractCaseTargetRegistry.sourceUri(testCase.caseId()),
                        () -> executor.execute(testCase)
                ));
            }
        }
        return tests.stream();
    }

    private static String label(HttpContractIdentity identity) {
        return identity.name() == null || identity.name().isBlank() ? "identity" : identity.name();
    }
}
