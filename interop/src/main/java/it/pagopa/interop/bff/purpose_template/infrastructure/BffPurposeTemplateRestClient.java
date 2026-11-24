package it.pagopa.interop.bff.purpose_template.infrastructure;

import it.pagopa.infrastructure.template.RestClient;
import it.pagopa.infrastructure.template.action.TestChain;
import it.pagopa.infrastructure.template.action.TestChainFactory;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.PurposeTemplatesApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource;
import it.pagopa.interop.generated.openapi.clients.bff.model.LinkableResourceRequest;
import it.pagopa.interop.generated.openapi.clients.bff.model.LinkableResources;
import it.pagopa.interop.generated.openapi.clients.bff.model.LinkedResource;
import it.pagopa.interop.generated.openapi.clients.bff.model.PatchUpdatePurposeTemplateSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeTemplateSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeTemplate;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeTemplateWithCompactCreator;
import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisTemplateAnswerAnnotation;
import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisTemplateAnswerAnnotationDocument;
import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisTemplateAnswerAnnotationSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisTemplateAnswerRequest;
import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisTemplateAnswerResponse;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateRiskAnalysisTemplateAnswerAnnotationDocumentSeed;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Locale;
import java.util.UUID;
import java.util.function.Function;

@Component
public class BffPurposeTemplateRestClient extends RestClient {

    private final PurposeTemplatesApi purposeTemplatesApi;

    public BffPurposeTemplateRestClient(TestChainFactory chainFactory, ApiClient apiClient) {
        super(chainFactory);
        this.purposeTemplatesApi = apiClient.purposeTemplates();
    }

    public TestChain<PurposeTemplateWithCompactCreator> getPurposeTemplate(@Nonnull UUID purposeTemplateId) {
        return execute(
                () -> purposeTemplatesApi.getPurposeTemplate()
                        .purposeTemplateIdPath(purposeTemplateId)
                        .execute(Function.identity()),
                PurposeTemplateWithCompactCreator.class
        );
    }

    public TestChain<LinkableResources> getLinkableResources(@Nonnull UUID purposeTemplateId, int offset, int limit) {
        return execute(
                () -> purposeTemplatesApi.getPurposeTemplateLinkableResources()
                        .purposeTemplateIdPath(purposeTemplateId)
                        .offsetQuery(offset)
                        .limitQuery(limit)
                        .execute(Function.identity()),
                LinkableResources.class
        );
    }

    public TestChain<CreatedResource> createPurposeTemplate(@Nonnull PurposeTemplateSeed payload) {
        return execute(
                () -> purposeTemplatesApi.createPurposeTemplate().body(payload).execute(Function.identity()),
                CreatedResource.class
        );
    }

    public TestChain<PurposeTemplate> updatePurposeTemplate(
            @Nonnull UUID purposeTemplateId, @Nonnull PatchUpdatePurposeTemplateSeed payload) {
        return execute(
                () -> purposeTemplatesApi.patchUpdatePurposeTemplate()
                        .purposeTemplateIdPath(purposeTemplateId)
                        .body(payload)
                        .execute(Function.identity()),
                PurposeTemplate.class
        );
    }

    public TestChain<Void> publishPurposeTemplate(@Nonnull UUID purposeTemplateId) {
        return execute(
                () -> purposeTemplatesApi.publishPurposeTemplate()
                        .purposeTemplateIdPath(purposeTemplateId)
                        .reqSpec(reqSpec -> reqSpec.setContentType("application/json"))
                        .execute(Function.identity()),
                Void.class
        );
    }

    public TestChain<Void> suspendPurposeTemplate(@Nonnull UUID purposeTemplateId) {
        return execute(
                () -> purposeTemplatesApi.suspendPurposeTemplate()
                        .purposeTemplateIdPath(purposeTemplateId)
                        .reqSpec(reqSpec -> reqSpec.setContentType("application/json"))
                        .execute(Function.identity()),
                Void.class
        );
    }

    public TestChain<Void> archivePurposeTemplate(@Nonnull UUID purposeTemplateId) {
        return execute(
                () -> purposeTemplatesApi.archivePurposeTemplate()
                        .purposeTemplateIdPath(purposeTemplateId)
                        .reqSpec(reqSpec -> reqSpec.setContentType("application/x-www-form-urlencoded; charset=UTF-8"))
                        .execute(Function.identity()),
                Void.class
        );
    }

