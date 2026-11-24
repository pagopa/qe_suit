package it.pagopa.interop.m2m.eservice.infrastructure;

import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.common.infrastructure.config.TestMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.ArrayList;
import java.util.List;

@Mapper(config = TestMapperConfig.class)
public interface M2MEServiceMapper {

    @Mapping(target = "id", source = "source.id")
    @Mapping(target = "producerId", expression = "java(source.getProducerId().toString())")
    @Mapping(target = "name", source = "source.name")
    @Mapping(target = "description", source = "source.description")
    @Mapping(target = "technology", source = "source.technology")
    @Mapping(target = "mode", source = "source.mode")
    @Mapping(target = "isSignalHubEnabled", source = "source.isSignalHubEnabled")
    @Mapping(target = "isConsumerDelegable", source = "source.isConsumerDelegable")
    @Mapping(target = "isClientAccessDelegable", source = "source.isClientAccessDelegable")
    @Mapping(target = "personalData", source = "source.personalData")
    @Mapping(target = "asyncExchange", source = "source.asyncExchange")
    @Mapping(target = "descriptors", expression = "java(copyDescriptors(existingEService))")
    EService toEServicePreservingDescriptors(
            it.pagopa.interop.generated.openapi.clients.m2m.v3.model.EService source,
            EService existingEService
    );

    default List<EServiceDescriptor> copyDescriptors(EService existingEService) {
        return existingEService == null || existingEService.getDescriptors() == null
                ? new ArrayList<>()
                : new ArrayList<>(existingEService.getDescriptors());
    }
}
