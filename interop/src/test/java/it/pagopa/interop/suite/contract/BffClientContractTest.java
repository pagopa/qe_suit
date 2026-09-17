package it.pagopa.interop.suite.contract;

import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import it.pagopa.infrastructure.contract.http.HttpContractValidator;
import it.pagopa.infrastructure.fuzzing.FuzzScenario;
import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.bff.client.infrastructure.BffClientRequestFactory;
import it.pagopa.interop.bff.infrastructure.config.BffApiContractConfig;
import it.pagopa.interop.common.client.domain.Client;
import it.pagopa.interop.common.client.domain.ClientKind;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRef;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.utils.RandomUtils;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;

@Execution(ExecutionMode.CONCURRENT)
@SpringBootTest(classes = {TestBootApp.class, JunitContextConfig.class, BffApiContractConfig.class})
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class BffClientContractTest {

    private static final List<FuzzScenario> POSITIVE_SCENARIOS = List.of(
            FuzzScenario.REMOVED,
            FuzzScenario.REPLACED_WITH_NIL_UUID
    );

    private final ApiClient apiClient;
    private final HttpContractValidator httpContractValidator;
    private final InteropJourney interopJourney;
    private final BffClientRequestFactory requestFactory;

    @TestFactory
    Stream<DynamicTest> createConsumerClient() {
        return httpContractValidator
                .apiCall(() -> {
                    interopJourney.withConsumer(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN);
                    return apiClient.clients().createConsumerClient();
                })
                .payload(requestFactory::creationRequest)
                .scenario(POSITIVE_SCENARIOS, BffClientContractTest::getValidatableResponse)
                .tests();
    }

    @TestFactory
    Stream<DynamicTest> createApiClient() {
        return httpContractValidator
                .apiCall(() -> {
                    interopJourney.withConsumer(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN);
                    return apiClient.clients().createApiClient();
                })
                .payload(requestFactory::creationRequest)
                .scenario(POSITIVE_SCENARIOS, BffClientContractTest::getValidatableResponse)
                .tests();
    }

    @TestFactory
    Stream<DynamicTest> createKey() {
        return httpContractValidator
                .apiCall(() -> {
                    Client createdClient = interopJourney
                            .withConsumer(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN)
                            .createClient(clientConfig -> clientConfig
                                    .name(RandomUtils.randomAlphanumericName("client"))
                                    .kind(ClientKind.API)
                                    .users(UserRef.of(User.getTenantUser(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN), Tenant.COMUNE_DI_MILANO))
                            )
                            .get(Client.class);

                    return apiClient.clients().createKey().clientIdPath(createdClient.getId());
                })
                .payload(requestFactory::keyCreationRequest)
                .scenario(POSITIVE_SCENARIOS, BffClientContractTest::getValidatableResponse)
                .tests();
    }

    private static void getValidatableResponse(Response response) {
        response.then().statusCode(anyOf(is(200), is(201), is(204)));
    }
}

