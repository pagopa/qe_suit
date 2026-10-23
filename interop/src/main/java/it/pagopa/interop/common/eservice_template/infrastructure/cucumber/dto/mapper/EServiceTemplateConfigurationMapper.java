package it.pagopa.interop.common.eservice_template.infrastructure.cucumber.dto.mapper;

import it.pagopa.interop.common.eservice_template.application.command.EServiceTemplateCreationCommand;
import it.pagopa.interop.common.eservice_template.application.command.EServiceTemplateInstanceCreationCommand;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplatePreparationConfiguration;
import it.pagopa.interop.common.eservice_template.infrastructure.cucumber.dto.EServiceTemplatePublicationConfiguration;
import it.pagopa.interop.common.eservice_template.infrastructure.cucumber.dto.EServiceTemplateInstanceConfiguration;
import it.pagopa.interop.common.infrastructure.config.TestMapperConfig;
import it.pagopa.utils.RandomUtils;
import org.mapstruct.*;

@Mapper(config = TestMapperConfig.class)
public interface EServiceTemplateConfigurationMapper {
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "technology", source = "technology")
    @Mapping(target = "interfaceDocumentPath", source = "interfaceFixture", qualifiedByName = "fixturePath")
    void apply(EServiceTemplatePreparationConfiguration source, @MappingTarget EServiceTemplateCreationCommand target);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "technology", source = "technology")
    @Mapping(target = "name", source = "name", qualifiedByName = "randomized")
    void apply(EServiceTemplatePublicationConfiguration source, @MappingTarget EServiceTemplateCreationCommand target);

    @BeanMapping(ignoreByDefault = true, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "instanceLabel", source = "instanceLabel")
    void apply(EServiceTemplateInstanceConfiguration source, @MappingTarget EServiceTemplateInstanceCreationCommand target);

    @Named("randomized")
    default String randomized(String value) {
        return RandomUtils.randomAlphanumericName(value);
    }

    @Named("fixturePath")
    default String fixturePath(String fixture) {
        return fixture.contains("/") ? fixture : "assets/eservice-template/" + fixture;
    }
}
