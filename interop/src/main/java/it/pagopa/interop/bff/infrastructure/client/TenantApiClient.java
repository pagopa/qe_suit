package it.pagopa.interop.bff.infrastructure.client;

import it.pagopa.interop.common.contract.model.shared.TenantRef;
import it.pagopa.interop.generated.openapi.clients.bff.api.TenantsApi;
import it.pagopa.interop.common.contract.template.action.TestChain;
import it.pagopa.interop.common.contract.template.rest.RestService;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import java.util.UUID;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TenantApiClient extends RestService {

    private final TenantsApi api;
    private final TenantMapper mapper;

    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.CompactOrganizations, TenantRef> getConsumers(Integer offset, Integer limit, String q) {
        return super.readAll(
            () -> api.getConsumersWithHttpInfo(offset, limit, q),
            mapper::toDomainList
        );
    }
    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.CompactOrganizations, TenantRef> getProducers(Integer offset, Integer limit, String q) {
        return super.readAll(
            () -> api.getProducersWithHttpInfo(offset, limit, q),
            mapper::toDomainList
        );
    }
    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.RequesterCertifiedAttributes, TenantRef> getRequesterCertifiedAttributes(Integer offset, Integer limit) {
        return super.readAll(
            () -> api.getRequesterCertifiedAttributesWithHttpInfo(offset, limit),
            mapper::toDomainList
        );
    }
    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.CertifiedAttributesResponse, TenantRef> getCertifiedAttributes(UUID tenantId) {
        return super.readAll(
            () -> api.getCertifiedAttributesWithHttpInfo(tenantId),
            mapper::toDomainList
        );
    }
    public TestChain<Void, TenantRef> addCertifiedAttribute(UUID tenantId, it.pagopa.interop.generated.openapi.clients.bff.model.CertifiedTenantAttributeSeed certifiedTenantAttributeSeed) {
        return super.update(
            () -> api.addCertifiedAttributeWithHttpInfo(tenantId, certifiedTenantAttributeSeed),
            mapper::toDomain
        );
    }
    public TestChain<Void, TenantRef> addDeclaredAttribute(it.pagopa.interop.generated.openapi.clients.bff.model.DeclaredTenantAttributeSeed declaredTenantAttributeSeed) {
        return super.create(
            () -> api.addDeclaredAttributeWithHttpInfo(declaredTenantAttributeSeed),
            mapper::toDomain
        );
    }
    public TestChain<Void, TenantRef> revokeDeclaredAttribute(UUID attributeId) {
        return super.read(
            () -> api.revokeDeclaredAttributeWithHttpInfo(attributeId),
            mapper::toDomain
        );
    }
    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.DeclaredAttributesResponse, TenantRef> getDeclaredAttributes(UUID tenantId) {
        return super.readAll(
            () -> api.getDeclaredAttributesWithHttpInfo(tenantId),
            mapper::toDomainList
        );
    }
    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.VerifiedAttributesResponse, TenantRef> getVerifiedAttributes(UUID tenantId) {
        return super.readAll(
            () -> api.getVerifiedAttributesWithHttpInfo(tenantId),
            mapper::toDomainList
        );
    }
    public TestChain<Void, TenantRef> verifyVerifiedAttribute(UUID tenantId, it.pagopa.interop.generated.openapi.clients.bff.model.VerifiedTenantAttributeSeed verifiedTenantAttributeSeed) {
        return super.update(
            () -> api.verifyVerifiedAttributeWithHttpInfo(tenantId, verifiedTenantAttributeSeed),
            mapper::toDomain
        );
    }
    public TestChain<Void, TenantRef> revokeCertifiedAttribute(UUID tenantId, UUID attributeId) {
        return super.read(
            () -> api.revokeCertifiedAttributeWithHttpInfo(tenantId, attributeId),
            mapper::toDomain
        );
    }
    public TestChain<Void, TenantRef> updateVerifiedAttribute(UUID tenantId, UUID attributeId, it.pagopa.interop.generated.openapi.clients.bff.model.UpdateVerifiedTenantAttributeSeed updateVerifiedTenantAttributeSeed) {
        return super.update(
            () -> api.updateVerifiedAttributeWithHttpInfo(tenantId, attributeId, updateVerifiedTenantAttributeSeed),
            mapper::toDomain
        );
    }
    public TestChain<Void, TenantRef> revokeVerifiedAttribute(UUID tenantId, UUID attributeId, it.pagopa.interop.generated.openapi.clients.bff.model.RevokeVerifiedAttributeRequest revokeVerifiedAttributeRequest) {
        return super.read(
            () -> api.revokeVerifiedAttributeWithHttpInfo(tenantId, attributeId, revokeVerifiedAttributeRequest),
            mapper::toDomain
        );
    }
    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.Tenant, TenantRef> getTenant(UUID tenantId) {
        return super.read(
            () -> api.getTenantWithHttpInfo(tenantId),
            mapper::toDomain
        );
    }
    public TestChain<Void, TenantRef> addTenantMail(UUID tenantId, it.pagopa.interop.generated.openapi.clients.bff.model.MailSeed mailSeed) {
        return super.update(
            () -> api.addTenantMailWithHttpInfo(tenantId, mailSeed),
            mapper::toDomain
        );
    }
    public TestChain<Void, TenantRef> deleteTenantMail(UUID tenantId, String mailId) {
        return super.read(
            () -> api.deleteTenantMailWithHttpInfo(tenantId, mailId),
            mapper::toDomain
        );
    }
    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.Tenants, TenantRef> getTenants(Integer limit, String name, List<it.pagopa.interop.generated.openapi.clients.bff.model.TenantFeatureType> features) {
        return super.readAll(
            () -> api.getTenantsWithHttpInfo(limit, name, features),
            mapper::toDomainList
        );
    }
    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.IsTenantAllowedToDelegation, TenantRef> isTenantAllowedToDelegation(UUID tenantId) {
        return super.read(
            () -> api.isTenantAllowedToDelegationWithHttpInfo(tenantId),
            mapper::toDomain
        );
    }
    public TestChain<Void, TenantRef> updateTenantDelegatedFeatures(it.pagopa.interop.generated.openapi.clients.bff.model.TenantDelegatedFeaturesFlagsUpdateSeed tenantDelegatedFeaturesFlagsUpdateSeed) {
        return super.create(
            () -> api.updateTenantDelegatedFeaturesWithHttpInfo(tenantDelegatedFeaturesFlagsUpdateSeed),
            mapper::toDomain
        );
    }
}