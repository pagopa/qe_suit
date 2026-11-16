package it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.mapper;

import it.pagopa.interop.common.eservice.application.command.EServiceCreationCommand;
import it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.EServicePreparationConfiguration;
import it.pagopa.interop.common.infrastructure.SharedMapper;
import it.pagopa.interop.common.infrastructure.config.TestMapperConfig;
import it.pagopa.interop.common.infrastructure.cucumber.dto.ConfiguredBoolean;
import it.pagopa.utils.RandomUtils;
import org.mapstruct.*;

import java.util.function.Consumer;

@Mapper(config = TestMapperConfig.class, uses = { SharedMapper.class })
public interface EServicePreparationConfigurationMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(
            target = "name",
            source = "name",
            qualifiedByName = "randomizedString"
    )
    void apply(EServicePreparationConfiguration source, @MappingTarget EServiceCreationCommand target);

    @AfterMapping
    default void applyFlags(EServicePreparationConfiguration source, @MappingTarget EServiceCreationCommand target) {
        applyConfiguredBoolean(source.getSignalHub(), target::isSignalHubEnabled);
        applyConfiguredBoolean(source.getAsyncExchange(), target::isAsync);
    }

    private static void applyConfiguredBoolean(ConfiguredBoolean configuration, Consumer<Boolean> setter) {
        switch (configuration) {
            case DEFAULT -> {
                // Preserve the factory default.
            }
            case ENABLED -> setter.accept(true);
            case DISABLED -> setter.accept(false);
            case UNSET -> setter.accept(null);
        }
    }
}
