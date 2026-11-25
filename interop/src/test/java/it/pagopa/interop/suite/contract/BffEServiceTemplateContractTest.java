package it.pagopa.interop.suite.contract;

import it.pagopa.infrastructure.contract.http.TargetExpression;
import it.pagopa.infrastructure.fuzzing.FuzzScenario;
import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.common.infrastructure.config.JunitEnvironmentProfiles;
import it.pagopa.interop.bff.eservice_template.infrastructure.BffEServiceTemplateRequestFactory;
import it.pagopa.interop.bff.infrastructure.config.BffApiContractConfig;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersionState;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.common.infrastructure.contract.InteropHttpContractValidator;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceMode;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTechnology;
import it.pagopa.interop.generated.openapi.clients.bff.model.PatchUpdateEServiceTemplateSeed;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Qualifier;
import org.junit.jupiter.api.DynamicTest;
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
public class BffEServiceTemplateContractTest {

    private final ApiClient apiClient;
    private final InteropHttpContractValidator httpContractValidator;
    private final InteropJourney interopJourney;
    private final BffEServiceTemplateRequestFactory requestFactory;

    public BffEServiceTemplateContractTest(ApiClient apiClient,
            @Qualifier("bffInteropHttpContractValidator") InteropHttpContractValidator httpContractValidator,
            InteropJourney interopJourney, BffEServiceTemplateRequestFactory requestFactory) {
        this.apiClient = apiClient;
        this.httpContractValidator = httpContractValidator;
        this.interopJourney = interopJourney;
        this.requestFactory = requestFactory;
    }

    @TestFactory
    Stream<DynamicTest> createEServiceTemplate() {
        return httpContractValidator
                .as(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN)
                .apiCall(() -> apiClient.eserviceTemplates().createEServiceTemplate())
                .payload(requestFactory::creationRequest)
                .tests();
    }

    @TestFactory
    Stream<DynamicTest> updateEServiceTemplateName() {
        return httpContractValidator
                .as(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN)
                .apiCall(() -> apiClient.eserviceTemplates().updateEServiceTemplateName())
                .pathParams(() -> Map.of("eServiceTemplateId", createPublishedEServiceTemplateId()))
                .payload(requestFactory::updateNameRequest)
                .tests();
    }

    @TestFactory
    Stream<DynamicTest> updateEServiceTemplateIntendedTarget() {
        return httpContractValidator
                .as(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN)
                .apiCall(() -> apiClient.eserviceTemplates().updateEServiceTemplateIntendedTarget())
                .pathParams(() -> Map.of("eServiceTemplateId", createPublishedEServiceTemplateId()))
                .payload(requestFactory::updateIntendedTargetRequest)
                .tests();
    }

    @TestFactory
    Stream<DynamicTest> updateEServiceTemplateDescription() {
        return httpContractValidator
                .as(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN)
                .apiCall(() -> apiClient.eserviceTemplates().updateEServiceTemplateDescription())
                .pathParams(() -> Map.of("eServiceTemplateId", createPublishedEServiceTemplateId()))
                .payload(requestFactory::updateDescriptionRequest)
                .tests();
    }

    @TestFactory
    Stream<DynamicTest> patchUpdateEServiceTemplate() {
        return httpContractValidator
                .as(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN)
                .apiCall(() -> apiClient.eserviceTemplates().patchUpdateEServiceTemplate())
                .pathParams(() -> Map.of("eServiceTemplateId", createDraftEServiceTemplateId()))
                .payload(() -> metadataSeed()
                        .technology(EServiceTechnology.REST)
                        .mode(EServiceMode.DELIVER)
                        .isSignalHubEnabled(false)
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
    private static final TargetExpression<PatchUpdateEServiceTemplateSeed> NAME = PatchUpdateEServiceTemplateSeed::getName;
    private static final TargetExpression<PatchUpdateEServiceTemplateSeed> INTENDED_TARGET = PatchUpdateEServiceTemplateSeed::getIntendedTarget;
    private static final TargetExpression<PatchUpdateEServiceTemplateSeed> DESCRIPTION = PatchUpdateEServiceTemplateSeed::getDescription;
    private static final TargetExpression<PatchUpdateEServiceTemplateSeed> TECHNOLOGY = PatchUpdateEServiceTemplateSeed::getTechnology;
    private static final TargetExpression<PatchUpdateEServiceTemplateSeed> MODE = PatchUpdateEServiceTemplateSeed::getMode;
    private static final TargetExpression<PatchUpdateEServiceTemplateSeed> IS_SIGNAL_HUB_ENABLED = PatchUpdateEServiceTemplateSeed::getIsSignalHubEnabled;
    private static final TargetExpression<PatchUpdateEServiceTemplateSeed> PERSONAL_DATA = PatchUpdateEServiceTemplateSeed::getPersonalData;
    private static final TargetExpression<PatchUpdateEServiceTemplateSeed> ASYNC_EXCHANGE = PatchUpdateEServiceTemplateSeed::getAsyncExchange;

    private static final List<TargetExpression<PatchUpdateEServiceTemplateSeed>> METADATA_FIELDS = List.of(NAME, INTENDED_TARGET, DESCRIPTION);
    private static final List<TargetExpression<PatchUpdateEServiceTemplateSeed>> OPERATIONAL_FIELDS = List.of(
            TECHNOLOGY, MODE, IS_SIGNAL_HUB_ENABLED, PERSONAL_DATA, ASYNC_EXCHANGE
    );
    private static final List<TargetExpression<PatchUpdateEServiceTemplateSeed>> ALL_FIELDS =
            Stream.concat(METADATA_FIELDS.stream(), OPERATIONAL_FIELDS.stream()).toList();
    private static final List<TargetExpression<PatchUpdateEServiceTemplateSeed>> NULLABLE_FIELDS = List.of(PERSONAL_DATA, ASYNC_EXCHANGE);
    private static final List<TargetExpression<PatchUpdateEServiceTemplateSeed>> NON_NULLABLE_FIELDS =
            ALL_FIELDS.stream().filter(field -> !NULLABLE_FIELDS.contains(field)).toList();

    private PatchUpdateEServiceTemplateSeed metadataSeed() {
        return new PatchUpdateEServiceTemplateSeed()
                .name(randomAlphanumericName("contract", 20))
                .intendedTarget(randomAlphanumericName("contract intended target", 20))
                .description(randomAlphanumericName("contract description", 20));
    }

    private static Consumer<Response> expectSuccess() {
        return response -> response.then().statusCode(is(DEFAULT_SUCCESS_STATUS_CODE));
    }

    private static Consumer<Response> expectValidationError() {
        return response -> response.then().statusCode(is(400));
    }

    private UUID createDraftEServiceTemplateId() {
        EServiceTemplate eServiceTemplate = interopJourney
                .withProducer(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN)
                .createEServiceTemplate()
                .get(EServiceTemplate.class);

        return eServiceTemplate.getId();
    }

    private UUID createPublishedEServiceTemplateId() {
        EServiceTemplate eServiceTemplate = interopJourney
                .withProducer(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN)
                .createEServiceTemplate(EServiceTemplateVersionState.PUBLISHED)
                .get(EServiceTemplate.class);

        return eServiceTemplate.getId();
    }
}
