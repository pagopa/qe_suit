package it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.mapper;

import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.EServiceCatalogExpectation;
import it.pagopa.interop.common.infrastructure.config.TestMapperConfig;
import org.mapstruct.Context;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(config = TestMapperConfig.class)
public interface EServiceCatalogExpectationMapper {
    List<EService> toEServices(List<EServiceCatalogExpectation> source, @Context EntityStore entityStore);

    default EService toEService(EServiceCatalogExpectation source, @Context EntityStore entityStore) {
        return entityStore.getByAliasOrThrow(source.getAlias(), EService.class);
    }
}
