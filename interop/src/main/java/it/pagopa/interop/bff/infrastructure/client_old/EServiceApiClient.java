package it.pagopa.interop.bff.infrastructure.client_old;

import it.pagopa.interop.common.contract.model.eservice.EService;
import it.pagopa.interop.generated.openapi.clients.bff.api.EservicesApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import it.pagopa.interop.common.contract.template.action.TestChain;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import java.util.UUID;
import java.util.List;

@Component
@RequiredArgsConstructor
public class EServiceApiClient extends RestService {

    private final EservicesApi api;
    private final EServiceHandler handler;

    
    public TestChain<CatalogEServices, EService> getEServicesCatalog(PersonalDataFilter personalData, String q, List<UUID> producersIds, List<UUID> attributesIds, List<EServiceDescriptorState> states, List<AgreementState> agreementStates, EServiceMode mode, Boolean isConsumerDelegable, Integer offset, Integer limit) {
        return super.readAll(
            () -> api.getEServicesCatalogWithHttpInfo(personalData, q, producersIds, attributesIds, states, agreementStates, mode, isConsumerDelegable, offset, limit),
            handler::mapGetEServicesCatalogWrapper
        );
    }
    
    public TestChain<CreatedEServiceDescriptor, EService> createEService(EServiceSeed eServiceSeed) {
        return super.create(
            () -> api.createEServiceWithHttpInfo(eServiceSeed),
            handler::mapCreateEService
        );
    }
    
    public TestChain<Void, EService> getEServiceConsumers(UUID eServiceId) {
        return super.read(
            () -> api.getEServiceConsumersWithHttpInfo(eServiceId),
            handler::mapGetEServiceConsumers
        );
    }
    
    public TestChain<Void, EService> deleteDraft(UUID eServiceId, UUID descriptorId) {
        return super.read(
            () -> api.deleteDraftWithHttpInfo(eServiceId, descriptorId),
            handler::mapDeleteDraft
        );
    }
    
    public TestChain<CreatedResource, EService> updateDraftDescriptor(UUID eServiceId, UUID descriptorId, UpdateEServiceDescriptorSeed updateEServiceDescriptorSeed) {
        return super.update(
            () -> api.updateDraftDescriptorWithHttpInfo(eServiceId, descriptorId, updateEServiceDescriptorSeed),
            handler::mapUpdateDraftDescriptor
        );
    }
    
    public TestChain<CreatedResource, EService> updateDraftDescriptorTemplateInstance(UUID eServiceId, UUID descriptorId, UpdateEServiceDescriptorTemplateInstanceSeed updateEServiceDescriptorTemplateInstanceSeed) {
        return super.update(
            () -> api.updateDraftDescriptorTemplateInstanceWithHttpInfo(eServiceId, descriptorId, updateEServiceDescriptorTemplateInstanceSeed),
            handler::mapUpdateDraftDescriptorTemplateInstance
        );
    }
    
    public TestChain<CreatedResource, EService> createDescriptor(UUID eServiceId) {
        return super.update(
            () -> api.createDescriptorWithHttpInfo(eServiceId),
            handler::mapCreateDescriptor
        );
    }
    
    public TestChain<Void, EService> activateDescriptor(UUID eServiceId, UUID descriptorId) {
        return super.update(
            () -> api.activateDescriptorWithHttpInfo(eServiceId, descriptorId),
            handler::mapActivateDescriptor
        );
    }
    
    public TestChain<CreatedResource, EService> updateDescriptor(UUID eServiceId, UUID descriptorId, UpdateEServiceDescriptorQuotas updateEServiceDescriptorQuotas) {
        return super.update(
            () -> api.updateDescriptorWithHttpInfo(eServiceId, descriptorId, updateEServiceDescriptorQuotas),
            handler::mapUpdateDescriptor
        );
    }
    
