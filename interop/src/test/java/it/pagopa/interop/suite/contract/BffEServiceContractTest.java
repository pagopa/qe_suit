package it.pagopa.interop.suite.contract;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.filter.Filter;
import io.restassured.response.Response;
import it.pagopa.infrastructure.fuzzing.FuzzScenario;
import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.bff.eservice.infrastructure.BffEServiceRequestFactory;
import it.pagopa.interop.bff.infrastructure.config.BffApiContractConfig;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersionState;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.common.infrastructure.contract.InteropHttpContractValidator;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.model.CatalogFilterPayload;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static it.pagopa.interop.bff.infrastructure.config.BffApiContractConfig.DEFAULT_SUCCESS_STATUS_CODE;
import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

@SpringBootTest(classes = {TestBootApp.class, JunitContextConfig.class, BffApiContractConfig.class})
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class BffEServiceContractTest {

    private final ApiClient apiClient;
    private final InteropHttpContractValidator httpContractValidator;
    private final InteropJourney interopJourney;
    private final BffEServiceRequestFactory requestFactory;
    private final CurrentUserSession currentUserSession;
    private final ObjectMapper objectMapper;

    private static final int CONFLICT_MAX_RETRIES = 3;
    private static final long CONFLICT_RETRY_MIN_DELAY_MS = 1000;
    private static final long CONFLICT_RETRY_MAX_DELAY_MS = 4000;

    // Aggiornamenti ravvicinati della stessa bozza possono restituire 409: si ritenta prima di accettare il fallimento.
    static final Filter RETRY_ON_CONFLICT = (requestSpec, responseSpec, ctx) -> {
        String method = requestSpec.getMethod();
        String path = requestSpec.getUserDefinedPath();
        Response response = ctx.next(requestSpec, responseSpec);
        for (int attempt = 0; attempt < CONFLICT_MAX_RETRIES && response.statusCode() == 409; attempt++) {
            try {
                Thread.sleep(ThreadLocalRandom.current().nextLong(CONFLICT_RETRY_MIN_DELAY_MS, CONFLICT_RETRY_MAX_DELAY_MS + 1));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Interrupted while retrying a draft descriptor update", e);
            }
            // ctx.next() consuma la catena: ogni retry ne richiede una nuova, senza questo filtro.
            response = RestAssured.given()
                    .spec(requestSpec)
                    .noFiltersOfType(BffEServiceContractTest.RETRY_ON_CONFLICT.getClass())
                    .request(method, path);
        }
        return response;
    };

    private static final List<String> ATTRIBUTE_GROUPS = List.of("certified", "declared", "verified");

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
                    operation.reqSpec(reqSpec -> reqSpec.addFilter(RETRY_ON_CONFLICT));
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

        currentUserSession.set(User.getTenantUser(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN), Tenant.COMUNE_DI_MILANO);
        return apiClient.eservices().updateDraftDescriptorTemplateInstance()
                .eServiceIdPath(draftInstance.getId())
                .descriptorIdPath(draftInstance.getLatestDescriptor().getId())
                .reqSpec(reqSpec -> reqSpec.setContentType("application/json").setBody(payload).addFilter(RETRY_ON_CONFLICT))
                .execute(Function.identity());
    }

    private EService createDraftEServiceTemplateInstance() {
        return interopJourney
                .withProducer(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN)
                .createEServiceTemplate(EServiceTemplateVersionState.PUBLISHED)
                .createEServiceInstanceTemplate()
                .get(EService.class);
    }

    private UUID createEServiceId(EServiceDescriptorState descriptorState) {
        EService createdEService = interopJourney
                .withProducer(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN)
                .createEService(descriptorState)
                .get(EService.class);

        return createdEService.getId();
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
