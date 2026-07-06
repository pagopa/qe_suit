package it.pagopa.interop.new_arch.bff.agreement.infrastructure.contract;

import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementPayload;
import it.pagopa.interop.new_arch.bff.agreement.infrastructure.BffAgreementRequestFactory;
import it.pagopa.interop.new_arch.common.infrastructure.http.contract.engine.ContractTestEngine;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class BffAgreementContractTest extends ContractTestEngine {

    private final ApiClient apiClient;
    private final BffAgreementRequestFactory requestFactory;

    @TestFactory
    Stream<DynamicTest> createAgreement() {
        AgreementPayload validPayload = requestFactory.baseCreationRequest();

        return fuzz(validPayload)
                .expectValid(200)
                .execute((request, expectedStatus) -> {
                    var operation = apiClient.agreements().createAgreement();
                    injectRawBody(operation, request.body());

                    operation.execute(response -> {
                        response.then().statusCode(expectedStatus);
                        return response;
                    });
                });
    }
}