    public TestChain<Void, EService> scheduleArchiveEserviceDescriptor(UUID eServiceId, UUID descriptorId) {
        return super.update(
            () -> api.scheduleArchiveEserviceDescriptorWithHttpInfo(eServiceId, descriptorId),
            handler::mapScheduleArchiveEserviceDescriptor
        );
    }
    
    public TestChain<Void, EService> cancelEServiceDescriptorArchiving(UUID eServiceId, UUID descriptorId) {
        return super.read(
            () -> api.cancelEServiceDescriptorArchivingWithHttpInfo(eServiceId, descriptorId),
            handler::mapCancelEServiceDescriptorArchiving
        );
    }
    
    public TestChain<Void, EService> cancelScheduleArchiveEservice(UUID eServiceId) {
        return super.read(
            () -> api.cancelScheduleArchiveEserviceWithHttpInfo(eServiceId),
            handler::mapCancelScheduleArchiveEservice
        );
    }
    
    public TestChain<Void, EService> scheduleArchiveEservice(UUID eServiceId, EServiceArchivingReasonSeed eServiceArchivingReasonSeed) {
        return super.update(
            () -> api.scheduleArchiveEserviceWithHttpInfo(eServiceId, eServiceArchivingReasonSeed),
            handler::mapScheduleArchiveEservice
        );
    }
    
    public TestChain<CreatedResource, EService> updateTemplateInstanceDescriptor(UUID eServiceId, UUID descriptorId, UpdateEServiceTemplateInstanceDescriptorQuotas updateEServiceTemplateInstanceDescriptorQuotas) {
        return super.update(
            () -> api.updateTemplateInstanceDescriptorWithHttpInfo(eServiceId, descriptorId, updateEServiceTemplateInstanceDescriptorQuotas),
            handler::mapUpdateTemplateInstanceDescriptor
        );
    }
    
    public TestChain<Void, EService> updateAgreementApprovalPolicy(UUID eServiceId, UUID descriptorId, UpdateEServiceDescriptorAgreementApprovalPolicySeed updateEServiceDescriptorAgreementApprovalPolicySeed) {
        return super.update(
            () -> api.updateAgreementApprovalPolicyWithHttpInfo(eServiceId, descriptorId, updateEServiceDescriptorAgreementApprovalPolicySeed),
            handler::mapUpdateAgreementApprovalPolicy
        );
    }
    
    public TestChain<Void, EService> publishDescriptor(UUID eServiceId, UUID descriptorId) {
        return super.update(
            () -> api.publishDescriptorWithHttpInfo(eServiceId, descriptorId),
            handler::mapPublishDescriptor
        );
    }
    
    public TestChain<Void, EService> suspendDescriptor(UUID eServiceId, UUID descriptorId) {
        return super.update(
            () -> api.suspendDescriptorWithHttpInfo(eServiceId, descriptorId),
            handler::mapSuspendDescriptor
        );
    }
    
    public TestChain<CreatedResource, EService> addEServiceTemplateInstanceInterfaceRest(UUID eServiceId, UUID descriptorId, TemplateInstanceInterfaceRESTSeed templateInstanceInterfaceRESTSeed) {
        return super.update(
            () -> api.addEServiceTemplateInstanceInterfaceRestWithHttpInfo(eServiceId, descriptorId, templateInstanceInterfaceRESTSeed),
            handler::mapAddEServiceTemplateInstanceInterfaceRest
        );
    }
    
    public TestChain<CreatedResource, EService> addEServiceTemplateInstanceInterfaceSoap(UUID eServiceId, UUID descriptorId, TemplateInstanceInterfaceSOAPSeed templateInstanceInterfaceSOAPSeed) {
        return super.update(
            () -> api.addEServiceTemplateInstanceInterfaceSoapWithHttpInfo(eServiceId, descriptorId, templateInstanceInterfaceSOAPSeed),
            handler::mapAddEServiceTemplateInstanceInterfaceSoap
        );
    }
    
