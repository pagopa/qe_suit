package it.pagopa.interop.m2m.purpose_template.infrastructure;

import it.pagopa.application.context.EntityStore;
import it.pagopa.application.context.LastApiResponseStore;
import it.pagopa.infrastructure.response.ApiResponse;
import it.pagopa.infrastructure.template.action.strategy.PollingStrategy;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import it.pagopa.interop.common.kernel.domain.EServiceTemplateRef;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.TenantKind;
import it.pagopa.interop.common.purpose_template.application.PurposeTemplateGateway;
import it.pagopa.interop.common.purpose_template.domain.PurposeTemplate;
import it.pagopa.interop.common.risk_analysis.domain.RiskAnalysisForm;
import it.pagopa.interop.common.risk_analysis.domain.RiskAnalysisFormConfig;
import it.pagopa.interop.generated.openapi.clients.m2m.v3.model.PurposeTemplateDraftUpdateSeed;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Service
@RequiredArgsConstructor
public class M2MPurposeTemplateGateway implements PurposeTemplateGateway {

    private final M2MPurposeTemplateRestClient restClient;
    private final M2MPurposeTemplateMapper mapper;
    private final EntityStore entityStore;
    private final LastApiResponseStore lastApiResponseStore;

    @Override
    public void shouldFailToUpdateMetadataAsMalformed() {
        assertThat(lastApiResponseStore.getLastResponse())
                .as("Risposta al tentativo di modifica dei metadati del template finalità")
                .isNotNull()
                .extracting(ApiResponse::getStatusCode)
                .as("Modifica dei metadati del template finalità malformata")
                .isEqualTo(400);
    }

    @Override
    public void shouldFailToUpdateMetadataAsForbidden() {
        assertThat(lastApiResponseStore.getLastResponse())
                .as("Risposta al tentativo di modifica dei metadati del template finalità")
                .isNotNull()
                .extracting(ApiResponse::getStatusCode)
                .as("Modifica dei metadati del template finalità vietata")
                .isEqualTo(403);
    }

    @Override
    public void shouldFailToUpdateMetadataAsNotFound() {
        assertThat(lastApiResponseStore.getLastResponse())
                .as("Risposta al tentativo di modifica dei metadati del template finalità")
                .isNotNull()
                .extracting(ApiResponse::getStatusCode)
                .as("Modifica dei metadati del template finalità per risorsa non trovata")
                .isEqualTo(404);
    }

    @Override
    public void shouldFailToUpdateMetadataAsConflict() {
        assertThat(lastApiResponseStore.getLastResponse())
                .as("Risposta al tentativo di modifica dei metadati del template finalità")
                .isNotNull()
                .extracting(ApiResponse::getStatusCode)
                .as("Modifica dei metadati del template finalità in conflitto")
                .isEqualTo(409);
    }

    @Override
    public PurposeTemplate getPurposeTemplate(UUID purposeTemplateId) {
        return restClient.getPurposeTemplate(purposeTemplateId)
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(details -> mapper.toPurposeTemplate(
                        details, entityStore.getById(purposeTemplateId, PurposeTemplate.class).orElse(null)))
                .updateContext()
                .get();
    }

    @Override
    public PurposeTemplate updateMetadata(
            PurposeTemplate purposeTemplate,
            String name,
            String description,
            String targetDescription,
            Boolean purposeIsFreeOfCharge,
            Integer purposeDailyCalls,
            Boolean handlesPersonalData) {
        PurposeTemplateDraftUpdateSeed payload = new PurposeTemplateDraftUpdateSeed()
                .purposeTitle(name)
                .purposeDescription(description)
                .targetDescription(targetDescription);
        if (purposeIsFreeOfCharge != null) {
            payload.purposeIsFreeOfCharge(purposeIsFreeOfCharge);
        }
        if (purposeDailyCalls != null) {
            payload.purposeDailyCalls(purposeDailyCalls);
        }
        if (handlesPersonalData != null) {
            payload.handlesPersonalData(handlesPersonalData);
        }

        return restClient.updatePurposeTemplate(purposeTemplate.getId(), payload)
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(ignored -> getPurposeTemplate(purposeTemplate.getId()))
                .updateContext()
                .get();
    }

