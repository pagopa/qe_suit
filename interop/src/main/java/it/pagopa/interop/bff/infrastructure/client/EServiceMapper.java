package it.pagopa.interop.bff.infrastructure.client;

import it.pagopa.interop.bff.infrastructure.config.StrictMapperConfig;
import it.pagopa.interop.common.contract.model.eservice.EService;
import org.mapstruct.Mapper;
import java.util.List;

@Mapper(config = StrictMapperConfig.class)
public interface EServiceMapper {
    EService toDomain(it.pagopa.interop.generated.openapi.clients.bff.model.EServiceDoc dto);
    EService toDomain(it.pagopa.interop.generated.openapi.clients.bff.model.EServiceRiskAnalysis dto);
    EService toDomain(it.pagopa.interop.generated.openapi.clients.bff.model.ProducerEServiceDescriptor dto);
    EService toDomain(it.pagopa.interop.generated.openapi.clients.bff.model.CatalogEServiceDescriptor dto);

    default EService toDomain(it.pagopa.interop.generated.openapi.clients.bff.model.CreatedEServiceDescriptor dto) {
        return null;
    }
    default EService toDomain(org.springframework.core.io.Resource dto) {
        return null;
    }
    default EService toDomain(Void dto) {
        return null;
    }
    default EService toDomain(it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource dto) {
        return null;
    }
    default EService toDomain(it.pagopa.interop.generated.openapi.clients.bff.model.FileResource dto) {
        return null;
    }
    default EService toDomain(it.pagopa.interop.generated.openapi.clients.bff.model.PresignedUrl dto) {
        return null;
    }
    default EService toDomain(Boolean dto) {
        return null;
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
    default List<EService> toDomainList(it.pagopa.interop.generated.openapi.clients.bff.model.CatalogEServices dto) {
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
    default List<EService> toDomainList(it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateInstances dto) {
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
    default List<EService> toDomainList(it.pagopa.interop.generated.openapi.clients.bff.model.ProducerEServices dto) {
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
    default List<EService> toDomainList(it.pagopa.interop.generated.openapi.clients.bff.model.ProducerEServiceDetails dto) {
        return List.of();
    }
}