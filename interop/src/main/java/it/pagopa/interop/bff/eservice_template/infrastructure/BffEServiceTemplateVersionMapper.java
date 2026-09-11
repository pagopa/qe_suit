package it.pagopa.interop.bff.eservice_template.infrastructure;

import it.pagopa.interop.bff.eservice.infrastructure.BffEServiceDescriptorMapper;
import it.pagopa.interop.bff.eservice.infrastructure.BffEServiceMapper;
import it.pagopa.interop.common.infrastructure.SharedMapper;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateVersionDetails;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersion;
import it.pagopa.interop.common.infrastructure.config.TestMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(
        config = TestMapperConfig.class,
        uses = {SharedMapper.class, BffEServiceMapper.class, BffEServiceDescriptorMapper.class}
)
public interface BffEServiceTemplateVersionMapper {

    @Mapping(target = "version", source = "version", qualifiedByName = "mapVersionNumberToString")
    @Mapping(target = "interfaceDocument", source = "interface")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "publishedAt", ignore = true)
    @Mapping(target = "suspendedAt", ignore = true)
    @Mapping(target = "archivedAt", ignore = true)
    @Mapping(target = "audience", ignore = true)
    @Mapping(target = "serverUrls", ignore = true)
    EServiceTemplateVersion toEServiceTemplateVersion(EServiceTemplateVersionDetails source);

    @Named("mapVersionNumberToString")
    default String mapVersionNumberToString(Integer version) {
        return version == null ? null : String.valueOf(version);
    }
}

