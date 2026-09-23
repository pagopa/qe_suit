package it.pagopa.interop.suite.contract;

import io.restassured.response.Response;
import it.pagopa.infrastructure.contract.http.HttpContractValidator;
import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.bff.eservice_template.infrastructure.BffEServiceTemplateRequestFactory;
import it.pagopa.interop.bff.infrastructure.config.BffApiContractConfig;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import static it.pagopa.interop.bff.infrastructure.config.BffApiContractConfig.DEFAULT_SUCCESS_STATUS_CODE;
import static org.hamcrest.Matchers.is;

@Execution(ExecutionMode.CONCURRENT)
@SpringBootTest(classes = {TestBootApp.class, JunitContextConfig.class, BffApiContractConfig.class})
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class BffEServiceTemplateContractTest {

    private final ApiClient apiClient;
    private final HttpContractValidator httpContractValidator;
    private final InteropJourney interopJourney;
    private final BffEServiceTemplateRequestFactory requestFactory;

    @TestFactory
    Stream<DynamicTest> createEServiceTemplate() {
        return httpContractValidator
                .apiCall(() -> {
                    interopJourney.withProducer(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN);
                    return apiClient.eserviceTemplates().createEServiceTemplate();
                })
                .payload(requestFactory::creationRequest)
                .tests();
    }

    @TestFactory
    Stream<DynamicTest> updateEServiceTemplateName() {
        return httpContractValidator
                .apiCall(() -> {
                    interopJourney.withProducer(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN);
                    return apiClient.eserviceTemplates().updateEServiceTemplateName();
                })
                .pathParams(() -> Map.of("eServiceTemplateId", createPublishedEServiceTemplateId()))
                .payload(requestFactory::updateNameRequest)
                .tests();
    }

    @TestFactory
    Stream<DynamicTest> updateEServiceTemplateIntendedTarget() {
        return httpContractValidator
                .apiCall(() -> {
                    interopJourney.withProducer(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN);
                    return apiClient.eserviceTemplates().updateEServiceTemplateIntendedTarget();
                })
                .pathParams(() -> Map.of("eServiceTemplateId", createPublishedEServiceTemplateId()))
                .payload(requestFactory::updateIntendedTargetRequest)
                .tests();
    }

    @TestFactory
    Stream<DynamicTest> updateEServiceTemplateDescription() {
        return httpContractValidator
                .apiCall(() -> {
                    interopJourney.withProducer(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN);
                    return apiClient.eserviceTemplates().updateEServiceTemplateDescription();
                })
                .pathParams(() -> Map.of("eServiceTemplateId", createPublishedEServiceTemplateId()))
                .payload(requestFactory::updateDescriptionRequest)
                .tests();
    }

    private UUID createPublishedEServiceTemplateId() {
        interopJourney.withProducer(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN);

        Response creationResponse = apiClient.eserviceTemplates()
                .createEServiceTemplate()
                .body(requestFactory.creationRequest())
                .execute(value -> value);
        creationResponse.then().statusCode(is(DEFAULT_SUCCESS_STATUS_CODE));

        UUID eServiceTemplateId = creationResponse.jsonPath().getObject("id", UUID.class);
        UUID eServiceTemplateVersionId = creationResponse.jsonPath().getObject("versionId", UUID.class);

        Response publishResponse = apiClient.eserviceTemplates()
                .publishEServiceTemplateVersion()
                .eServiceTemplateIdPath(eServiceTemplateId)
                .eServiceTemplateVersionIdPath(eServiceTemplateVersionId)
                .execute(value -> value);
        publishResponse.then().statusCode(is(DEFAULT_SUCCESS_STATUS_CODE));

        return eServiceTemplateId;
    }
}