    public TestChain<LinkedResource> linkResource(@Nonnull UUID purposeTemplateId, @Nonnull LinkableResourceRequest payload) {
        return execute(
                () -> purposeTemplatesApi.linkResourceToPurposeTemplate()
                        .purposeTemplateIdPath(purposeTemplateId)
                        .body(payload)
                        .execute(Function.identity()),
                LinkedResource.class
        );
    }

    public TestChain<Void> unsuspendPurposeTemplate(@Nonnull UUID purposeTemplateId) {
        return execute(
                () -> purposeTemplatesApi.unsuspendPurposeTemplate()
                        .purposeTemplateIdPath(purposeTemplateId)
                        .reqSpec(reqSpec -> reqSpec.setContentType("application/json"))
                        .execute(Function.identity()),
                Void.class
        );
    }

    public TestChain<RiskAnalysisTemplateAnswerAnnotation> addRiskAnalysisAnswerAnnotation(
            @Nonnull UUID purposeTemplateId,
            @Nonnull UUID answerId,
            @Nonnull RiskAnalysisTemplateAnswerAnnotationSeed payload
    ) {
        return execute(
                () -> purposeTemplatesApi.addPurposeTemplateRiskAnalysisAnswerAnnotation()
                        .purposeTemplateIdPath(purposeTemplateId)
                        .answerIdPath(answerId)
                        .body(payload)
                        .execute(Function.identity()),
                RiskAnalysisTemplateAnswerAnnotation.class
        );
    }

    public TestChain<RiskAnalysisTemplateAnswerAnnotationDocument> addRiskAnalysisAnswerAnnotationDocument(
            @Nonnull UUID purposeTemplateId,
            @Nonnull UUID answerId,
            @Nonnull String prettyName,
            @Nonnull File doc
    ) {
        String mimeType = resolveMimeType(doc);
        return execute(
                () -> purposeTemplatesApi.addRiskAnalysisTemplateAnswerAnnotationDocument()
                        .purposeTemplateIdPath(purposeTemplateId)
                        .answerIdPath(answerId)
                        .prettyNameForm(prettyName)
                        .reqSpec(reqSpec -> reqSpec.addMultiPart("doc", doc, mimeType))
                        .execute(Function.identity()),
                RiskAnalysisTemplateAnswerAnnotationDocument.class
        );
    }

    public TestChain<Void> updatePurposeTemplate(
            @Nonnull UUID purposeTemplateId,
            @Nonnull PurposeTemplateSeed payload
    ) {
        return execute(
                () -> purposeTemplatesApi.updatePurposeTemplate()
                        .purposeTemplateIdPath(purposeTemplateId)
                        .body(payload)
                        .execute(Function.identity()),
                Void.class
        );
    }

    public TestChain<RiskAnalysisTemplateAnswerResponse> addRiskAnalysisAnswer(
            @Nonnull UUID purposeTemplateId,
            @Nonnull RiskAnalysisTemplateAnswerRequest payload
    ) {
        return execute(
                () -> purposeTemplatesApi.addPurposeTemplateRiskAnalysisAnswer()
                        .purposeTemplateIdPath(purposeTemplateId)
                        .body(payload)
                        .execute(Function.identity()),
                RiskAnalysisTemplateAnswerResponse.class
        );
    }

    public TestChain<RiskAnalysisTemplateAnswerAnnotationDocument> updateRiskAnalysisAnswerAnnotationDocument(
            @Nonnull UUID purposeTemplateId,
            @Nonnull UUID answerId,
            @Nonnull UUID documentId,
            @Nonnull UpdateRiskAnalysisTemplateAnswerAnnotationDocumentSeed payload
    ) {
        return execute(
                () -> purposeTemplatesApi.updateRiskAnalysisTemplateAnswerAnnotationDocument()
                        .purposeTemplateIdPath(purposeTemplateId)
                        .answerIdPath(answerId)
                        .documentIdPath(documentId)
                        .body(payload)
                        .execute(Function.identity()),
                RiskAnalysisTemplateAnswerAnnotationDocument.class
        );
    }

    public TestChain<byte[]> getRiskAnalysisTemplateSignedDocument(@Nonnull UUID purposeTemplateId) {
        return execute(
                () -> purposeTemplatesApi.getRiskAnalysisTemplateSignedDocument()
                        .purposeTemplateIdPath(purposeTemplateId)
                        .execute(Function.identity()),
                byte[].class
        );
    }

    private static String resolveMimeType(File doc) {
        String name = doc.getName().toLowerCase(Locale.ROOT);
        if (name.endsWith(".pdf")) {
            return "application/pdf";
        }
        if (name.endsWith(".txt")) {
            return "text/plain";
        }
        return "application/octet-stream";
    }
}
