package it.pagopa.interop.common.eservice_template.infrastructure.cucumber.dto.mapper;

import it.pagopa.interop.common.eservice_template.application.command.EServiceTemplateCreationCommand;
import it.pagopa.interop.common.eservice_template.application.command.EServiceTemplateInstanceCreationCommand;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplatePreparationConfiguration;
import it.pagopa.interop.common.eservice_template.infrastructure.cucumber.dto.EServiceTemplatePublicationConfiguration;
import it.pagopa.interop.common.eservice_template.infrastructure.cucumber.dto.EServiceTemplateInstanceConfiguration;
import it.pagopa.interop.common.infrastructure.config.TestMapperConfig;
import it.pagopa.interop.common.kernel.dto.ConfigurableText;
import it.pagopa.interop.common.kernel.dto.ConfigurableTextValueResolver;
import org.mapstruct.*;

@Mapper(config = TestMapperConfig.class)
public interface EServiceTemplateConfigurationMapper {
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "technology", source = "technology")
    @Mapping(target = "interfaceDocumentPath", source = "interfaceFixture", qualifiedByName = "fixturePath")
    void apply(EServiceTemplatePreparationConfiguration source, @MappingTarget EServiceTemplateCreationCommand target);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "technology", source = "technology")
    @Mapping(target = "name", source = "name", qualifiedByName = "resolveConfigurableText")
    void apply(EServiceTemplatePublicationConfiguration source, @MappingTarget EServiceTemplateCreationCommand target);

    @BeanMapping(ignoreByDefault = true, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "instanceLabel", source = "instanceLabel")
    void apply(EServiceTemplateInstanceConfiguration source, @MappingTarget EServiceTemplateInstanceCreationCommand target);

    @Named("resolveConfigurableText")
    default String resolveConfigurableText(ConfigurableText text) {
        return ConfigurableTextValueResolver.resolve(text);
    }

    @Named("fixturePath")
    default String fixturePath(String fixture) {
        return fixture.contains("/") ? fixture : "assets/eservice-template/" + fixture;
    }
}