    public TestChain<CreatedResource, EService> createEServiceDocument(UUID eServiceId, UUID descriptorId) {
        return super.update(
            () -> api.createEServiceDocumentWithHttpInfo(eServiceId, descriptorId),
            handler::mapCreateEServiceDocument
        );
    }
    
    public TestChain<Void, EService> deleteEServiceDocumentById(UUID eServiceId, UUID descriptorId, UUID documentId) {
        return super.read(
            () -> api.deleteEServiceDocumentByIdWithHttpInfo(eServiceId, descriptorId, documentId),
            handler::mapDeleteEServiceDocumentById
        );
    }
    
    public TestChain<Void, EService> getEServiceDocumentById(UUID eServiceId, UUID descriptorId, UUID documentId) {
        return super.read(
            () -> api.getEServiceDocumentByIdWithHttpInfo(eServiceId, descriptorId, documentId),
            handler::mapGetEServiceDocumentById
        );
    }
    
    public TestChain<CreatedEServiceDescriptor, EService> cloneEServiceByDescriptor(UUID eServiceId, UUID descriptorId) {
        return super.update(
            () -> api.cloneEServiceByDescriptorWithHttpInfo(eServiceId, descriptorId),
            handler::mapCloneEServiceByDescriptor
        );
    }
    
    public TestChain<EServiceDoc, EService> updateEServiceDocumentById(UUID eServiceId, UUID descriptorId, UUID documentId, UpdateEServiceDescriptorDocumentSeed updateEServiceDescriptorDocumentSeed) {
        return super.update(
            () -> api.updateEServiceDocumentByIdWithHttpInfo(eServiceId, descriptorId, documentId, updateEServiceDescriptorDocumentSeed),
            handler::mapUpdateEServiceDocumentById
        );
    }
    
    public TestChain<Void, EService> deleteEService(UUID eServiceId) {
        return super.read(
            () -> api.deleteEServiceWithHttpInfo(eServiceId),
            handler::mapDeleteEService
        );
    }
    
    public TestChain<CreatedResource, EService> updateEServiceById(UUID eServiceId, UpdateEServiceSeed updateEServiceSeed) {
        return super.update(
            () -> api.updateEServiceByIdWithHttpInfo(eServiceId, updateEServiceSeed),
            handler::mapUpdateEServiceById
        );
    }
    
    public TestChain<CreatedResource, EService> updateEServiceTemplateInstanceById(UUID eServiceId, UpdateEServiceTemplateInstanceSeed updateEServiceTemplateInstanceSeed) {
        return super.update(
            () -> api.updateEServiceTemplateInstanceByIdWithHttpInfo(eServiceId, updateEServiceTemplateInstanceSeed),
            handler::mapUpdateEServiceTemplateInstanceById
        );
    }
    
    public TestChain<CreatedResource, EService> updateEServiceInstanceLabelAfterPublication(UUID eServiceId, EServiceInstanceLabelUpdateSeed eServiceInstanceLabelUpdateSeed) {
        return super.update(
            () -> api.updateEServiceInstanceLabelAfterPublicationWithHttpInfo(eServiceId, eServiceInstanceLabelUpdateSeed),
            handler::mapUpdateEServiceInstanceLabelAfterPublication
        );
    }
    
    public TestChain<Void, EService> addRiskAnalysisToEService(UUID eServiceId, EServiceRiskAnalysisSeed eServiceRiskAnalysisSeed) {
        return super.update(
            () -> api.addRiskAnalysisToEServiceWithHttpInfo(eServiceId, eServiceRiskAnalysisSeed),
            handler::mapAddRiskAnalysisToEService
        );
    }
    
    public TestChain<EServiceRiskAnalysis, EService> getEServiceRiskAnalysis(UUID eServiceId, UUID riskAnalysisId) {
        return super.readAll(
            () -> api.getEServiceRiskAnalysisWithHttpInfo(eServiceId, riskAnalysisId),
            handler::mapGetEServiceRiskAnalysisWrapper
        );
    }
    
