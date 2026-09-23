package it.pagopa.interop.suite.contract;

import it.pagopa.infrastructure.contract.http.HttpContractValidator;
import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.bff.eservice_template.infrastructure.BffEServiceTemplateRequestFactory;
import it.pagopa.interop.bff.infrastructure.config.BffApiContractConfig;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedEServiceTemplateVersion;
import it.pagopa.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;

import static it.pagopa.utils.RandomUtils.randomAlphanumericName;

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
        // Workaround temporaneo: evita il passaggio journey che oggi invoca GET version
        // e fallisce su contract validation (campo creationDate mancante nella response).
        interopJourney.withProducer(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN);

        CreatedEServiceTemplateVersion createdTemplate = apiClient.eserviceTemplates()
                .createEServiceTemplate()
                .body(requestFactory.creationRequest())
                .executeAs(Function.identity());

        // Allinea la precondizione al journey standard: interfaccia collegata e versione pubblicata.
        apiClient.eserviceTemplates()
                .createEServiceTemplateDocument()
                .eServiceTemplateIdPath(createdTemplate.getId())
                .eServiceTemplateVersionIdPath(createdTemplate.getVersionId())
                .kindForm("INTERFACE")
                .prettyNameForm(randomAlphanumericName("interface", 12) + ".yaml")
                .reqSpec(reqSpec -> reqSpec.addMultiPart(
                        "doc",
                        FileUtils.loadClasspathResourceAsTempFile("assets/origin-interface.yaml"),
                        "application/octet-stream"
                ))
                .execute(Function.identity());

        apiClient.eserviceTemplates()
                .publishEServiceTemplateVersion()
                .eServiceTemplateIdPath(createdTemplate.getId())
                .eServiceTemplateVersionIdPath(createdTemplate.getVersionId())
                .execute(Function.identity());

        return createdTemplate.getId();

        /*
        EServiceTemplate eServiceTemplate = interopJourney
                .withProducer(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN)
                .createEServiceTemplate(
                        BffEServiceTemplateCreationCommand.from(requestFactory.creationRequest()),
                        EServiceTemplateVersionState.PUBLISHED
                )
                .get(EServiceTemplate.class);

        return eServiceTemplate.getId();
        */
    }
}

