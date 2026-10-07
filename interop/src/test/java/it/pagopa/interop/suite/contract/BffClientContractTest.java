package it.pagopa.interop.suite.contract;

import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.bff.client.infrastructure.BffClientRequestFactory;
import it.pagopa.interop.bff.infrastructure.config.BffApiContractConfig;
import it.pagopa.interop.common.client.domain.Client;
import it.pagopa.interop.common.client.domain.ClientKind;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.common.infrastructure.contract.InteropHttpContractValidator;
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.Map;
import java.util.stream.Stream;

@SpringBootTest(classes = {TestBootApp.class, JunitContextConfig.class, BffApiContractConfig.class})
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class BffClientContractTest {
    private final ApiClient apiClient;
    private final InteropHttpContractValidator httpContractValidator;
    private final InteropJourney interopJourney;
    private final BffClientRequestFactory requestFactory;

    @TestFactory
    Stream<DynamicTest> createConsumerClient() {
        return httpContractValidator
                .as(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN)
                .apiCall(() -> apiClient.clients().createConsumerClient())
                .payload(requestFactory::creationRequest)
                .tests()
                .filter(test -> test.getDisplayName().equals("[payload] REPLACED_WITH_NIL_UUID @ /members/0"));
    }

    /*@TestFactory
    Stream<DynamicTest> getClient() {
        return httpContractValidator
                .as(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN)
                .apiCall(() -> apiClient.clients().getClient())
                .pathParams(() -> {
                    Client createdClient = interopJourney
                            .withConsumer(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN)
                            .createClient(clientConfig -> clientConfig
                                    .name(RandomUtils.randomAlphanumericName("client"))
                                    .kind(ClientKind.API)
                                    .users(UserRef.of(
                                            User.getTenantUser(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN),
                                            Tenant.COMUNE_DI_MILANO
                                    ))
                            )
                            .get(Client.class);

                    return Map.of("clientId", createdClient.getId());
                })
                .tests();
    }*/

    @TestFactory
    Stream<DynamicTest> createApiClient() {
        return httpContractValidator
                .as(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN)
                .apiCall(() -> apiClient.clients().createApiClient())
                .payload(requestFactory::creationRequest)
                .tests()
                .filter(test -> test.getDisplayName().equals("[payload] REPLACED_WITH_NIL_UUID @ /members/0"));
    }

    @TestFactory
    Stream<DynamicTest> createKey() {
        return httpContractValidator
                .as(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN)
                .apiCall(() -> apiClient.clients().createKey())
                .pathParams(() -> {
                    Client createdClient = interopJourney
                            .withConsumer(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN)
                            .createClient(clientConfig -> clientConfig
                                    .name(RandomUtils.randomAlphanumericName("client"))
                                    .kind(ClientKind.API)
                                    .users(UserRef.of(
                                            User.getTenantUser(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN),
                                            Tenant.COMUNE_DI_MILANO
                                    ))
                            )
                            .get(Client.class);

                    return Map.of("clientId", createdClient.getId());
                })
                .payload(requestFactory::keyCreationRequest)
                .tests()
                .filter(test -> test.getDisplayName().equals("[payload] REPLACED_WITH_URL_INJECTION @ /alg"));
    }

}