    @Override
    public void tryToUpdateMetadata(
            PurposeTemplate purposeTemplate,
            String name,
            String description,
            String targetDescription,
            Boolean purposeIsFreeOfCharge,
            Integer purposeDailyCalls,
            Boolean handlesPersonalData) {
        PurposeTemplateDraftUpdateSeed payload = new PurposeTemplateDraftUpdateSeed()
                .purposeTitle(name)
                .purposeDescription(description)
                .targetDescription(targetDescription);
        if (purposeIsFreeOfCharge != null) {
            payload.purposeIsFreeOfCharge(purposeIsFreeOfCharge);
        }
        if (purposeDailyCalls != null) {
            payload.purposeDailyCalls(purposeDailyCalls);
        }
        if (handlesPersonalData != null) {
            payload.handlesPersonalData(handlesPersonalData);
        }

        restClient.updatePurposeTemplate(purposeTemplate.getId(), payload)
                .withoutPolling();
    }

    @Override
    public PurposeTemplate createPurposeTemplate(
            Tenant targetTenant, RiskAnalysisForm riskAnalysisForm, RiskAnalysisFormConfig config) {
        throw unsupportedOperation();
    }

    @Override
    public PurposeTemplate createPurposeTemplate(Tenant targetTenant, RiskAnalysisForm riskAnalysisForm) {
        throw unsupportedOperation();
    }

    @Override
    public PurposeTemplate publishPurposeTemplate(PurposeTemplate purposeTemplate) {
        throw unsupportedOperation();
    }

    @Override
    public PurposeTemplate suspendPurposeTemplate(PurposeTemplate purposeTemplate) {
        throw unsupportedOperation();
    }

    @Override
    public PurposeTemplate archivePurposeTemplate(PurposeTemplate purposeTemplate) {
        throw unsupportedOperation();
    }

    @Override
    public PurposeTemplate linkEService(PurposeTemplate purposeTemplate, EServiceRef eServiceRef) {
        throw unsupportedOperation();
    }

    @Override
    public PurposeTemplate linkEServiceTemplate(PurposeTemplate purposeTemplate, EServiceTemplateRef templateRef) {
        throw unsupportedOperation();
    }

    @Override
    public void attemptPurposeTemplateReactivation(PurposeTemplate purposeTemplate) {
        throw unsupportedOperation();
    }

    @Override
    public void attemptPurposeTemplatePublication(PurposeTemplate purposeTemplate) {
        throw unsupportedOperation();
    }

    @Override
    public PurposeTemplate addRiskAnalysisAnswerAnnotation(PurposeTemplate purposeTemplate,
        UUID answerId) {
        throw unsupportedOperation();
    }

    @Override
    public PurposeTemplate addRiskAnalysisAnswerAnnotationDocument(PurposeTemplate purposeTemplate,
        UUID answerId, String prettyName, String fixturePath) {
        throw unsupportedOperation();
    }

    @Override
    public void awaitSignedRiskAnalysisDocumentAvailable(PurposeTemplate purposeTemplate) {
        throw unsupportedOperation();
    }

    @Override
    public void attemptRiskAnalysisAnswerAnnotationDocumentUpload(PurposeTemplate purposeTemplate,
        UUID answerId, String prettyName, String fixturePath) {
        throw unsupportedOperation();
    }

    @Override
    public void attemptLinkEService(PurposeTemplate purposeTemplate, EServiceRef eServiceRef) {
        throw unsupportedOperation();
    }

    @Override
    public void attemptUpdatePurposeTemplate(PurposeTemplate purposeTemplate,
        TenantKind targetTenantKind, RiskAnalysisForm riskAnalysisForm) {
        throw unsupportedOperation();
    }

    @Override
    public void attemptAddRiskAnalysisAnswerWithAnnotation(PurposeTemplate purposeTemplate,
        String annotationText) {
        throw unsupportedOperation();
    }

    @Override
    public void attemptUpdateRiskAnalysisAnswerAnnotation(PurposeTemplate purposeTemplate,
        UUID answerId, String annotationText) {
        throw unsupportedOperation();
    }

    @Override
    public void attemptRenameRiskAnalysisAnswerAnnotationDocument(PurposeTemplate purposeTemplate,
        UUID answerId, UUID documentId, String newPrettyName) {
        throw unsupportedOperation();
    }

    @Override
    public void attemptSignedRiskAnalysisDocumentDownload(PurposeTemplate purposeTemplate) {
        throw unsupportedOperation();
    }

    @Override
    public boolean supports(Channel channel) {
        return channel == Channel.M2M_V3;
    }

    private static UnsupportedOperationException unsupportedOperation() {
        return new UnsupportedOperationException("This purpose template operation is not supported on M2M v3");
    }
}
