package it.pagopa.interop.bff.infrastructure.client;

import it.pagopa.interop.bff.infrastructure.config.StrictMapperConfig;
import it.pagopa.interop.common.contract.model.shared.TenantRef;
import it.pagopa.interop.generated.openapi.clients.bff.model.CompactOrganization;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(config = StrictMapperConfig.class)
public interface TenantMapper {


    @BeanMapping(ignoreUnmappedSourceProperties = {
            "externalId",
            "features",
            "createdAt",
            "updatedAt",
            "onboardedAt",
            "subUnitType",
            "selfcareInstitutionType",
            "remoteIds",
            "contactMail",
    })
    TenantRef toDomain(it.pagopa.interop.generated.openapi.clients.bff.model.Tenant dto);

    TenantRef toDomain(it.pagopa.interop.generated.openapi.clients.bff.model.IsTenantAllowedToDelegation dto);

    @BeanMapping(ignoreUnmappedSourceProperties = {
            "contactMail",
            "hasUnreadNotifications"
    })
    @Mapping(target = "selfcareId", ignore = true)
    TenantRef toDomain(CompactOrganization dto);

    default TenantRef toDomain(Void dto) {
        return null;
    }

    default List<TenantRef> toDomainList(it.pagopa.interop.generated.openapi.clients.bff.model.CompactOrganizations dto) {
            return dto.getResults().stream()
                     .map(this::toDomain)
                     .toList();
    }
    /*
     * TODO: implementare il mapping del wrapper/list response.
     * Esempio tipico per wrapper paginati:
     *
     * if (dto == null || dto.getResults() == null) {
     *     return List.of();
     * }
     *
     * return dto.getResults().stream()
     *     .map(this::toDomain)
     *     .toList();
     */
    default List<TenantRef> toDomainList(it.pagopa.interop.generated.openapi.clients.bff.model.RequesterCertifiedAttributes dto) {
        return List.of();
    }
    /*
     * TODO: implementare il mapping del wrapper/list response.
     * Esempio tipico per wrapper paginati:
     *
     * if (dto == null || dto.getResults() == null) {
     *     return List.of();
     * }
     *
     * return dto.getResults().stream()
     *     .map(this::toDomain)
     *     .toList();
     */
    default List<TenantRef> toDomainList(it.pagopa.interop.generated.openapi.clients.bff.model.CertifiedAttributesResponse dto) {
        return List.of();
    }
    /*
     * TODO: implementare il mapping del wrapper/list response.
     * Esempio tipico per wrapper paginati:
     *
     * if (dto == null || dto.getResults() == null) {
     *     return List.of();
     * }
     *
     * return dto.getResults().stream()
     *     .map(this::toDomain)
     *     .toList();
     */
    default List<TenantRef> toDomainList(it.pagopa.interop.generated.openapi.clients.bff.model.DeclaredAttributesResponse dto) {
        return List.of();
    }
    /*
     * TODO: implementare il mapping del wrapper/list response.
     * Esempio tipico per wrapper paginati:
     *
     * if (dto == null || dto.getResults() == null) {
     *     return List.of();
     * }
     *
     * return dto.getResults().stream()
     *     .map(this::toDomain)
     *     .toList();
     */
    default List<TenantRef> toDomainList(it.pagopa.interop.generated.openapi.clients.bff.model.VerifiedAttributesResponse dto) {
        return List.of();
    }
    /*
     * TODO: implementare il mapping del wrapper/list response.
     * Esempio tipico per wrapper paginati:
     *
     * if (dto == null || dto.getResults() == null) {
     *     return List.of();
     * }
     *
     * return dto.getResults().stream()
     *     .map(this::toDomain)
     *     .toList();
     */
    default List<TenantRef> toDomainList(it.pagopa.interop.generated.openapi.clients.bff.model.Tenants dto) {
        return List.of();
    }
}