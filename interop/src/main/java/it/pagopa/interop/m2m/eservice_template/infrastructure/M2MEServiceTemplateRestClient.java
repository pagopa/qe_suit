package it.pagopa.interop.m2m.eservice_template.infrastructure;

import it.pagopa.infrastructure.template.RestClient;
import it.pagopa.infrastructure.template.action.TestChain;
import it.pagopa.infrastructure.template.action.TestChainFactory;
import it.pagopa.interop.generated.openapi.clients.m2m.v3.ApiClient;
import it.pagopa.interop.generated.openapi.clients.m2m.v3.api.EserviceTemplatesApi;
import it.pagopa.interop.generated.openapi.clients.m2m.v3.model.FileDownloadMultipart;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.function.Function;

@Component
public class M2MEServiceTemplateRestClient extends RestClient {

    private final EserviceTemplatesApi eServiceTemplatesApi;

    public M2MEServiceTemplateRestClient(TestChainFactory chainFactory, ApiClient apiClient) {
        super(chainFactory);
        this.eServiceTemplatesApi = apiClient.eserviceTemplates();
    }


    public TestChain<byte[]> downloadEServiceTemplateVersionInterface(
    // public TestChain<FileDownloadMultipart> downloadEServiceTemplateVersionInterface(
            @Nonnull UUID templateId,
            @Nonnull UUID versionId
    ) {

        var tmp = eServiceTemplatesApi.downloadEServiceTemplateVersionInterface()
                .templateIdPath(templateId)
                .versionIdPath(versionId);
        tmp.respSpec(r -> {

            r.expectStatusCode(200);
            r.expectHeader("Content-Type", "multipart/form-data");

        });

        return execute(
                () -> eServiceTemplatesApi
                        .downloadEServiceTemplateVersionInterface()
                        .templateIdPath(templateId)
                        .versionIdPath(versionId)
                        .execute(Function.identity()),
                // FileDownloadMultipart.class
                byte[].class
        );
    }
}
