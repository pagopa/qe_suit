package it.pagopa.send.suite.contract;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.infrastructure.contract.http.HttpContractPolicy;
import it.pagopa.infrastructure.contract.http.HttpContractValidator;
import it.pagopa.infrastructure.fuzzing.FuzzEngine;
import it.pagopa.infrastructure.fuzzing.FuzzScenario;
import it.pagopa.infrastructure.objectgraph.ObjectGraphDecomposer;
import it.pagopa.send.bff.delivery.infrastructure.config.DeliveryClientConfig;
import it.pagopa.send.bff.delivery.infrastructure.config.DeliveryRequestSpecFactory;
import it.pagopa.send.common.infrastructure.SendHttpContractValidator;
import it.pagopa.send.common.infrastructure.config.FuzzingConfig;
import it.pagopa.send.common.infrastructure.config.JunitContextConfig;
import it.pagopa.send.common.kernel.context.CurrentUserSession;
import it.pagopa.send.common.user.domain.UserAlias;
import it.pagopa.send.common.user.domain.UserProvider;
import it.pagopa.send.common.user.infrastructure.config.PropertiesUserProvider;
import it.pagopa.send.generated.openapi.clients.delivery.api.SenderReadB2BApi;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;

import java.util.List;
import java.util.stream.Stream;

@SpringBootTest(
        classes = {
                JunitContextConfig.class,
                BffDeliveryAuthorizationContractTest.AuthorizationConfiguration.class
        },
        properties = "pn.external.delivery-api-key.grossini=invalid-authorization-contract-key"
)
@ActiveProfiles({"test", "junit"})
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class BffDeliveryAuthorizationContractTest {

    private final SendHttpContractValidator httpContractValidator;
    private final UserProvider userProvider;
    private final SenderReadB2BApi senderReadB2BApi;

    public BffDeliveryAuthorizationContractTest(
            SendHttpContractValidator httpContractValidator,
            UserProvider userProvider,
            SenderReadB2BApi senderReadB2BApi
    ) {
        this.httpContractValidator = httpContractValidator;
        this.userProvider = userProvider;
        this.senderReadB2BApi = senderReadB2BApi;
    }

    @TestFactory
    Stream<DynamicTest> rejectsInvalidDeliveryApiKeyBeforeQueryValidation() {
        return httpContractValidator
                .as(List.of(userProvider.resolve(UserAlias.COMUNE_1)))
                .apiCalls(senderReadB2BApi::retrieveNotificationRequestStatusV26)
                .tests();
    }

    @TestConfiguration(proxyBeanMethods = false)
    @Import({
            FuzzingConfig.class,
            DeliveryClientConfig.class,
            DeliveryRequestSpecFactory.class,
            PropertiesUserProvider.class
    })
    static class AuthorizationConfiguration {

        @Bean
        SendHttpContractValidator sendHttpContractValidator(
                ObjectMapper objectMapper,
                @Qualifier("payloadFuzzEngine") FuzzEngine payloadFuzzEngine,
                @Qualifier("pathParamsFuzzEngine") FuzzEngine pathParamsFuzzEngine,
                ObjectGraphDecomposer objectGraphDecomposer,
                CurrentUserSession currentUserSession
        ) {
            HttpContractPolicy.Builder policy = HttpContractPolicy.builder()
                    .success(response -> {
                    });
            for (FuzzScenario scenario : FuzzScenario.values()) {
                policy.scenario(scenario, response -> {
                });
            }
            HttpContractValidator delegate = new HttpContractValidator(
                    objectMapper,
                    payloadFuzzEngine,
                    pathParamsFuzzEngine,
                    objectGraphDecomposer,
                    policy.build()
            );
            return new SendHttpContractValidator(delegate, currentUserSession);
        }
    }
}
