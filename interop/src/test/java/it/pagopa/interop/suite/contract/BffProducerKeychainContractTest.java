package it.pagopa.interop.suite.contract;

import static it.pagopa.interop.bff.infrastructure.config.BffApiContractConfig.DEFAULT_SUCCESS_STATUS_CODE;
import static org.hamcrest.Matchers.is;

import io.restassured.response.Response;
import it.pagopa.infrastructure.fuzzing.FuzzScenario;
import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.bff.infrastructure.config.BffApiContractConfig;
import it.pagopa.interop.bff.producer_keychain.infrastructure.BffProducerKeychainRequestFactory;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.common.infrastructure.contract.InteropHttpContractValidator;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.common.producer_keychain.application.ProducerKeychainFactory;
import it.pagopa.interop.common.producer_keychain.application.command.ProducerKeychainCreationCommand;
import it.pagopa.interop.common.producer_keychain.domain.ProducerKeychain;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

@SpringBootTest(classes = {TestBootApp.class, JunitContextConfig.class, BffApiContractConfig.class})
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class BffProducerKeychainContractTest {

    private final ApiClient apiClient;
    private final InteropHttpContractValidator httpContractValidator;
    private final InteropJourney interopJourney;
    private final BffProducerKeychainRequestFactory requestFactory;
    private final ProducerKeychainFactory commandFactory;

    public BffProducerKeychainContractTest(ApiClient apiClient,
        @Qualifier("bffInteropHttpContractValidator") InteropHttpContractValidator httpContractValidator, InteropJourney interopJourney,
        BffProducerKeychainRequestFactory requestFactory,
        ProducerKeychainFactory commandFactory) {
            this.apiClient = apiClient;
            this.httpContractValidator = httpContractValidator;
            this.interopJourney = interopJourney;
            this.requestFactory = requestFactory;
            this.commandFactory = commandFactory;
    }

    @TestFactory
    Stream<DynamicTest> createProducerKeychain() {
        return httpContractValidator
            .as(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN)
            .apiCall(() -> apiClient.producerKeychain().createProducerKeychain())
            .payload(requestFactory::creationRequest)
            .tests()
            .filter(test -> test.getDisplayName().equals("[payload] REPLACED_WITH_NIL_UUID @ /members/0"));
    }
/*
    @TestFactory
    Stream<DynamicTest> createProducerKey() {
        return httpContractValidator
            .as(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN)
            .apiCall(() -> apiClient.producerKeychain().createProducerKey())
            .pathParams(() -> Map.of("producerKeychainId", createProducerKeychainId()))
            .payload(requestFactory::keyCreationRequest)
            .tests();
    }

    private UUID createProducerKeychainId() {
        ProducerKeychainCreationCommand command = commandFactory.creationCommand();

        ProducerKeychain createdKeychain = interopJourney
            .withProducer(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN)
            .createProducerKeychain(command)
            .get(ProducerKeychain.class);

        return createdKeychain.getId();
    }

    private static void getValidatableResponse(Response response) {
        response.then().statusCode(is(DEFAULT_SUCCESS_STATUS_CODE));
    }*/
}

