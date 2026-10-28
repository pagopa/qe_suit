package it.pagopa.interop.m2m.eservice_template.infrastructure;

import it.pagopa.interop.common.eservice_template.domain.*;
import it.pagopa.interop.common.infrastructure.SharedMapper;
import it.pagopa.interop.common.infrastructure.config.TestMapperConfig;
import org.mapstruct.*;

@Mapper(
        config = TestMapperConfig.class,
        uses = {SharedMapper.class}
)
public interface M2MEServiceTemplateVersionMapper {

    @Mapping(target = "version", expression = "java(source.getVersion() == null ? null : String.valueOf(source.getVersion()))")
    @Mapping(target = "state", source = "state")
    @Mapping(target = "publishedAt", source = "publishedAt", qualifiedByName = "mapStringToInstant")
    @Mapping(target = "suspendedAt", source = "suspendedAt", qualifiedByName = "mapStringToInstant")
    @Mapping(target = "attributes", ignore = true)
    @Mapping(target = "interfaceDocument", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "archivedAt", ignore = true)
    @Mapping(target = "docs", expression = "java(new java.util.ArrayList<>())")
    @Mapping(target = "audience", expression = "java(new java.util.ArrayList<>())")
    @Mapping(target = "serverUrls", expression = "java(new java.util.ArrayList<>())")
    EServiceTemplateVersion toTemplateVersion(it.pagopa.interop.generated.openapi.clients.m2m.v3.model.EServiceTemplateVersion source);
}
