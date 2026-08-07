package it.pagopa.interop.bff.agreement.infrastructure.contract;

import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementPayload;
import it.pagopa.interop.bff.agreement.infrastructure.BffAgreementRequestFactory;
import it.pagopa.interop.common.infrastructure.context.junit.JunitSupportConfig;
import it.pagopa.interop.common.infrastructure.http.contract.engine.ContractTestEngine;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.stream.Stream;

@SpringBootTest(classes = {TestBootApp.class, JunitSupportConfig.class})
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
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