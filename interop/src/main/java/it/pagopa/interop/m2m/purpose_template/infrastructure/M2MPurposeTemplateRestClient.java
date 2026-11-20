package it.pagopa.interop.m2m.purpose_template.infrastructure;

import it.pagopa.infrastructure.template.RestClient;
import it.pagopa.infrastructure.template.action.TestChain;
import it.pagopa.infrastructure.template.action.TestChainFactory;
import it.pagopa.interop.generated.openapi.clients.m2m.v3.ApiClient;
import it.pagopa.interop.generated.openapi.clients.m2m.v3.api.PurposeTemplatesApi;
import it.pagopa.interop.generated.openapi.clients.m2m.v3.model.PurposeTemplate;
import it.pagopa.interop.generated.openapi.clients.m2m.v3.model.PurposeTemplateDraftUpdateSeed;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.function.Function;

@Component
public class M2MPurposeTemplateRestClient extends RestClient {

    private final PurposeTemplatesApi purposeTemplatesApi;

    public M2MPurposeTemplateRestClient(TestChainFactory chainFactory, ApiClient apiClient) {
        super(chainFactory);
        this.purposeTemplatesApi = apiClient.purposeTemplates();
    }

    public TestChain<PurposeTemplate> getPurposeTemplate(UUID purposeTemplateId) {
        return execute(
                () -> purposeTemplatesApi.getPurposeTemplate()
                        .purposeTemplateIdPath(purposeTemplateId)
                        .execute(Function.identity()),
                PurposeTemplate.class
        );
    }

    public TestChain<PurposeTemplate> updatePurposeTemplate(
            UUID purposeTemplateId, PurposeTemplateDraftUpdateSeed payload) {
        return execute(
                () -> purposeTemplatesApi.updateDraftPurposeTemplate()
                        .purposeTemplateIdPath(purposeTemplateId)
                        .reqSpec(reqSpec -> reqSpec.setContentType(
                                "application/merge-patch+json; charset=UTF-8"))
                        .body(payload)
                        .execute(Function.identity()),
                PurposeTemplate.class
        );
    }
}