    public TestChain<Void, EService> updateEServiceRiskAnalysis(UUID eServiceId, UUID riskAnalysisId, EServiceRiskAnalysisSeed eServiceRiskAnalysisSeed) {
        return super.update(
            () -> api.updateEServiceRiskAnalysisWithHttpInfo(eServiceId, riskAnalysisId, eServiceRiskAnalysisSeed),
            handler::mapUpdateEServiceRiskAnalysis
        );
    }
    
    public TestChain<Void, EService> deleteEServiceRiskAnalysis(UUID eServiceId, UUID riskAnalysisId) {
        return super.read(
            () -> api.deleteEServiceRiskAnalysisWithHttpInfo(eServiceId, riskAnalysisId),
            handler::mapDeleteEServiceRiskAnalysis
        );
    }
    
    public TestChain<CreatedResource, EService> updateEServiceDescription(UUID eServiceId, EServiceDescriptionUpdateSeed eServiceDescriptionUpdateSeed) {
        return super.update(
            () -> api.updateEServiceDescriptionWithHttpInfo(eServiceId, eServiceDescriptionUpdateSeed),
            handler::mapUpdateEServiceDescription
        );
    }
    
    public TestChain<CreatedResource, EService> updateEServiceDelegationFlags(UUID eServiceId, EServiceDelegationFlagsUpdateSeed eServiceDelegationFlagsUpdateSeed) {
        return super.update(
            () -> api.updateEServiceDelegationFlagsWithHttpInfo(eServiceId, eServiceDelegationFlagsUpdateSeed),
            handler::mapUpdateEServiceDelegationFlags
        );
    }
    
    public TestChain<Void, EService> updateEServiceName(UUID eServiceId, EServiceNameUpdateSeed eServiceNameUpdateSeed) {
        return super.update(
            () -> api.updateEServiceNameWithHttpInfo(eServiceId, eServiceNameUpdateSeed),
            handler::mapUpdateEServiceName
        );
    }
    
    public TestChain<Void, EService> updateEServiceSignalHubFlag(UUID eServiceId, EServiceSignalHubUpdateSeed eServiceSignalHubUpdateSeed) {
        return super.update(
            () -> api.updateEServiceSignalHubFlagWithHttpInfo(eServiceId, eServiceSignalHubUpdateSeed),
            handler::mapUpdateEServiceSignalHubFlag
        );
    }
    
    public TestChain<Void, EService> updateEServicePersonalDataFlagAfterPublication(UUID eServiceId, EServicePersonalDataFlagUpdateSeed eServicePersonalDataFlagUpdateSeed) {
        return super.update(
            () -> api.updateEServicePersonalDataFlagAfterPublicationWithHttpInfo(eServiceId, eServicePersonalDataFlagUpdateSeed),
            handler::mapUpdateEServicePersonalDataFlagAfterPublication
        );
    }
    
    public TestChain<Void, EService> updateDescriptorAttributes(UUID eServiceId, UUID descriptorId, DescriptorAttributesSeed descriptorAttributesSeed) {
        return super.update(
            () -> api.updateDescriptorAttributesWithHttpInfo(eServiceId, descriptorId, descriptorAttributesSeed),
            handler::mapUpdateDescriptorAttributes
        );
    }
    
    public TestChain<Void, EService> approveDelegatedEServiceDescriptor(UUID eServiceId, UUID descriptorId) {
        return super.update(
            () -> api.approveDelegatedEServiceDescriptorWithHttpInfo(eServiceId, descriptorId),
            handler::mapApproveDelegatedEServiceDescriptor
        );
    }
    
    public TestChain<Void, EService> rejectDelegatedEServiceDescriptor(UUID eServiceId, UUID descriptorId, RejectDelegatedEServiceDescriptorSeed rejectDelegatedEServiceDescriptorSeed) {
        return super.update(
            () -> api.rejectDelegatedEServiceDescriptorWithHttpInfo(eServiceId, descriptorId, rejectDelegatedEServiceDescriptorSeed),
            handler::mapRejectDelegatedEServiceDescriptor
        );
    }
    
