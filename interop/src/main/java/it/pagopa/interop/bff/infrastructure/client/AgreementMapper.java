package it.pagopa.interop.bff.infrastructure.client;

import it.pagopa.interop.common.contract.model.Agreement;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AgreementMapper {

    Agreement toDomain(it.pagopa.interop.generated.openapi.clients.bff.model.Agreement dto);

    Agreement toDomain(it.pagopa.interop.generated.openapi.clients.bff.model.CompactEServicesLight dto);

    default Agreement toDomain(it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource dto) {
        return null;
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
    default List<Agreement> toDomainList(it.pagopa.interop.generated.openapi.clients.bff.model.Agreements dto) {
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
    default List<Agreement> toDomainList(it.pagopa.interop.generated.openapi.clients.bff.model.CompactOrganizations dto) {
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
    default List<Agreement> toDomainList(it.pagopa.interop.generated.openapi.clients.bff.model.HasCertifiedAttributes dto) {
        return List.of();
    }

    default Agreement toDomain(Void dto) {
        return null;
    }

    default Agreement toDomain(org.springframework.core.io.Resource dto) {
        return null;
    }

}