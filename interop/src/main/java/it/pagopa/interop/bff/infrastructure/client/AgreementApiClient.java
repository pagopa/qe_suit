package it.pagopa.interop.bff.infrastructure.client;

import it.pagopa.interop.common.contract.model.Agreement;
import it.pagopa.interop.generated.openapi.clients.bff.api.AgreementsApi;
import it.pagopa.interop.common.contract.template.action.TestChain;
import it.pagopa.interop.common.contract.template.rest.RestService;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import java.util.UUID;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AgreementApiClient extends RestService {

    private final AgreementsApi api;
    private final AgreementMapper mapper;

    
    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.Agreements, Agreement> getConsumerAgreements(Integer offset, Integer limit, List<UUID> eservicesIds, List<UUID> producersIds, List<it.pagopa.interop.generated.openapi.clients.bff.model.AgreementState> states, Boolean showOnlyUpgradeable) {
        return super.readAll(
            () -> api.getConsumerAgreementsWithHttpInfo(offset, limit, eservicesIds, producersIds, states, showOnlyUpgradeable),
            mapper::toDomainList
        );
    }
    
    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.Agreements, Agreement> getProducerAgreements(Integer offset, Integer limit, List<UUID> eservicesIds, List<UUID> consumersIds, List<it.pagopa.interop.generated.openapi.clients.bff.model.AgreementState> states, Boolean showOnlyUpgradeable) {
        return super.readAll(
            () -> api.getProducerAgreementsWithHttpInfo(offset, limit, eservicesIds, consumersIds, states, showOnlyUpgradeable),
            mapper::toDomainList
        );
    }
    
    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource, Agreement> createAgreement(it.pagopa.interop.generated.openapi.clients.bff.model.AgreementPayload agreementPayload) {
        return super.create(
            () -> api.createAgreementWithHttpInfo(agreementPayload),
            mapper::toDomain
        );
    }
    
    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.CompactOrganizations, Agreement> getAgreementsProducers(String q, Integer offset, Integer limit) {
        return super.readAll(
            () -> api.getAgreementsProducersWithHttpInfo(offset, limit, q),
            mapper::toDomainList
        );
    }
    
    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.CompactOrganizations, Agreement> getAgreementsConsumers(String q, Integer offset, Integer limit) {
        return super.readAll(
            () -> api.getAgreementsConsumersWithHttpInfo(offset, limit, q),
            mapper::toDomainList
        );
    }
    
    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.Agreement, Agreement> getAgreementById(UUID agreementId) {
        return super.read(
            () -> api.getAgreementByIdWithHttpInfo(agreementId),
            mapper::toDomain
        );
    }
    
    public TestChain<Void, Agreement> deleteAgreement(UUID agreementId) {
        return super.read(
            () -> api.deleteAgreementWithHttpInfo(agreementId),
            mapper::toDomain
        );
    }
    
    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.Agreement, Agreement> activateAgreement(UUID agreementId, it.pagopa.interop.generated.openapi.clients.bff.model.DelegationRef delegationRef) {
        return super.update(
            () -> api.activateAgreementWithHttpInfo(agreementId, delegationRef),
            mapper::toDomain
        );
    }
    
    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource, Agreement> cloneAgreement(UUID agreementId) {
        return super.update(
            () -> api.cloneAgreementWithHttpInfo(agreementId),
            mapper::toDomain
        );
    }
    
    public TestChain<org.springframework.core.io.Resource, Agreement> addAgreementConsumerDocument(UUID agreementId, String name, String prettyName, org.springframework.core.io.AbstractResource doc) {
        return super.update(
            () -> api.addAgreementConsumerDocumentWithHttpInfo(agreementId, name, prettyName, doc),
            mapper::toDomain
        );
    }
    
    public TestChain<org.springframework.core.io.Resource, Agreement> getAgreementConsumerDocument(UUID agreementId, UUID documentId) {
        return super.read(
            () -> api.getAgreementConsumerDocumentWithHttpInfo(agreementId, documentId),
            mapper::toDomain
        );
    }
    
    public TestChain<Void, Agreement> removeAgreementConsumerDocument(UUID agreementId, UUID documentId) {
        return super.read(
            () -> api.removeAgreementConsumerDocumentWithHttpInfo(agreementId, documentId),
            mapper::toDomain
        );
    }
    
    public TestChain<org.springframework.core.io.Resource, Agreement> getAgreementContract(UUID agreementId) {
        return super.read(
            () -> api.getAgreementContractWithHttpInfo(agreementId),
            mapper::toDomain
        );
    }
    
    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.Agreement, Agreement> submitAgreement(UUID agreementId, it.pagopa.interop.generated.openapi.clients.bff.model.AgreementSubmissionPayload agreementSubmissionPayload) {
        return super.update(
            () -> api.submitAgreementWithHttpInfo(agreementId, agreementSubmissionPayload),
            mapper::toDomain
        );
    }
    
    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.Agreement, Agreement> suspendAgreement(UUID agreementId, it.pagopa.interop.generated.openapi.clients.bff.model.DelegationRef delegationRef) {
        return super.update(
            () -> api.suspendAgreementWithHttpInfo(agreementId, delegationRef),
            mapper::toDomain
        );
    }
    
    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.Agreement, Agreement> rejectAgreement(UUID agreementId, it.pagopa.interop.generated.openapi.clients.bff.model.AgreementRejectionPayload agreementRejectionPayload) {
        return super.update(
            () -> api.rejectAgreementWithHttpInfo(agreementId, agreementRejectionPayload),
            mapper::toDomain
        );
    }
    
    public TestChain<Void, Agreement> archiveAgreement(UUID agreementId) {
        return super.update(
            () -> api.archiveAgreementWithHttpInfo(agreementId),
            mapper::toDomain
        );
    }
    
    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.Agreement, Agreement> updateAgreement(UUID agreementId, it.pagopa.interop.generated.openapi.clients.bff.model.AgreementUpdatePayload agreementUpdatePayload) {
        return super.update(
            () -> api.updateAgreementWithHttpInfo(agreementId, agreementUpdatePayload),
            mapper::toDomain
        );
    }
    
    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.Agreement, Agreement> upgradeAgreement(UUID agreementId) {
        return super.update(
            () -> api.upgradeAgreementWithHttpInfo(agreementId),
            mapper::toDomain
        );
    }
    
    public TestChain<org.springframework.core.io.Resource, Agreement> getSignedAgreementContract(UUID agreementId) {
        return super.read(
            () -> api.getSignedAgreementContractWithHttpInfo(agreementId),
            mapper::toDomain
        );
    }
    
    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.HasCertifiedAttributes, Agreement> verifyTenantCertifiedAttributes(UUID tenantId, UUID eserviceId, UUID descriptorId) {
        return super.readAll(
            () -> api.verifyTenantCertifiedAttributesWithHttpInfo(tenantId, eserviceId, descriptorId),
            mapper::toDomainList
        );
    }
    
    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.CompactEServicesLight, Agreement> getAgreementsProducerEServices(String q, Integer offset, Integer limit) {
        return super.read(
            () -> api.getAgreementsProducerEServicesWithHttpInfo(offset, limit, q),
            mapper::toDomain
        );
    }
    
    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.CompactEServicesLight, Agreement> getAgreementsConsumerEServices(String q, Integer offset, Integer limit) {
        return super.read(
            () -> api.getAgreementsConsumerEServicesWithHttpInfo(offset, limit, q),
            mapper::toDomain
        );
    }
    
}