    public TestChain<FileResource, EService> exportEServiceDescriptor(UUID eserviceId, UUID descriptorId) {
        return super.read(
            () -> api.exportEServiceDescriptorWithHttpInfo(eserviceId, descriptorId),
            handler::mapExportEServiceDescriptor
        );
    }
    
    public TestChain<PresignedUrl, EService> getImportEservicePresignedUrl(String fileName) {
        return super.read(
            () -> api.getImportEservicePresignedUrlWithHttpInfo(fileName),
            handler::mapGetImportEservicePresignedUrl
        );
    }
    
    public TestChain<CreatedEServiceDescriptor, EService> importEService(FileResource fileResource) {
        return super.create(
            () -> api.importEServiceWithHttpInfo(fileResource),
            handler::mapImportEService
        );
    }
    
    public TestChain<CreatedResource, EService> upgradeEServiceInstance(UUID eServiceId) {
        return super.update(
            () -> api.upgradeEServiceInstanceWithHttpInfo(eServiceId),
            handler::mapUpgradeEServiceInstance
        );
    }
    
    public TestChain<EServiceTemplateInstances, EService> getEServiceTemplateInstances(UUID templateId, String producerName, List<EServiceDescriptorState> states, Integer offset, Integer limit) {
        return super.readAll(
            () -> api.getEServiceTemplateInstancesWithHttpInfo(templateId, producerName, states, offset, limit),
            handler::mapGetEServiceTemplateInstancesWrapper
        );
    }
    
    public TestChain<CreatedResource, EService> createEServiceInstanceFromTemplate(UUID templateId, InstanceEServiceSeed instanceEServiceSeed) {
        return super.update(
            () -> api.createEServiceInstanceFromTemplateWithHttpInfo(templateId, instanceEServiceSeed),
            handler::mapCreateEServiceInstanceFromTemplate
        );
    }
    
    public TestChain<EServiceTemplateInstances, EService> getMyEServiceTemplateInstances(UUID templateId, Integer offset, Integer limit) {
        return super.readAll(
            () -> api.getMyEServiceTemplateInstancesWithHttpInfo(templateId, offset, limit),
            handler::mapGetMyEServiceTemplateInstancesWrapper
        );
    }
    
    public TestChain<ProducerEServices, EService> getProducerEServices(PersonalDataFilter personalData, String q, List<UUID> consumersIds, Boolean delegated, Integer offset, Integer limit) {
        return super.readAll(
            () -> api.getProducerEServicesWithHttpInfo(personalData, q, consumersIds, delegated, offset, limit),
            handler::mapGetProducerEServicesWrapper
        );
    }
    
    public TestChain<ProducerEServiceDetails, EService> getProducerEServiceDetails(UUID eserviceId) {
        return super.readAll(
            () -> api.getProducerEServiceDetailsWithHttpInfo(eserviceId),
            handler::mapGetProducerEServiceDetailsWrapper
        );
    }
    
    public TestChain<ProducerEServiceDescriptor, EService> getProducerEServiceDescriptor(UUID eserviceId, UUID descriptorId) {
        return super.read(
            () -> api.getProducerEServiceDescriptorWithHttpInfo(eserviceId, descriptorId),
            handler::mapGetProducerEServiceDescriptor
        );
    }
    
    public TestChain<CatalogEServiceDescriptor, EService> getCatalogEServiceDescriptor(UUID eserviceId, UUID descriptorId) {
        return super.read(
            () -> api.getCatalogEServiceDescriptorWithHttpInfo(eserviceId, descriptorId),
            handler::mapGetCatalogEServiceDescriptor
        );
    }
    
    public TestChain<Void, EService> isEServiceNameAvailable(String name) {
        return super.read(
            () -> api.isEServiceNameAvailableWithHttpInfo(name),
            handler::mapIsEServiceNameAvailable
        );
    }
    
}