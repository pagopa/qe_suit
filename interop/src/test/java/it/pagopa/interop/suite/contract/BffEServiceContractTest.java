package it.pagopa.interop.suite.contract;

import it.pagopa.infrastructure.fuzzing.FuzzScenario;
import it.pagopa.infrastructure.contract.http.TargetExpression;
import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.common.infrastructure.config.JunitEnvironmentProfiles;
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
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceMode;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTechnology;
import it.pagopa.interop.generated.openapi.clients.bff.model.PatchUpdateEServiceSeed;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Qualifier;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static it.pagopa.interop.bff.infrastructure.config.BffApiContractConfig.DEFAULT_SUCCESS_STATUS_CODE;
import static it.pagopa.utils.RandomUtils.randomAlphanumericName;
import static org.hamcrest.Matchers.is;

@ActiveProfiles(resolver = JunitEnvironmentProfiles.class)
@SpringBootTest(classes = {TestBootApp.class, JunitContextConfig.class, BffApiContractConfig.class})
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class BffEServiceContractTest {

    private final ApiClient apiClient;
    private final InteropHttpContractValidator httpContractValidator;
    private final InteropJourney interopJourney;
    private final BffEServiceRequestFactory requestFactory;

    public BffEServiceContractTest(ApiClient apiClient,
            @Qualifier("bffInteropHttpContractValidator") InteropHttpContractValidator httpContractValidator,
            InteropJourney interopJourney, BffEServiceRequestFactory requestFactory) {
        this.apiClient = apiClient;
        this.httpContractValidator = httpContractValidator;
        this.interopJourney = interopJourney;
        this.requestFactory = requestFactory;
    }

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
                                //CatalogFilterPayload::getRequesterDelegationRoles,
                                CatalogFilterPayload::getProducersIds,
                                CatalogFilterPayload::getAvailableForRequester,
                                CatalogFilterPayload::getOnlyTemplateInstances,
                                CatalogFilterPayload::getHasLinkedPurposeTemplates,
                                CatalogFilterPayload::getProducerCategories,
                                //p -> p.getRequesterDelegationRoles().get(0),
                                p -> p.getProducerCategories().get(0)
                        )
                )
                .tests();
    }

    @TestFactory
    Stream<DynamicTest> patchUpdateEServiceById() {
        return httpContractValidator
                .as(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN)
                .apiCall(() -> apiClient.eservices().patchUpdateEServiceById())
                .pathParams(() -> Map.of("eServiceId", createEService(EServiceDescriptorState.DRAFT).getId()))
                .payload(() -> metadataSeed()
                        .technology(EServiceTechnology.REST)
                        .mode(EServiceMode.DELIVER)
                        .isSignalHubEnabled(false)
                        .isConsumerDelegable(true)
                        .isClientAccessDelegable(true)
                        .personalData(false)
                        .asyncExchange(false))
                .targets(FuzzScenario.REMOVED, expectSuccess(), ALL_FIELDS)
                .targets(FuzzScenario.REPLACED_WITH_NULL, expectSuccess(), NULLABLE_FIELDS)
                .targets(FuzzScenario.REPLACED_WITH_NULL, expectValidationError(), NON_NULLABLE_FIELDS)
                .targets(INVALID_VALUE_SCENARIOS, expectValidationError(), ALL_FIELDS)
                .tests();
    }

    private static final List<FuzzScenario> INVALID_VALUE_SCENARIOS = List.of(
            FuzzScenario.REPLACED_WITH_EMPTY_STRING,
            FuzzScenario.REPLACED_WITH_BLANK_STRING,
            FuzzScenario.REPLACED_WITH_LONG_STRING,
            FuzzScenario.REPLACED_WITH_WRONG_TYPE_STRING,
            FuzzScenario.REPLACED_WITH_WRONG_TYPE_NUMBER,
            FuzzScenario.REPLACED_WITH_WRONG_TYPE_DECIMAL,
            FuzzScenario.REPLACED_WITH_ZERO,
            FuzzScenario.REPLACED_WITH_NEGATIVE_VALUE,
            FuzzScenario.REPLACED_WITH_MIN_VALUE,
            FuzzScenario.REPLACED_WITH_MAX_VALUE,
            FuzzScenario.REPLACED_WITH_UNKNOWN_ENUM
    );
    private static final TargetExpression<PatchUpdateEServiceSeed> NAME = PatchUpdateEServiceSeed::getName;
    private static final TargetExpression<PatchUpdateEServiceSeed> DESCRIPTION = PatchUpdateEServiceSeed::getDescription;
    private static final TargetExpression<PatchUpdateEServiceSeed> TECHNOLOGY = PatchUpdateEServiceSeed::getTechnology;
    private static final TargetExpression<PatchUpdateEServiceSeed> MODE = PatchUpdateEServiceSeed::getMode;
    private static final TargetExpression<PatchUpdateEServiceSeed> IS_SIGNAL_HUB_ENABLED = PatchUpdateEServiceSeed::getIsSignalHubEnabled;
    private static final TargetExpression<PatchUpdateEServiceSeed> IS_CONSUMER_DELEGABLE = PatchUpdateEServiceSeed::getIsConsumerDelegable;
    private static final TargetExpression<PatchUpdateEServiceSeed> IS_CLIENT_ACCESS_DELEGABLE = PatchUpdateEServiceSeed::getIsClientAccessDelegable;
    private static final TargetExpression<PatchUpdateEServiceSeed> PERSONAL_DATA = PatchUpdateEServiceSeed::getPersonalData;
    private static final TargetExpression<PatchUpdateEServiceSeed> ASYNC_EXCHANGE = PatchUpdateEServiceSeed::getAsyncExchange;

    private static final List<TargetExpression<PatchUpdateEServiceSeed>> METADATA_FIELDS = List.of(NAME, DESCRIPTION);
    private static final List<TargetExpression<PatchUpdateEServiceSeed>> OPERATIONAL_FIELDS = List.of(
            TECHNOLOGY, MODE, IS_SIGNAL_HUB_ENABLED, IS_CONSUMER_DELEGABLE, IS_CLIENT_ACCESS_DELEGABLE, PERSONAL_DATA, ASYNC_EXCHANGE
    );
    private static final List<TargetExpression<PatchUpdateEServiceSeed>> ALL_FIELDS =
            Stream.concat(METADATA_FIELDS.stream(), OPERATIONAL_FIELDS.stream()).toList();
    private static final List<TargetExpression<PatchUpdateEServiceSeed>> NULLABLE_FIELDS = List.of(PERSONAL_DATA, ASYNC_EXCHANGE);
    private static final List<TargetExpression<PatchUpdateEServiceSeed>> NON_NULLABLE_FIELDS =
            ALL_FIELDS.stream().filter(field -> !NULLABLE_FIELDS.contains(field)).toList();

    private PatchUpdateEServiceSeed metadataSeed() {
        return new PatchUpdateEServiceSeed()
                .name(randomAlphanumericName("contract", 20))
                .description(randomAlphanumericName("contract description", 20));
    }

    private static Consumer<Response> expectSuccess() {
        return response -> response.then().statusCode(is(DEFAULT_SUCCESS_STATUS_CODE));
    }

    private static Consumer<Response> expectValidationError() {
        return response -> response.then().statusCode(is(400));
    }

    private UUID createEServiceId(EServiceDescriptorState descriptorState) {
        return createEService(descriptorState).getId();
    }

    private EService createEService(EServiceDescriptorState descriptorState) {
        return interopJourney
                .withProducer(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN)
                .createEService(descriptorState)
                .get(EService.class);
    }
}
