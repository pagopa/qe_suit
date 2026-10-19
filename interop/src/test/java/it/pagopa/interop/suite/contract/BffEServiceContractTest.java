package it.pagopa.interop.suite.contract;

import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.bff.eservice.infrastructure.BffEServiceRequestFactory;
import it.pagopa.interop.bff.infrastructure.config.BffApiContractConfig;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.common.infrastructure.contract.InteropHttpContractValidator;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

@SpringBootTest(classes = {TestBootApp.class, JunitContextConfig.class, BffApiContractConfig.class})
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class BffEServiceContractTest {

    private final ApiClient apiClient;
    private final InteropHttpContractValidator httpContractValidator;
    private final InteropJourney interopJourney;
    private final BffEServiceRequestFactory requestFactory;

    @TestFactory
    Stream<DynamicTest> createEService() {
        return httpContractValidator
                .as(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN)
                .apiCall(() -> apiClient.eservices().createEService())
                .payload(requestFactory::creationRequest)
                .tests();
    }

    @TestFactory
    Stream<DynamicTest> updateEServiceById() {
        return httpContractValidator
                .as(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN)
                .apiCall(() -> apiClient.eservices().updateEServiceById())
                .pathParams(() -> Map.of("eServiceId", createEServiceId(EServiceDescriptorState.DRAFT)))
                .payload(requestFactory::updateRequest)
                .tests();
    }

    @TestFactory
    Stream<DynamicTest> updateEServiceName() {
        return httpContractValidator
                .as(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN)
                .apiCall(() -> apiClient.eservices().updateEServiceName())
                .pathParams(() -> Map.of("eServiceId", createEServiceId(EServiceDescriptorState.PUBLISHED)))
                .payload(requestFactory::updateNameRequest)
                .tests();
    }

    @TestFactory
    Stream<DynamicTest> updateEServiceDescription() {
        return httpContractValidator
                .as(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN)
                .apiCall(() -> apiClient.eservices().updateEServiceDescription())
                .pathParams(() -> Map.of("eServiceId", createEServiceId(EServiceDescriptorState.PUBLISHED)))
                .payload(requestFactory::updateDescriptionRequest)
                .tests();
    }

    private UUID createEServiceId(EServiceDescriptorState descriptorState) {
        EService createdEService = interopJourney
                .withProducer(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN)
                .createEService(descriptorState)
                .get(EService.class);

        return createdEService.getId();
    }
}

