package it.pagopa.infrastructure.contract.http;

import io.restassured.response.Response;
import it.pagopa.infrastructure.logging.TestMdcKeys;
import it.pagopa.infrastructure.reporting.contract.target.ContractCaseTargetRegistry;
import org.slf4j.MDC;

import java.util.function.Supplier;

final class HttpAuthorizationCaseExecutor {
    private final OpenApiOperationAdapter operationAdapter;

    HttpAuthorizationCaseExecutor(OpenApiOperationAdapter operationAdapter) {
        this.operationAdapter = operationAdapter;
    }

    void execute(AuthorizationCase testCase) {
        String previousScenarioName = MDC.get(TestMdcKeys.SCENARIO_NAME);
        MDC.put(TestMdcKeys.SCENARIO_NAME, testCase.name());
        try {
            authenticate(testCase);
            Object operation = materializeOperation(testCase);
            HttpOperationTarget target = HttpOperationTarget.from(operation);
            ContractCaseTargetRegistry.register(testCase.caseId(), target.reportTarget());

            Response response = operationAdapter.execute(operation);
            int actualStatus = response.getStatusCode();
            if (actualStatus != testCase.expectedStatus()) {
                throw HttpContractFailureDiagnostics.authorizationFailure(
                        testCase.identityLabel(),
                        target,
                        testCase.expectedStatus(),
                        response
                );
            }
        } finally {
            // Come HttpContractRuntimeCaseExecutor: ripristina il contesto del @TestFactory padre.
            if (previousScenarioName != null) {
                MDC.put(TestMdcKeys.SCENARIO_NAME, previousScenarioName);
            } else {
                MDC.remove(TestMdcKeys.SCENARIO_NAME);
            }
        }
    }

    private void authenticate(AuthorizationCase testCase) {
        try {
            testCase.identity().authentication().authenticate();
        } catch (RuntimeException exception) {
            throw new ContractHttpException(
                    "authentication failed for " + testCase.description(),
                    exception
            );
        }
    }

    private Object materializeOperation(AuthorizationCase testCase) {
        try {
            Object operation = testCase.operationSupplier().get();
            if (operation == null) {
                throw new ContractHttpException("operation supplier returned null for " + testCase.description());
            }
            return operation;
        } catch (ContractHttpException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            throw new ContractHttpException("operation supplier failed for " + testCase.description(), exception);
        }
    }

    record AuthorizationCase(
            String caseId,
            String name,
            int identityNumber,
            int apiNumber,
            HttpContractIdentity identity,
            Supplier<?> operationSupplier,
            int expectedStatus
    ) {
        String identityLabel() {
            return identity.name() == null || identity.name().isBlank()
                    ? "identity #" + identityNumber
                    : identity.name();
        }

        String description() {
            return "identity '" + identityLabel() + "' (#" + identityNumber + "), api #" + apiNumber;
        }
    }
}
