package it.pagopa.interop.bff.infrastructure.client;

import it.pagopa.interop.bff.infrastructure.config.StrictMapperConfig;
import it.pagopa.interop.common.contract.model.Attribute;
import org.mapstruct.Mapper;
import java.util.List;

@Mapper(config = StrictMapperConfig.class)
public interface AttributeMapper {
    Attribute toDomain(it.pagopa.interop.generated.openapi.clients.bff.model.Attribute dto);


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
    default List<Attribute> toDomainList(it.pagopa.interop.generated.openapi.clients.bff.model.Attributes dto) {
        return List.of();
    }
}