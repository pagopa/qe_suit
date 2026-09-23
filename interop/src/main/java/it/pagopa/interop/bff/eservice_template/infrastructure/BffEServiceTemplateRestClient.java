package it.pagopa.interop.bff.eservice_template.infrastructure;

import it.pagopa.infrastructure.template.RestClient;
import it.pagopa.infrastructure.template.action.TestChain;
import it.pagopa.infrastructure.template.action.TestChainFactory;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.EserviceTemplatesApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedEServiceTemplateVersion;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateDescriptionUpdateSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateDetails;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateIntendedTargetUpdateSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateNameUpdateSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateVersionDetails;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceTemplateSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceTemplateVersionSeed;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.UUID;
import java.util.function.Function;

@Component
public class BffEServiceTemplateRestClient extends RestClient {

    private final EserviceTemplatesApi eServiceTemplatesApi;

    public BffEServiceTemplateRestClient(TestChainFactory chainFactory, ApiClient apiClient) {
        super(chainFactory);
        this.eServiceTemplatesApi = apiClient.eserviceTemplates();
    }

    public TestChain<CreatedEServiceTemplateVersion> createEServiceTemplate(@Nonnull EServiceTemplateSeed payload) {
        return execute(
                () -> eServiceTemplatesApi
                        .createEServiceTemplate()
                        .body(payload)
                        .execute(Function.identity()),
                CreatedEServiceTemplateVersion.class
        );
    }

    public TestChain<EServiceTemplateDetails> getEServiceTemplate(@Nonnull UUID templateId) {
        return execute(
                () -> eServiceTemplatesApi
                        .getEServiceTemplate()
                        .eServiceTemplateIdPath(templateId)
                        .execute(Function.identity()),
                EServiceTemplateDetails.class
        );
    }

    public TestChain<Void> updateEServiceTemplate(
            @Nonnull UUID templateId,
            @Nonnull UpdateEServiceTemplateSeed payload
    ) {
        return execute(
                () -> eServiceTemplatesApi
                        .updateEServiceTemplate()
                        .eServiceTemplateIdPath(templateId)
                        .body(payload)
                        .execute(Function.identity()),
                Void.class
        );
    }

    public TestChain<Void> updateTemplateName(
            @Nonnull UUID templateId,
            @Nonnull EServiceTemplateNameUpdateSeed payload
    ) {
        return execute(
                () -> eServiceTemplatesApi
                        .updateEServiceTemplateName()
                        .eServiceTemplateIdPath(templateId)
                        .body(payload)
                        .execute(Function.identity()),
                Void.class
        );
    }

    public TestChain<Void> updateTemplateIntendedTarget(
            @Nonnull UUID templateId,
            @Nonnull EServiceTemplateIntendedTargetUpdateSeed payload
    ) {
        return execute(
                () -> eServiceTemplatesApi
                        .updateEServiceTemplateIntendedTarget()
                        .eServiceTemplateIdPath(templateId)
                        .body(payload)
                        .execute(Function.identity()),
                Void.class
        );
    }

    public TestChain<Void> updateTemplateDescription(
            @Nonnull UUID templateId,
            @Nonnull EServiceTemplateDescriptionUpdateSeed payload
    ) {
        return execute(
                () -> eServiceTemplatesApi
                        .updateEServiceTemplateDescription()
                        .eServiceTemplateIdPath(templateId)
                        .body(payload)
                        .execute(Function.identity()),
                Void.class
        );
    }

    public TestChain<CreatedResource> createVersion(@Nonnull UUID templateId) {
        return execute(
                () -> eServiceTemplatesApi
                        .createEServiceTemplateVersion()
                        .eServiceTemplateIdPath(templateId)
                        .execute(Function.identity()),
                CreatedResource.class
        );
    }

    public TestChain<EServiceTemplateVersionDetails> getVersion(
            @Nonnull UUID templateId,
            @Nonnull UUID versionId
    ) {
        return execute(
                () -> eServiceTemplatesApi
                        .getEServiceTemplateVersion()
                        .eServiceTemplateIdPath(templateId)
                        .eServiceTemplateVersionIdPath(versionId)
                        .execute(Function.identity()),
                EServiceTemplateVersionDetails.class
        );
    }

    public TestChain<Void> publishVersion(@Nonnull UUID templateId, @Nonnull UUID versionId) {
        return execute(
                () -> eServiceTemplatesApi
                        .publishEServiceTemplateVersion()
                        .eServiceTemplateIdPath(templateId)
                        .eServiceTemplateVersionIdPath(versionId)
                        .execute(Function.identity()),
                Void.class
        );
    }

    public TestChain<Void> updateDraftVersion(
            @Nonnull UUID templateId,
            @Nonnull UUID versionId,
            @Nonnull UpdateEServiceTemplateVersionSeed payload
    ) {
        return execute(
                () -> eServiceTemplatesApi
                        .updateDraftTemplateVersion()
                        .eServiceTemplateIdPath(templateId)
                        .eServiceTemplateVersionIdPath(versionId)
                        .body(payload)
                        .execute(Function.identity()),
                Void.class
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
                () -> eServiceTemplatesApi
                        .createEServiceTemplateDocument()
                        .eServiceTemplateIdPath(templateId)
                        .eServiceTemplateVersionIdPath(versionId)
                        .kindForm(documentKind)
                        .prettyNameForm(documentName)
                        .reqSpec(reqSpec -> reqSpec.addMultiPart("doc", document, "application/octet-stream"))
                        .execute(Function.identity()),
                CreatedResource.class
        );
    }
}

