package it.pagopa.interop.bff.infrastructure.client;

import it.pagopa.interop.common.contract.model.eservice.EService;
import it.pagopa.interop.generated.openapi.clients.bff.api.EservicesApi;
import it.pagopa.interop.common.contract.template.action.TestChain;
import it.pagopa.interop.common.contract.template.rest.RestService;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import java.util.UUID;
import java.util.List;

@Component
@RequiredArgsConstructor
public class EServiceApiClient extends RestService {

    private final EservicesApi api;
    private final EServiceMapper mapper;

    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.CatalogEServices, EService> getEServicesCatalog(Integer offset, Integer limit, it.pagopa.interop.generated.openapi.clients.bff.model.PersonalDataFilter personalData, String q, List<UUID> producersIds, List<UUID> attributesIds, List<it.pagopa.interop.generated.openapi.clients.bff.model.EServiceDescriptorState> states, List<it.pagopa.interop.generated.openapi.clients.bff.model.AgreementState> agreementStates, it.pagopa.interop.generated.openapi.clients.bff.model.EServiceMode mode, Boolean isConsumerDelegable) {
        return super.readAll(
            () -> api.getEServicesCatalogWithHttpInfo(offset, limit, personalData, q, producersIds, attributesIds, states, agreementStates, mode, isConsumerDelegable),
            mapper::toDomainList
        );
    }
    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.CreatedEServiceDescriptor, EService> createEService(it.pagopa.interop.generated.openapi.clients.bff.model.EServiceSeed eserviceSeed) {
        return super.create(
            () -> api.createEServiceWithHttpInfo(eserviceSeed),
            mapper::toDomain
        );
    }
    public TestChain<org.springframework.core.io.Resource, EService> getEServiceConsumers(UUID eServiceId) {
        return super.read(
            () -> api.getEServiceConsumersWithHttpInfo(eServiceId),
            mapper::toDomain
        );
    }
    public TestChain<Void, EService> deleteDraft(UUID eServiceId, UUID descriptorId) {
        return super.read(
            () -> api.deleteDraftWithHttpInfo(eServiceId, descriptorId),
            mapper::toDomain
        );
    }
    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource, EService> updateDraftDescriptor(UUID eServiceId, UUID descriptorId, it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceDescriptorSeed updateEServiceDescriptorSeed) {
        return super.update(
            () -> api.updateDraftDescriptorWithHttpInfo(eServiceId, descriptorId, updateEServiceDescriptorSeed),
            mapper::toDomain
        );
    }
    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource, EService> updateDraftDescriptorTemplateInstance(UUID eServiceId, UUID descriptorId, it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceDescriptorTemplateInstanceSeed updateEServiceDescriptorTemplateInstanceSeed) {
        return super.update(
            () -> api.updateDraftDescriptorTemplateInstanceWithHttpInfo(eServiceId, descriptorId, updateEServiceDescriptorTemplateInstanceSeed),
            mapper::toDomain
        );
    }
    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource, EService> createDescriptor(UUID eServiceId) {
        return super.update(
            () -> api.createDescriptorWithHttpInfo(eServiceId),
            mapper::toDomain
        );
    }
    public TestChain<Void, EService> activateDescriptor(UUID eServiceId, UUID descriptorId) {
        return super.update(
            () -> api.activateDescriptorWithHttpInfo(eServiceId, descriptorId),
            mapper::toDomain
        );
    }
    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource, EService> updateDescriptor(UUID eServiceId, UUID descriptorId, it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceDescriptorQuotas updateEServiceDescriptorQuotas) {
        return super.update(
            () -> api.updateDescriptorWithHttpInfo(eServiceId, descriptorId, updateEServiceDescriptorQuotas),
            mapper::toDomain
        );
    }
    public TestChain<Void, EService> scheduleArchiveEserviceDescriptor(UUID eServiceId, UUID descriptorId) {
        return super.update(
            () -> api.scheduleArchiveEserviceDescriptorWithHttpInfo(eServiceId, descriptorId),
            mapper::toDomain
        );
    }
    public TestChain<Void, EService> cancelEServiceDescriptorArchiving(UUID eServiceId, UUID descriptorId) {
        return super.read(
            () -> api.cancelEServiceDescriptorArchivingWithHttpInfo(eServiceId, descriptorId),
            mapper::toDomain
        );
    }
    public TestChain<Void, EService> cancelScheduleArchiveEservice(UUID eServiceId) {
        return super.read(
            () -> api.cancelScheduleArchiveEserviceWithHttpInfo(eServiceId),
            mapper::toDomain
        );
    }
    public TestChain<Void, EService> scheduleArchiveEservice(UUID eServiceId, it.pagopa.interop.generated.openapi.clients.bff.model.EServiceArchivingReasonSeed eserviceArchivingReasonSeed) {
        return super.update(
            () -> api.scheduleArchiveEserviceWithHttpInfo(eServiceId, eserviceArchivingReasonSeed),
            mapper::toDomain
        );
    }
    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource, EService> updateTemplateInstanceDescriptor(UUID eServiceId, UUID descriptorId, it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceTemplateInstanceDescriptorQuotas updateEServiceTemplateInstanceDescriptorQuotas) {
        return super.update(
            () -> api.updateTemplateInstanceDescriptorWithHttpInfo(eServiceId, descriptorId, updateEServiceTemplateInstanceDescriptorQuotas),
            mapper::toDomain
        );
    }
    public TestChain<Void, EService> updateAgreementApprovalPolicy(UUID eServiceId, UUID descriptorId, it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceDescriptorAgreementApprovalPolicySeed updateEServiceDescriptorAgreementApprovalPolicySeed) {
        return super.update(
            () -> api.updateAgreementApprovalPolicyWithHttpInfo(eServiceId, descriptorId, updateEServiceDescriptorAgreementApprovalPolicySeed),
            mapper::toDomain
        );
    }
    public TestChain<Void, EService> publishDescriptor(UUID eServiceId, UUID descriptorId) {
        return super.update(
            () -> api.publishDescriptorWithHttpInfo(eServiceId, descriptorId),
            mapper::toDomain
        );
    }
    public TestChain<Void, EService> suspendDescriptor(UUID eServiceId, UUID descriptorId) {
        return super.update(
            () -> api.suspendDescriptorWithHttpInfo(eServiceId, descriptorId),
            mapper::toDomain
        );
    }
    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource, EService> addEServiceTemplateInstanceInterfaceRest(UUID eServiceId, UUID descriptorId, it.pagopa.interop.generated.openapi.clients.bff.model.TemplateInstanceInterfaceRESTSeed templateInstanceInterfaceRESTSeed) {
        return super.update(
            () -> api.addEServiceTemplateInstanceInterfaceRestWithHttpInfo(eServiceId, descriptorId, templateInstanceInterfaceRESTSeed),
            mapper::toDomain
        );
    }
    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource, EService> addEServiceTemplateInstanceInterfaceSoap(UUID eServiceId, UUID descriptorId, it.pagopa.interop.generated.openapi.clients.bff.model.TemplateInstanceInterfaceSOAPSeed templateInstanceInterfaceSOAPSeed) {
        return super.update(
            () -> api.addEServiceTemplateInstanceInterfaceSoapWithHttpInfo(eServiceId, descriptorId, templateInstanceInterfaceSOAPSeed),
            mapper::toDomain
        );
    }
    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource, EService> createEServiceDocument(UUID eServiceId, UUID descriptorId, String kind, String prettyName, org.springframework.core.io.AbstractResource doc) {
        return super.update(
            () -> api.createEServiceDocumentWithHttpInfo(eServiceId, descriptorId, kind, prettyName, doc),
            mapper::toDomain
        );
    }
    public TestChain<Void, EService> deleteEServiceDocumentById(UUID eServiceId, UUID descriptorId, UUID documentId) {
        return super.read(
            () -> api.deleteEServiceDocumentByIdWithHttpInfo(eServiceId, descriptorId, documentId),
            mapper::toDomain
        );
    }
    public TestChain<org.springframework.core.io.Resource, EService> getEServiceDocumentById(UUID eServiceId, UUID descriptorId, UUID documentId) {
        return super.read(
            () -> api.getEServiceDocumentByIdWithHttpInfo(eServiceId, descriptorId, documentId),
            mapper::toDomain
        );
    }
    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.CreatedEServiceDescriptor, EService> cloneEServiceByDescriptor(UUID eServiceId, UUID descriptorId) {
        return super.update(
            () -> api.cloneEServiceByDescriptorWithHttpInfo(eServiceId, descriptorId),
            mapper::toDomain
        );
    }
    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.EServiceDoc, EService> updateEServiceDocumentById(UUID eServiceId, UUID descriptorId, UUID documentId, it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceDescriptorDocumentSeed updateEServiceDescriptorDocumentSeed) {
        return super.update(
            () -> api.updateEServiceDocumentByIdWithHttpInfo(eServiceId, descriptorId, documentId, updateEServiceDescriptorDocumentSeed),
            mapper::toDomain
        );
    }
    public TestChain<Void, EService> deleteEService(UUID eServiceId) {
        return super.read(
            () -> api.deleteEServiceWithHttpInfo(eServiceId),
            mapper::toDomain
        );
    }
    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource, EService> updateEServiceById(UUID eServiceId, it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceSeed updateEServiceSeed) {
        return super.update(
            () -> api.updateEServiceByIdWithHttpInfo(eServiceId, updateEServiceSeed),
            mapper::toDomain
        );
    }
    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource, EService> updateEServiceTemplateInstanceById(UUID eServiceId, it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceTemplateInstanceSeed updateEServiceTemplateInstanceSeed) {
        return super.update(
            () -> api.updateEServiceTemplateInstanceByIdWithHttpInfo(eServiceId, updateEServiceTemplateInstanceSeed),
            mapper::toDomain
        );
    }
    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource, EService> updateEServiceInstanceLabelAfterPublication(UUID eServiceId, it.pagopa.interop.generated.openapi.clients.bff.model.EServiceInstanceLabelUpdateSeed eserviceInstanceLabelUpdateSeed) {
        return super.update(
            () -> api.updateEServiceInstanceLabelAfterPublicationWithHttpInfo(eServiceId, eserviceInstanceLabelUpdateSeed),
            mapper::toDomain
        );
    }
    public TestChain<Void, EService> addRiskAnalysisToEService(UUID eServiceId, it.pagopa.interop.generated.openapi.clients.bff.model.EServiceRiskAnalysisSeed eserviceRiskAnalysisSeed) {
        return super.update(
            () -> api.addRiskAnalysisToEServiceWithHttpInfo(eServiceId, eserviceRiskAnalysisSeed),
            mapper::toDomain
        );
    }
    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.EServiceRiskAnalysis, EService> getEServiceRiskAnalysis(UUID eServiceId, UUID riskAnalysisId) {
        return super.read(
            () -> api.getEServiceRiskAnalysisWithHttpInfo(eServiceId, riskAnalysisId),
            mapper::toDomain
        );
    }
    public TestChain<Void, EService> updateEServiceRiskAnalysis(UUID eServiceId, UUID riskAnalysisId, it.pagopa.interop.generated.openapi.clients.bff.model.EServiceRiskAnalysisSeed eserviceRiskAnalysisSeed) {
        return super.update(
            () -> api.updateEServiceRiskAnalysisWithHttpInfo(eServiceId, riskAnalysisId, eserviceRiskAnalysisSeed),
            mapper::toDomain
        );
    }
    public TestChain<Void, EService> deleteEServiceRiskAnalysis(UUID eServiceId, UUID riskAnalysisId) {
        return super.read(
            () -> api.deleteEServiceRiskAnalysisWithHttpInfo(eServiceId, riskAnalysisId),
            mapper::toDomain
        );
    }
    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource, EService> updateEServiceDescription(UUID eServiceId, it.pagopa.interop.generated.openapi.clients.bff.model.EServiceDescriptionUpdateSeed eserviceDescriptionUpdateSeed) {
        return super.update(
            () -> api.updateEServiceDescriptionWithHttpInfo(eServiceId, eserviceDescriptionUpdateSeed),
            mapper::toDomain
        );
    }
    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource, EService> updateEServiceDelegationFlags(UUID eServiceId, it.pagopa.interop.generated.openapi.clients.bff.model.EServiceDelegationFlagsUpdateSeed eserviceDelegationFlagsUpdateSeed) {
        return super.update(
            () -> api.updateEServiceDelegationFlagsWithHttpInfo(eServiceId, eserviceDelegationFlagsUpdateSeed),
            mapper::toDomain
        );
    }
    public TestChain<Void, EService> updateEServiceName(UUID eServiceId, it.pagopa.interop.generated.openapi.clients.bff.model.EServiceNameUpdateSeed eserviceNameUpdateSeed) {
        return super.update(
            () -> api.updateEServiceNameWithHttpInfo(eServiceId, eserviceNameUpdateSeed),
            mapper::toDomain
        );
    }
    public TestChain<Void, EService> updateEServiceSignalHubFlag(UUID eServiceId, it.pagopa.interop.generated.openapi.clients.bff.model.EServiceSignalHubUpdateSeed eserviceSignalHubUpdateSeed) {
        return super.update(
            () -> api.updateEServiceSignalHubFlagWithHttpInfo(eServiceId, eserviceSignalHubUpdateSeed),
            mapper::toDomain
        );
    }
    public TestChain<Void, EService> updateEServicePersonalDataFlagAfterPublication(UUID eServiceId, it.pagopa.interop.generated.openapi.clients.bff.model.EServicePersonalDataFlagUpdateSeed eservicePersonalDataFlagUpdateSeed) {
        return super.update(
            () -> api.updateEServicePersonalDataFlagAfterPublicationWithHttpInfo(eServiceId, eservicePersonalDataFlagUpdateSeed),
            mapper::toDomain
        );
    }
    public TestChain<Void, EService> updateDescriptorAttributes(UUID eServiceId, UUID descriptorId, it.pagopa.interop.generated.openapi.clients.bff.model.DescriptorAttributesSeed descriptorAttributesSeed) {
        return super.update(
            () -> api.updateDescriptorAttributesWithHttpInfo(eServiceId, descriptorId, descriptorAttributesSeed),
            mapper::toDomain
        );
    }
    public TestChain<Void, EService> approveDelegatedEServiceDescriptor(UUID eServiceId, UUID descriptorId) {
        return super.update(
            () -> api.approveDelegatedEServiceDescriptorWithHttpInfo(eServiceId, descriptorId),
            mapper::toDomain
        );
    }
    public TestChain<Void, EService> rejectDelegatedEServiceDescriptor(UUID eServiceId, UUID descriptorId, it.pagopa.interop.generated.openapi.clients.bff.model.RejectDelegatedEServiceDescriptorSeed rejectDelegatedEServiceDescriptorSeed) {
        return super.update(
            () -> api.rejectDelegatedEServiceDescriptorWithHttpInfo(eServiceId, descriptorId, rejectDelegatedEServiceDescriptorSeed),
            mapper::toDomain
        );
    }
    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.FileResource, EService> exportEServiceDescriptor(UUID eserviceId, UUID descriptorId) {
        return super.read(
            () -> api.exportEServiceDescriptorWithHttpInfo(eserviceId, descriptorId),
            mapper::toDomain
        );
    }
    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.PresignedUrl, EService> getImportEservicePresignedUrl(String fileName) {
        return super.read(
            () -> api.getImportEservicePresignedUrlWithHttpInfo(fileName),
            mapper::toDomain
        );
    }
    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.CreatedEServiceDescriptor, EService> importEService(it.pagopa.interop.generated.openapi.clients.bff.model.FileResource fileResource) {
        return super.create(
            () -> api.importEServiceWithHttpInfo(fileResource),
            mapper::toDomain
        );
    }
    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource, EService> upgradeEServiceInstance(UUID eServiceId) {
        return super.update(
            () -> api.upgradeEServiceInstanceWithHttpInfo(eServiceId),
            mapper::toDomain
        );
    }
    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateInstances, EService> getEServiceTemplateInstances(UUID templateId, Integer offset, Integer limit, String producerName, List<it.pagopa.interop.generated.openapi.clients.bff.model.EServiceDescriptorState> states) {
        return super.readAll(
            () -> api.getEServiceTemplateInstancesWithHttpInfo(templateId, offset, limit, producerName, states),
            mapper::toDomainList
        );
    }
    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource, EService> createEServiceInstanceFromTemplate(UUID templateId, it.pagopa.interop.generated.openapi.clients.bff.model.InstanceEServiceSeed instanceEServiceSeed) {
        return super.update(
            () -> api.createEServiceInstanceFromTemplateWithHttpInfo(templateId, instanceEServiceSeed),
            mapper::toDomain
        );
    }
    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateInstances, EService> getMyEServiceTemplateInstances(UUID templateId, Integer offset, Integer limit) {
        return super.readAll(
            () -> api.getMyEServiceTemplateInstancesWithHttpInfo(templateId, offset, limit),
            mapper::toDomainList
        );
    }
    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.ProducerEServices, EService> getProducerEServices(Integer offset, Integer limit, it.pagopa.interop.generated.openapi.clients.bff.model.PersonalDataFilter personalData, String q, List<UUID> consumersIds, Boolean delegated) {
        return super.readAll(
            () -> api.getProducerEServicesWithHttpInfo(offset, limit, personalData, q, consumersIds, delegated),
            mapper::toDomainList
        );
    }
    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.ProducerEServiceDetails, EService> getProducerEServiceDetails(UUID eserviceId) {
        return super.readAll(
            () -> api.getProducerEServiceDetailsWithHttpInfo(eserviceId),
            mapper::toDomainList
        );
    }
    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.ProducerEServiceDescriptor, EService> getProducerEServiceDescriptor(UUID eserviceId, UUID descriptorId) {
        return super.read(
            () -> api.getProducerEServiceDescriptorWithHttpInfo(eserviceId, descriptorId),
            mapper::toDomain
        );
    }
    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.CatalogEServiceDescriptor, EService> getCatalogEServiceDescriptor(UUID eserviceId, UUID descriptorId) {
        return super.read(
            () -> api.getCatalogEServiceDescriptorWithHttpInfo(eserviceId, descriptorId),
            mapper::toDomain
        );
    }
    public TestChain<Boolean, EService> isEServiceNameAvailable(String name) {
        return super.read(
            () -> api.isEServiceNameAvailableWithHttpInfo(name),
            mapper::toDomain
        );
    }
}