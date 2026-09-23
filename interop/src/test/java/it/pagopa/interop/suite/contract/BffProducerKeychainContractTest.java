package it.pagopa.interop.suite.contract;

import io.restassured.response.Response;
import it.pagopa.infrastructure.contract.http.HttpContractValidator;
import it.pagopa.infrastructure.fuzzing.FuzzScenario;
import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.bff.infrastructure.config.BffApiContractConfig;
import it.pagopa.interop.bff.producer_keychain.infrastructure.BffProducerKeychainRequestFactory;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import static it.pagopa.interop.bff.infrastructure.config.BffApiContractConfig.DEFAULT_SUCCESS_STATUS_CODE;
import static org.hamcrest.Matchers.is;

@Execution(ExecutionMode.CONCURRENT)
@SpringBootTest(classes = {TestBootApp.class, JunitContextConfig.class, BffApiContractConfig.class})
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class BffProducerKeychainContractTest {

    private final ApiClient apiClient;
    private final HttpContractValidator httpContractValidator;
    private final InteropJourney interopJourney;
    private final BffProducerKeychainRequestFactory requestFactory;

    @TestFactory
    Stream<DynamicTest> createProducerKeychain() {
        return httpContractValidator
                .apiCall(() -> {
                    interopJourney.withProducer(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN);
                    return apiClient.producerKeychain().createProducerKeychain();
                })
                .payload(requestFactory::creationRequest)
                .targets(
                        FuzzScenario.REMOVED,
                        BffProducerKeychainContractTest::getValidatableResponse,
                        List.of(seed -> seed.getMembers().get(0))
                )
                .tests();
    }

    @TestFactory
    Stream<DynamicTest> createProducerKeychainKey() {
        return httpContractValidator
                .apiCall(() -> {
                    interopJourney.withProducer(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN);
                    return apiClient.producerKeychain().createProducerKey();
                })
                .pathParams(() -> Map.of("producerKeychainId", createProducerKeychainId()))
                .payload(requestFactory::keyCreationRequest)
                .tests();
    }

    private UUID createProducerKeychainId() {
        interopJourney.withProducer(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN);
        Response response = apiClient.producerKeychain()
                .createProducerKeychain()
                .body(requestFactory.creationRequest())
                .execute(value -> value);

        response.then().statusCode(is(DEFAULT_SUCCESS_STATUS_CODE));
        return response.jsonPath().getObject("id", UUID.class);
    }

    private static void getValidatableResponse(Response response) {
        response.then().statusCode(is(DEFAULT_SUCCESS_STATUS_CODE));
    }
}

