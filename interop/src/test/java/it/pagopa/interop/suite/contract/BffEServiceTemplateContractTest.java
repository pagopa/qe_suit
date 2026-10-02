package it.pagopa.interop.suite.contract;

import io.restassured.response.Response;
import it.pagopa.infrastructure.fuzzing.FuzzScenario;
import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersionState;
import it.pagopa.interop.bff.infrastructure.config.BffApiContractConfig;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.common.infrastructure.contract.InteropHttpContractValidator;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.EserviceTemplatesApi;
import it.pagopa.utils.FileUtils;
import it.pagopa.utils.RandomUtils;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.io.File;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * PIN-9480 / PR #3140 - upload of the interface document on an e-service template.
 */
@SpringBootTest(classes = {TestBootApp.class, JunitContextConfig.class, BffApiContractConfig.class})
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class BffEServiceTemplateContractTest {

    private static final String STANDARD_INTERFACE = "assets/origin-interface.yaml";
    // Fixture exceeding the YAML alias limit enforced by the BFF
    private static final String COMPLEX_INTERFACE = "openapi/test.yaml";

    private final ApiClient apiClient;
    private final InteropHttpContractValidator httpContractValidator;
    private final InteropJourney interopJourney;

    public BffEServiceTemplateContractTest(
            ApiClient apiClient,
            @Qualifier("bffInteropHttpContractValidator") InteropHttpContractValidator httpContractValidator,
            InteropJourney interopJourney
    ) {
        this.apiClient = apiClient;
        this.httpContractValidator = httpContractValidator;
        this.interopJourney = interopJourney;
    }

    @TestFactory
    Stream<DynamicTest> createEServiceTemplateDocument() {
        return httpContractValidator
                .as(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN)
                .apiCall(() -> {
                    EserviceTemplatesApi.CreateEServiceTemplateDocumentOper operation =
                            apiClient.eserviceTemplates().createEServiceTemplateDocument();
                    operation.kindForm("INTERFACE");
                    operation.prettyNameForm(RandomUtils.randomAlphanumericName("interface") + ".yaml");
                    operation.docMultiPart(FileUtils.loadClasspathResourceAsTempFile(STANDARD_INTERFACE));
                    return operation;
                })
                .pathParams(() -> {
                    EServiceTemplate template = createDraftTemplate();
                    return Map.of(
                            "eServiceTemplateId", template.getId(),
                            "eServiceTemplateVersionId", template.lastVersion().getId()
                    );
                })
                .scenario(FuzzScenario.REPLACED_WITH_NIL_UUID, response -> response.then().statusCode(400))
                .tests();
    }

    @Test
    void createEServiceTemplateDocumentWithComplexYamlIsRejected() {
        EServiceTemplate template = createDraftTemplate();
        File complexYaml = FileUtils.loadClasspathResourceAsTempFile(COMPLEX_INTERFACE);

        Response response = apiClient.eserviceTemplates()
                .createEServiceTemplateDocument()
                .eServiceTemplateIdPath(template.getId())
                .eServiceTemplateVersionIdPath(template.lastVersion().getId())
                .kindForm("INTERFACE")
                .prettyNameForm(RandomUtils.randomAlphanumericName("complex-interface") + ".yaml")
                .docMultiPart(complexYaml)
                .execute(Function.identity());

        response.then().statusCode(400);
    }

    private EServiceTemplate createDraftTemplate() {
        return interopJourney
                .withProducer(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN)
                .createEServiceTemplate(EServiceTemplateVersionState.DRAFT)
                .get(EServiceTemplate.class);
    }
}

