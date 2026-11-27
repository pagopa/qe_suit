package it.pagopa.interop.suite.contract;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import it.pagopa.infrastructure.fuzzing.FuzzScenario;
import it.pagopa.infrastructure.contract.http.TargetExpression;
import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.common.infrastructure.config.JunitEnvironmentProfiles;
import it.pagopa.interop.bff.eservice.infrastructure.BffEServiceRequestFactory;
import it.pagopa.interop.bff.infrastructure.config.BffApiContractConfig;
import it.pagopa.interop.bff.infrastructure.config.BffRestApiClientConfig;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersionState;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.common.infrastructure.contract.InteropHttpContractValidator;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.context.AuthSession;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.model.CatalogFilterPayload;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceMode;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTechnology;
import it.pagopa.interop.generated.openapi.clients.bff.model.PatchUpdateEServiceSeed;
import org.springframework.beans.factory.annotation.Qualifier;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.function.Consumer;

import static it.pagopa.interop.bff.infrastructure.config.BffApiContractConfig.DEFAULT_SUCCESS_STATUS_CODE;
import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static it.pagopa.utils.RandomUtils.randomAlphanumericName;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

@ActiveProfiles(resolver = JunitEnvironmentProfiles.class)
@SpringBootTest(classes = {TestBootApp.class, JunitContextConfig.class, BffApiContractConfig.class})
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class BffEServiceContractTest {

    private final ApiClient apiClient;
    private final InteropHttpContractValidator httpContractValidator;
    private final InteropJourney interopJourney;
    private final BffEServiceRequestFactory requestFactory;
    private final AuthSession authSession;
    private final ObjectMapper objectMapper;

    private static final List<String> ATTRIBUTE_GROUPS = List.of("certified", "declared", "verified");

    public BffEServiceContractTest(ApiClient apiClient,
            @Qualifier("bffInteropHttpContractValidator") InteropHttpContractValidator httpContractValidator,
            InteropJourney interopJourney, BffEServiceRequestFactory requestFactory,
            @Qualifier("userAuthSession") AuthSession authSession, ObjectMapper objectMapper) {
        this.apiClient = apiClient;
        this.httpContractValidator = httpContractValidator;
        this.interopJourney = interopJourney;
        this.requestFactory = requestFactory;
        this.authSession = authSession;
        this.objectMapper = objectMapper;
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
    Stream<DynamicTest> updateDraftDescriptorTemplateInstance() {
        Supplier<EService> draftInstance = new LazyReference<>(this::createDraftEServiceTemplateInstance);

        Stream<DynamicTest> fuzzTests = httpContractValidator
                .as(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN)
                .apiCall(() -> {
                    var operation = apiClient.eservices().updateDraftDescriptorTemplateInstance();
                    operation.reqSpec(reqSpec -> reqSpec.addFilter(BffRestApiClientConfig.RETRY_ON_CONFLICT));
                    return operation;
                })
                .pathParams(() -> {
                    EService instance = draftInstance.get();
                    return Map.of(
                            "eServiceId", instance.getId(),
                            "descriptorId", instance.getLatestDescriptor().getId()
                    );
                })
                .payload(requestFactory::updateDraftTemplateInstanceDescriptorRequest)
                .tests();

        return Stream.concat(fuzzTests, draftInstanceAttributesTests(draftInstance));
    }

    private Stream<DynamicTest> draftInstanceAttributesTests(Supplier<EService> draftInstance) {
        return ATTRIBUTE_GROUPS.stream().flatMap(group -> Stream.of(
                dynamicTest(
                        "[attributes] " + group + " empty list",
                        () -> callUpdateDraftWithAttribute(draftInstance.get(), group, new ArrayList<>())
                                .then().statusCode(is(DEFAULT_SUCCESS_STATUS_CODE))
                ),
                dynamicTest(
                        "[attributes] " + group + " null",
                        () -> callUpdateDraftWithAttribute(draftInstance.get(), group, null)
                                .then().statusCode(is(400))
                ),
                dynamicTest(
                        "[attributes] " + group + " list containing an empty list",
                        () -> callUpdateDraftWithAttribute(draftInstance.get(), group, List.of(List.of()))
                                .then().statusCode(is(400))
                                .body(containsStringIgnoringCase("attribute"))
                )
        ));
    }

    private Response callUpdateDraftWithAttribute(EService draftInstance, String group, Object value) {
        Map<String, Object> payload = objectMapper.convertValue(
                requestFactory.updateDraftTemplateInstanceDescriptorRequest(), new TypeReference<>() {}
        );
        Map<String, Object> attributes = new HashMap<>();
        ATTRIBUTE_GROUPS.forEach(name -> attributes.put(name, new ArrayList<>()));
        attributes.put(group, value);
        payload.put("attributes", attributes);

        authSession.set(User.getTenantUser(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN), Tenant.COMUNE_DI_MILANO);
        return apiClient.eservices().updateDraftDescriptorTemplateInstance()
                .eServiceIdPath(draftInstance.getId())
                .descriptorIdPath(draftInstance.getLatestDescriptor().getId())
                .reqSpec(reqSpec -> reqSpec.setContentType("application/json").setBody(payload).addFilter(BffRestApiClientConfig.RETRY_ON_CONFLICT))
                .execute(Function.identity());
    }

    private EService createDraftEServiceTemplateInstance() {
        return interopJourney
                .withProducer(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN)
                .createEServiceTemplate(EServiceTemplateVersionState.PUBLISHED)
                .createEServiceInstanceTemplate()
                .get(EService.class);
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

    /* FIXME 17/11/2026: versione che si conclude con tutti i test in OK. La si tiene
    *   per permettere il paragone con la versione senza override, posta sotto, al termine
    *   del quale verrà formulata la versione definitiva. */
    /*@TestFactory
    Stream<DynamicTest> getCompactCatalogEServices() {
        return httpContractValidator
                .as(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN)
                .apiCall(() -> apiClient.eservices().getCompactCatalogEServices())
                .queryParams(requestFactory::compactCatalogRequest)
                .targets(
                        FuzzScenario.REMOVED,
                        response -> response.then().statusCode(is(DEFAULT_SUCCESS_STATUS_CODE)),
                        List.of(
                                BffCompactCatalogEServicesQueryParams::getQ,
                                BffCompactCatalogEServicesQueryParams::getStates,
                                BffCompactCatalogEServicesQueryParams::getAgreementStates,
                                BffCompactCatalogEServicesQueryParams::getMode,
                                BffCompactCatalogEServicesQueryParams::getIsConsumerDelegable,
                                BffCompactCatalogEServicesQueryParams::getPersonalData,
                                p -> p.getStates().get(0),
                                p -> p.getAgreementStates().get(0)
                        )
                )
                .targets(
                        FuzzScenario.REPLACED_WITH_EMPTY_STRING,
                        response -> response.then().statusCode(is(DEFAULT_SUCCESS_STATUS_CODE)),
                        List.of(
                                p -> p.getStates().get(0),
                                p -> p.getAgreementStates().get(0)
                        )
                )
                .targets(
                        FuzzScenario.REPLACED_WITH_LONG_STRING,
                        response -> response.then().statusCode(is(403)),
                        List.of(BffCompactCatalogEServicesQueryParams::getQ)
                )
                .targets(
                        FuzzScenario.REPLACED_WITH_ZERO,
                        response -> response.then().statusCode(is(DEFAULT_SUCCESS_STATUS_CODE)),
                        List.of(BffCompactCatalogEServicesQueryParams::getOffset)
                )
                .targets(
                        FuzzScenario.REPLACED_WITH_MAX_VALUE,
                        response -> response.then().statusCode(is(DEFAULT_SUCCESS_STATUS_CODE)),
                        List.of(BffCompactCatalogEServicesQueryParams::getOffset)
                )
                .targets(
                        List.of(
                                FuzzScenario.REPLACED_WITH_NULL,
                                FuzzScenario.REPLACED_WITH_EMPTY_STRING,
                                FuzzScenario.REPLACED_WITH_BLANK_STRING,
                                FuzzScenario.REPLACED_WITH_URL_INJECTION,
                                FuzzScenario.REPLACED_WITH_SQL_INJECTION,
                                FuzzScenario.REPLACED_WITH_WRONG_TYPE_NUMBER
                        ),
                        response -> response.then().statusCode(is(DEFAULT_SUCCESS_STATUS_CODE)),
                        List.of(BffCompactCatalogEServicesQueryParams::getQ)
                )
                .tests();
    }*/

    /* FIXME 17/11/2026: versione non definitiva, gli effettivi override verranno scritti
        al termine del paragone con gli esiti della versione di sopra. */
    @Tag("private-compact-catalog")
    @TestFactory
    Stream<DynamicTest> getCompactCatalogEServices() {
        return httpContractValidator
            .as(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN)
            .apiCall(() -> apiClient.eservices().getCompactCatalogEServices())
            .queryParams(requestFactory::compactCatalogRequest)
            .tests();
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

    private static final class LazyReference<T> implements Supplier<T> {
        private final Supplier<T> factory;
        private T value;

        private LazyReference(Supplier<T> factory) {
            this.factory = factory;
        }

        @Override
        public synchronized T get() {
            if (value == null) {
                value = factory.get();
            }
            return value;
        }
    }
}
