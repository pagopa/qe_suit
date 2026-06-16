package it.pagopa.interop.bff.infrastructure.client;

import it.pagopa.interop.common.contract.model.Purpose;
import it.pagopa.interop.generated.openapi.clients.bff.api.PurposesApi;
import it.pagopa.interop.common.contract.template.action.TestChain;
import it.pagopa.interop.common.contract.template.rest.RestService;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import java.util.UUID;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PurposeApiClient extends RestService {

    private final PurposesApi api;
    private final PurposeMapper mapper;

    
    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource, Purpose> createPurposeForReceiveEservice(it.pagopa.interop.generated.openapi.clients.bff.model.PurposeEServiceSeed purposeEServiceSeed) {
        return super.create(
            () -> api.createPurposeForReceiveEserviceWithHttpInfo(purposeEServiceSeed),
            mapper::toDomain
        );
    }
    
    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.PurposeVersionResource, Purpose> updateReversePurpose(UUID purposeId, it.pagopa.interop.generated.openapi.clients.bff.model.ReversePurposeUpdateContent reversePurposeUpdateContent) {
        return super.update(
            () -> api.updateReversePurposeWithHttpInfo(purposeId, reversePurposeUpdateContent),
            mapper::toDomain
        );
    }
    
    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource, Purpose> createPurpose(it.pagopa.interop.generated.openapi.clients.bff.model.PurposeSeed purposeSeed) {
        return super.create(
            () -> api.createPurposeWithHttpInfo(purposeSeed),
            mapper::toDomain
        );
    }
    
    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.Purposes, Purpose> getProducerPurposes(String q, List<UUID> eservicesIds, List<UUID> consumersIds, List<it.pagopa.interop.generated.openapi.clients.bff.model.PurposeVersionState> states, Integer offset, Integer limit) {
        return super.readAll(
            () -> api.getProducerPurposesWithHttpInfo(offset, limit, q, eservicesIds, consumersIds, states),
            mapper::toDomainList
        );
    }
    
    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.Purposes, Purpose> getConsumerPurposes(String q, List<UUID> eservicesIds, List<UUID> producersIds, List<it.pagopa.interop.generated.openapi.clients.bff.model.PurposeVersionState> states, Integer offset, Integer limit) {
        return super.readAll(
            () -> api.getConsumerPurposesWithHttpInfo(offset, limit, q, eservicesIds, producersIds, states),
            mapper::toDomainList
        );
    }
    
    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.Purposes, Purpose> getRiskAnalysisAssignments(List<UUID> eservicesIds, List<it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisSigningState> signingStates, Integer offset, Integer limit) {
        return super.readAll(
            () -> api.getRiskAnalysisAssignmentsWithHttpInfo(offset, limit, eservicesIds, signingStates),
            mapper::toDomainList
        );
    }
    
    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.PurposeVersionResource, Purpose> clonePurpose(UUID purposeId, it.pagopa.interop.generated.openapi.clients.bff.model.PurposeCloneSeed purposeCloneSeed) {
        return super.update(
            () -> api.clonePurposeWithHttpInfo(purposeId, purposeCloneSeed),
            mapper::toDomain
        );
    }
    
    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.PurposeVersionResource, Purpose> createPurposeVersion(UUID purposeId, it.pagopa.interop.generated.openapi.clients.bff.model.PurposeVersionSeed purposeVersionSeed) {
        return super.update(
            () -> api.createPurposeVersionWithHttpInfo(purposeId, purposeVersionSeed),
            mapper::toDomain
        );
    }
    
    public TestChain<org.springframework.core.io.Resource, Purpose> getRiskAnalysisDocument(UUID purposeId, UUID versionId, UUID documentId) {
        return super.read(
            () -> api.getRiskAnalysisDocumentWithHttpInfo(purposeId, versionId, documentId),
            mapper::toDomain
        );
    }
    
    public TestChain<org.springframework.core.io.Resource, Purpose> getSignedDocument(UUID purposeId, UUID versionId, UUID documentId) {
        return super.read(
            () -> api.getSignedDocumentWithHttpInfo(purposeId, versionId, documentId),
            mapper::toDomain
        );
    }
    
    public TestChain<Void, Purpose> rejectPurposeVersion(UUID purposeId, UUID versionId, it.pagopa.interop.generated.openapi.clients.bff.model.RejectPurposeVersionPayload rejectPurposeVersionPayload) {
        return super.update(
            () -> api.rejectPurposeVersionWithHttpInfo(purposeId, versionId, rejectPurposeVersionPayload),
            mapper::toDomain
        );
    }
    
    public TestChain<Void, Purpose> assignRiskAnalysisReviewer(UUID purposeId, it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisAssignmentSeed riskAnalysisAssignmentSeed) {
        return super.update(
            () -> api.assignRiskAnalysisReviewerWithHttpInfo(purposeId, riskAnalysisAssignmentSeed),
            mapper::toDomain
        );
    }
    
    public TestChain<Void, Purpose> submitRiskAnalysis(UUID purposeId, it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisSubmissionSeed riskAnalysisSubmissionSeed) {
        return super.update(
            () -> api.submitRiskAnalysisWithHttpInfo(purposeId, riskAnalysisSubmissionSeed),
            mapper::toDomain
        );
    }
    
    public TestChain<Void, Purpose> signRiskAnalysis(UUID purposeId) {
        return super.update(
            () -> api.signRiskAnalysisWithHttpInfo(purposeId),
            mapper::toDomain
        );
    }
    
    public TestChain<Void, Purpose> rejectRiskAnalysis(UUID purposeId, it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisRejectionSeed riskAnalysisRejectionSeed) {
        return super.update(
            () -> api.rejectRiskAnalysisWithHttpInfo(purposeId, riskAnalysisRejectionSeed),
            mapper::toDomain
        );
    }
    
    public TestChain<Void, Purpose> editRiskAnalysisForm(UUID purposeId, it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisFormSeed riskAnalysisFormSeed) {
        return super.update(
            () -> api.editRiskAnalysisFormWithHttpInfo(purposeId, riskAnalysisFormSeed),
            mapper::toDomain
        );
    }
    
    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.PurposeVersionResource, Purpose> archivePurposeVersion(UUID purposeId, UUID versionId) {
        return super.update(
            () -> api.archivePurposeVersionWithHttpInfo(purposeId, versionId),
            mapper::toDomain
        );
    }
    
    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.PurposeVersionResource, Purpose> suspendPurposeVersion(UUID purposeId, UUID versionId, it.pagopa.interop.generated.openapi.clients.bff.model.DelegationRef delegationRef) {
        return super.update(
            () -> api.suspendPurposeVersionWithHttpInfo(purposeId, versionId, delegationRef),
            mapper::toDomain
        );
    }
    
    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.PurposeVersionResource, Purpose> activatePurposeVersion(UUID purposeId, UUID versionId, it.pagopa.interop.generated.openapi.clients.bff.model.DelegationRef delegationRef) {
        return super.update(
            () -> api.activatePurposeVersionWithHttpInfo(purposeId, versionId, delegationRef),
            mapper::toDomain
        );
    }
    
    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.Purpose, Purpose> getPurpose(UUID purposeId) {
        return super.read(
            () -> api.getPurposeWithHttpInfo(purposeId),
            mapper::toDomain
        );
    }
    
    public TestChain<Void, Purpose> deletePurpose(UUID purposeId) {
        return super.read(
            () -> api.deletePurposeWithHttpInfo(purposeId),
            mapper::toDomain
        );
    }
    
    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.PurposeVersionResource, Purpose> updatePurpose(UUID purposeId, it.pagopa.interop.generated.openapi.clients.bff.model.PurposeUpdateContent purposeUpdateContent) {
        return super.update(
            () -> api.updatePurposeWithHttpInfo(purposeId, purposeUpdateContent),
            mapper::toDomain
        );
    }
    
    public TestChain<Void, Purpose> deletePurposeVersion(UUID purposeId, UUID versionId) {
        return super.read(
            () -> api.deletePurposeVersionWithHttpInfo(purposeId, versionId),
            mapper::toDomain
        );
    }
    
    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource, Purpose> createPurposeFromTemplate(UUID purposeTemplateId, it.pagopa.interop.generated.openapi.clients.bff.model.PurposeFromTemplateSeed purposeFromTemplateSeed) {
        return super.update(
            () -> api.createPurposeFromTemplateWithHttpInfo(purposeTemplateId, purposeFromTemplateSeed),
            mapper::toDomain
        );
    }
    
    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.PurposeVersionResource, Purpose> patchUpdatePurposeFromTemplate(UUID purposeTemplateId, UUID purposeId, it.pagopa.interop.generated.openapi.clients.bff.model.PatchPurposeUpdateFromTemplateContent patchPurposeUpdateFromTemplateContent) {
        return super.update(
            () -> api.patchUpdatePurposeFromTemplateWithHttpInfo(purposeTemplateId, purposeId, patchPurposeUpdateFromTemplateContent),
            mapper::toDomain
        );
    }
    
    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisFormConfig, Purpose> retrieveLatestRiskAnalysisConfiguration(it.pagopa.interop.generated.openapi.clients.bff.model.TenantKind tenantKind) {
        return super.read(
            () -> api.retrieveLatestRiskAnalysisConfigurationWithHttpInfo(tenantKind),
            mapper::toDomain
        );
    }
    
    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisFormConfig, Purpose> retrieveRiskAnalysisConfigurationByVersion(String riskAnalysisVersion, UUID eserviceId) {
        return super.read(
            () -> api.retrieveRiskAnalysisConfigurationByVersionWithHttpInfo(riskAnalysisVersion, eserviceId),
            mapper::toDomain
        );
    }
    
    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.RemainingDailyCallsResponse, Purpose> getRemainingDailyCalls(UUID purposeId) {
        return super.readAll(
            () -> api.getRemainingDailyCallsWithHttpInfo(purposeId),
            mapper::toDomainList
        );
    }
    
}