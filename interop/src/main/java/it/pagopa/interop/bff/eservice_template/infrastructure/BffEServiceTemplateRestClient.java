package it.pagopa.interop.bff.eservice_template.infrastructure;

import it.pagopa.infrastructure.template.RestClient;
import it.pagopa.infrastructure.template.action.TestChain;
import it.pagopa.infrastructure.template.action.TestChainFactory;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.EserviceTemplatesApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.UUID;
import java.util.function.Function;

@Component
public class BffEServiceTemplateRestClient extends RestClient {

    private final EserviceTemplatesApi eserviceTemplatesApi;

    public BffEServiceTemplateRestClient(TestChainFactory chainFactory, ApiClient apiClient) {
        super(chainFactory);
        this.eserviceTemplatesApi = apiClient.eserviceTemplates();
    }

    public TestChain<CreatedEServiceTemplateVersion> createEServiceTemplate(@Nonnull EServiceTemplateSeed payload) {
        return execute(
                () -> eserviceTemplatesApi.createEServiceTemplate().body(payload).execute(Function.identity()),
                CreatedEServiceTemplateVersion.class
        );
    }

    public TestChain<EServiceTemplateDetails> readEServiceTemplate(@Nonnull UUID eServiceTemplateId) {
        return execute(
                () -> eserviceTemplatesApi.getEServiceTemplate().eServiceTemplateIdPath(eServiceTemplateId).execute(Function.identity()),
                EServiceTemplateDetails.class
        );
    }

    public TestChain<EServiceTemplateVersionDetails> readVersion(@Nonnull UUID eServiceTemplateId, @Nonnull UUID eServiceTemplateVersionId) {
        return execute(
                () -> eserviceTemplatesApi.getEServiceTemplateVersion()
                        .eServiceTemplateIdPath(eServiceTemplateId)
                        .eServiceTemplateVersionIdPath(eServiceTemplateVersionId)
                        .execute(Function.identity()),
                EServiceTemplateVersionDetails.class
        );
    }

    public TestChain<Void> updateDraftVersion(@Nonnull UUID eServiceTemplateId, @Nonnull UUID eServiceTemplateVersionId, @Nonnull UpdateEServiceTemplateVersionSeed payload) {
        return execute(
                () -> eserviceTemplatesApi.updateDraftTemplateVersion()
                        .eServiceTemplateIdPath(eServiceTemplateId)
                        .eServiceTemplateVersionIdPath(eServiceTemplateVersionId)
                        .body(payload)
                        .execute(Function.identity()),
                Void.class
        );
    }

    public TestChain<Void> publishVersion(@Nonnull UUID eServiceTemplateId, @Nonnull UUID eServiceTemplateVersionId) {
        return execute(
                () -> eserviceTemplatesApi.publishEServiceTemplateVersion()
                        .eServiceTemplateIdPath(eServiceTemplateId)
                        .eServiceTemplateVersionIdPath(eServiceTemplateVersionId)
                        .reqSpec(reqSpec -> reqSpec.setContentType("application/json"))
                        .execute(Function.identity()),
                Void.class
        );
    }

    public TestChain<CreatedResource> addDocument(@Nonnull UUID eServiceTemplateId, @Nonnull UUID eServiceTemplateVersionId, @Nonnull String documentKind, @Nonnull String documentName, @Nonnull File document) {
        return execute(
                () -> eserviceTemplatesApi.createEServiceTemplateDocument()
                        .eServiceTemplateIdPath(eServiceTemplateId)
                        .eServiceTemplateVersionIdPath(eServiceTemplateVersionId)
                        .kindForm(documentKind)
                        .prettyNameForm(documentName)
                        .reqSpec(reqSpec ->
                                reqSpec.addMultiPart(
                                        "doc",
                                        document,
                                        "application/octet-stream"
                                )
                        )
                        .execute(Function.identity()),
                CreatedResource.class
        );
    }
}

