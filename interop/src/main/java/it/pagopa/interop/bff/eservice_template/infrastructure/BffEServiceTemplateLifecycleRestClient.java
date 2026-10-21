package it.pagopa.interop.bff.eservice_template.infrastructure;

import it.pagopa.infrastructure.template.RestClient;
import it.pagopa.infrastructure.template.action.TestChain;
import it.pagopa.infrastructure.template.action.TestChainFactory;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.EserviceTemplatesApi;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.function.Function;

@Component
public class BffEServiceTemplateLifecycleRestClient extends RestClient {
    private final EserviceTemplatesApi api;

    public BffEServiceTemplateLifecycleRestClient(TestChainFactory factory, ApiClient client) {
        super(factory);
        api = client.eserviceTemplates();
    }

    public TestChain<Void> deleteDraftVersion(UUID templateId, UUID versionId) {
        return execute(() -> api.deleteDraftTemplateVersion()
                .eServiceTemplateIdPath(templateId)
                .eServiceTemplateVersionIdPath(versionId)
                .execute(Function.identity()), Void.class);
    }

    public TestChain<Void> suspendVersion(UUID templateId, UUID versionId) {
        return execute(() -> api.suspendEServiceTemplateVersion()
                .eServiceTemplateIdPath(templateId)
                .eServiceTemplateVersionIdPath(versionId)
                .reqSpec(reqSpec -> reqSpec.setContentType("application/json"))
                .execute(Function.identity()), Void.class);
    }
}
