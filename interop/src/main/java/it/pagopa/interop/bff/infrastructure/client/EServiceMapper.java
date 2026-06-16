package it.pagopa.interop.bff.infrastructure.client;

import it.pagopa.interop.common.contract.model.EService;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EServiceMapper {

    EService toDomain(it.pagopa.interop.generated.openapi.clients.bff.model.CreatedEServiceDescriptor dto);

    EService toDomain(it.pagopa.interop.generated.openapi.clients.bff.model.EServiceDoc dto);

    EService toDomain(it.pagopa.interop.generated.openapi.clients.bff.model.PresignedUrl dto);

    EService toDomain(it.pagopa.interop.generated.openapi.clients.bff.model.ProducerEServiceDescriptor dto);

    EService toDomain(it.pagopa.interop.generated.openapi.clients.bff.model.CatalogEServiceDescriptor dto);

    EService toDomain(it.pagopa.interop.generated.openapi.clients.bff.model.FileResource dto);

    EService nameAvailableToDomain(Boolean dto);

    default EService toDomain(it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource dto) {
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
    default List<EService> toDomainList(it.pagopa.interop.generated.openapi.clients.bff.model.CatalogEServices dto) {
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
    default List<EService> toDomainList(it.pagopa.interop.generated.openapi.clients.bff.model.EServiceRiskAnalysis dto) {
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
    default List<EService> toDomainList(it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateInstances dto) {
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
    default List<EService> toDomainList(it.pagopa.interop.generated.openapi.clients.bff.model.ProducerEServices dto) {
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
    default List<EService> toDomainList(it.pagopa.interop.generated.openapi.clients.bff.model.ProducerEServiceDetails dto) {
        return List.of();
    }


    default EService toDomain(Void dto) {
        return null;
    }


    default EService toDomain(org.springframework.core.io.Resource dto) {
        return null;
    }

}