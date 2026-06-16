package it.pagopa.interop.bff.infrastructure.client;

import it.pagopa.interop.common.contract.model.Tenant;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TenantMapper {

    Tenant toDomain(it.pagopa.interop.generated.openapi.clients.bff.model.Tenant dto);

    Tenant toDomain(it.pagopa.interop.generated.openapi.clients.bff.model.IsTenantAllowedToDelegation dto);

    /*
     * TODO: implementare il mapping del wrapper/list response.
     * Esempio tipico:
     *
     * if (dto == null || dto.getResults() == null) {
     *     return List.of();
     * }
     *
     * return dto.getResults().stream()
     *     .map(this::toDomain)
     *     .toList();
     */
    default List<Tenant> toDomainList(it.pagopa.interop.generated.openapi.clients.bff.model.CompactOrganizations dto) {
        return List.of();
    }

    /*
     * TODO: implementare il mapping del wrapper/list response.
     * Esempio tipico:
     *
     * if (dto == null || dto.getResults() == null) {
     *     return List.of();
     * }
     *
     * return dto.getResults().stream()
     *     .map(this::toDomain)
     *     .toList();
     */
    default List<Tenant> toDomainList(it.pagopa.interop.generated.openapi.clients.bff.model.RequesterCertifiedAttributes dto) {
        return List.of();
    }

    /*
     * TODO: implementare il mapping del wrapper/list response.
     * Esempio tipico:
     *
     * if (dto == null || dto.getResults() == null) {
     *     return List.of();
     * }
     *
     * return dto.getResults().stream()
     *     .map(this::toDomain)
     *     .toList();
     */
    default List<Tenant> toDomainList(it.pagopa.interop.generated.openapi.clients.bff.model.CertifiedAttributesResponse dto) {
        return List.of();
    }

    /*
     * TODO: implementare il mapping del wrapper/list response.
     * Esempio tipico:
     *
     * if (dto == null || dto.getResults() == null) {
     *     return List.of();
     * }
     *
     * return dto.getResults().stream()
     *     .map(this::toDomain)
     *     .toList();
     */
    default List<Tenant> toDomainList(it.pagopa.interop.generated.openapi.clients.bff.model.DeclaredAttributesResponse dto) {
        return List.of();
    }

    /*
     * TODO: implementare il mapping del wrapper/list response.
     * Esempio tipico:
     *
     * if (dto == null || dto.getResults() == null) {
     *     return List.of();
     * }
     *
     * return dto.getResults().stream()
     *     .map(this::toDomain)
     *     .toList();
     */
    default List<Tenant> toDomainList(it.pagopa.interop.generated.openapi.clients.bff.model.VerifiedAttributesResponse dto) {
        return List.of();
    }

    /*
     * TODO: implementare il mapping del wrapper/list response.
     * Esempio tipico:
     *
     * if (dto == null || dto.getResults() == null) {
     *     return List.of();
     * }
     *
     * return dto.getResults().stream()
     *     .map(this::toDomain)
     *     .toList();
     */
    default List<Tenant> toDomainList(it.pagopa.interop.generated.openapi.clients.bff.model.Tenants dto) {
        return List.of();
    }

    default Tenant toDomain(Void dto) {
        return null;
    }

}