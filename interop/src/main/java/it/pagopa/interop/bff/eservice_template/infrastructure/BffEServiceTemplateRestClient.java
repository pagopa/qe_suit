package it.pagopa.interop.bff.eservice_template.infrastructure;

import it.pagopa.infrastructure.template.RestClient;
import it.pagopa.infrastructure.template.action.TestChain;
import it.pagopa.infrastructure.template.action.TestChainFactory;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.EserviceTemplatesApi;
import it.pagopa.interop.generated.openapi.clients.bff.api.EservicesApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedEServiceDescriptor;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedEServiceTemplateVersion;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateVersionDetails;
import it.pagopa.interop.generated.openapi.clients.bff.model.InstanceEServiceSeed;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.UUID;
import java.util.function.Function;

@Component
public class BffEServiceTemplateRestClient extends RestClient {

    private final EserviceTemplatesApi templatesApi;
    private final EservicesApi eservicesApi;

    public BffEServiceTemplateRestClient(TestChainFactory chainFactory, ApiClient apiClient) {
        super(chainFactory);
        this.templatesApi = apiClient.eserviceTemplates();
        this.eservicesApi = apiClient.eservices();
    }

    public TestChain<CreatedEServiceTemplateVersion> createEServiceTemplate(@Nonnull EServiceTemplateSeed payload) {
        return execute(
                () -> templatesApi.createEServiceTemplate().body(payload).execute(Function.identity()),
                CreatedEServiceTemplateVersion.class
        );
    }

    public TestChain<EServiceTemplateVersionDetails> readVersion(@Nonnull UUID templateId, @Nonnull UUID versionId) {
        return execute(
                () -> templatesApi.getEServiceTemplateVersion()
                        .eServiceTemplateIdPath(templateId)
                        .eServiceTemplateVersionIdPath(versionId)
                        .execute(Function.identity()),
                EServiceTemplateVersionDetails.class
        );
    }

    public TestChain<CreatedResource> addDocument(
            @Nonnull UUID templateId,
            @Nonnull UUID versionId,
            @Nonnull String documentKind,
            @Nonnull String documentName,
            @Nonnull File document
    ) {
        return execute(
                () -> templatesApi.createEServiceTemplateDocument()
                        .eServiceTemplateIdPath(templateId)
                        .eServiceTemplateVersionIdPath(versionId)
                        .kindForm(documentKind)
                        .prettyNameForm(documentName)
                        .reqSpec(reqSpec -> reqSpec.addMultiPart("doc", document, "application/octet-stream"))
                        .execute(Function.identity()),
                CreatedResource.class
        );
    }

    public TestChain<Void> publishVersion(@Nonnull UUID templateId, @Nonnull UUID versionId) {
        return execute(
                () -> templatesApi.publishEServiceTemplateVersion()
                        .eServiceTemplateIdPath(templateId)
                        .eServiceTemplateVersionIdPath(versionId)
                        .reqSpec(reqSpec -> reqSpec.setContentType("application/json"))
                        .execute(Function.identity()),
                Void.class
        );
    }

    public TestChain<CreatedEServiceDescriptor> instantiateEService(
            @Nonnull UUID templateId,
            @Nonnull InstanceEServiceSeed payload
    ) {
        return execute(
                () -> eservicesApi.createEServiceInstanceFromTemplate()
                        .templateIdPath(templateId)
                        .body(payload)
                        .execute(Function.identity()),
                CreatedEServiceDescriptor.class
        );
    }
}

