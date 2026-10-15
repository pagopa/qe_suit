package it.pagopa.interop.suite.contract;

import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.bff.client.application.BffClientCreationCommand;
import it.pagopa.interop.bff.client.application.BffClientKeyCreationCommand;
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
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.Map;
import java.util.stream.Stream;

@SpringBootTest(classes = {TestBootApp.class, JunitContextConfig.class, BffApiContractConfig.class})
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class BffClientContractTest {

    private final ApiClient apiClient;
    private final InteropHttpContractValidator httpContractValidator;
    private final InteropJourney interopJourney;
    private final BffClientRequestFactory requestFactory;

    public BffClientContractTest(ApiClient apiClient,
        @Qualifier("bffInteropHttpContractValidator") InteropHttpContractValidator httpContractValidator,
        InteropJourney interopJourney,
        BffClientRequestFactory requestFactory) {
            this.apiClient = apiClient;
            this.httpContractValidator = httpContractValidator;
            this.interopJourney = interopJourney;
            this.requestFactory = requestFactory;
    }

    @TestFactory
    Stream<DynamicTest> createConsumerClient() {
        BffClientCreationCommand command = (BffClientCreationCommand) requestFactory.creationRequest();
        return httpContractValidator
                .as(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN)
                .apiCall(() -> apiClient.clients().createConsumerClient())
                .payload(command::getClientSeed)
                .tests();
    }

    @TestFactory
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
    }

    @TestFactory
    Stream<DynamicTest> createApiClient() {
        return httpContractValidator
                .as(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN)
                .apiCall(() -> apiClient.clients().createApiClient())
                .payload(() -> {
                    BffClientCreationCommand command = (BffClientCreationCommand) requestFactory.creationRequest();
                    return command.getClientSeed();
                })
                .tests();
    }

    @TestFactory
    Stream<DynamicTest> createKey() {
        BffClientKeyCreationCommand command = (BffClientKeyCreationCommand) requestFactory.keyCreationRequest();
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
                .payload(command::getKeySeed)
                .tests();
    }

}