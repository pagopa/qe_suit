package it.pagopa.interop.bff.eservice.infrastructure;

import it.pagopa.interop.common.eservice.domain.EServiceCatalog;
import it.pagopa.interop.common.infrastructure.config.TestMapperConfig;
import it.pagopa.interop.generated.openapi.clients.bff.model.CatalogEService;
import it.pagopa.interop.generated.openapi.clients.bff.model.CatalogEServices;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = TestMapperConfig.class)
public interface BffEServiceCatalogMapper {
    @Mapping(target = "offset", source = "pagination.offset")
    @Mapping(target = "limit", source = "pagination.limit")
    @Mapping(target = "totalCount", source = "pagination.totalCount")
    EServiceCatalog toCatalog(CatalogEServices source);

    @Mapping(target = "producerId", source = "producer.id")
    @Mapping(target = "descriptorId", source = "activeDescriptor.id")
    @Mapping(target = "version", source = "activeDescriptor.version")
    @Mapping(target = "state", source = "activeDescriptor.state")
    EServiceCatalog.Entry toEntry(CatalogEService source);
}
