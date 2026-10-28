package it.pagopa.interop.suite.contract;

import it.pagopa.infrastructure.fuzzing.FuzzScenario;
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
import it.pagopa.interop.generated.openapi.clients.bff.model.CatalogFilterPayload;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import static it.pagopa.interop.bff.infrastructure.config.BffApiContractConfig.DEFAULT_SUCCESS_STATUS_CODE;
import static org.hamcrest.Matchers.is;

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


    @Tag("private-catalog")
    @TestFactory
    Stream<DynamicTest> queryEServicesCatalog() {
        return httpContractValidator
                .as(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN)
                .apiCall(() -> apiClient.eservices().queryEServicesCatalog())
                .payload(requestFactory::searchCatalogRequest)
                .targets(
                        FuzzScenario.REPLACED_WITH_ZERO,
                        response -> response.then().statusCode(is(DEFAULT_SUCCESS_STATUS_CODE)),
                        List.of(CatalogFilterPayload::getOffset)
                )
                .targets(
                        FuzzScenario.REPLACED_WITH_MAX_VALUE,
                        response -> response.then().statusCode(is(DEFAULT_SUCCESS_STATUS_CODE)),
                        List.of(CatalogFilterPayload::getOffset)
                )
                .targets(
                        FuzzScenario.REPLACED_WITH_EMPTY_STRING,
                        response -> response.then().statusCode(is(DEFAULT_SUCCESS_STATUS_CODE)),
                        List.of(CatalogFilterPayload::getKeyword)
                )
                .targets(
                        FuzzScenario.REPLACED_WITH_BLANK_STRING,
                        response -> response.then().statusCode(is(DEFAULT_SUCCESS_STATUS_CODE)),
                        List.of(CatalogFilterPayload::getKeyword)
                )
                .targets(
                        FuzzScenario.REPLACED_WITH_URL_INJECTION,
                        response -> response.then().statusCode(is(DEFAULT_SUCCESS_STATUS_CODE)),
                        List.of(CatalogFilterPayload::getKeyword)
                )
                .targets(
                        FuzzScenario.REPLACED_WITH_SQL_INJECTION,
                        response -> response.then().statusCode(is(DEFAULT_SUCCESS_STATUS_CODE)),
                        List.of(CatalogFilterPayload::getKeyword)
                )
                .targets(
                        FuzzScenario.REPLACED_WITH_NIL_UUID,
                        response -> response.then().statusCode(is(DEFAULT_SUCCESS_STATUS_CODE)),
                        List.of(CatalogFilterPayload::getTemplateId)
                )
                .targets(
                        FuzzScenario.REPLACED_WITH_NULL,
                        response -> response.then().statusCode(is(400)),
                        List.of(
                                CatalogFilterPayload::getSortBy,
                                CatalogFilterPayload::getKeyword,
                                CatalogFilterPayload::getOnlyActiveEservices,
                                CatalogFilterPayload::getSubscribedByRequester,
                                CatalogFilterPayload::getAsyncExchange,
                                CatalogFilterPayload::getMode,
                                CatalogFilterPayload::getTemplateId,
                                CatalogFilterPayload::getOnlySignalHubEnabled,
                                CatalogFilterPayload::getRequesterDelegationRoles,
                                CatalogFilterPayload::getProducersIds,
                                CatalogFilterPayload::getAvailableForRequester,
                                CatalogFilterPayload::getOnlyTemplateInstances,
                                CatalogFilterPayload::getHasLinkedPurposeTemplates,
                                CatalogFilterPayload::getProducerCategories,
                                p -> p.getRequesterDelegationRoles().get(0),
                                p -> p.getProducerCategories().get(0)
                        )
                )
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

