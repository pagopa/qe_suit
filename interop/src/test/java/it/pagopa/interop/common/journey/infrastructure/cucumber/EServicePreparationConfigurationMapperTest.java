package it.pagopa.interop.common.journey.infrastructure.cucumber;

import it.pagopa.interop.common.eservice.application.EServiceRequestFactory;
import it.pagopa.interop.common.eservice.application.command.EServiceCreationCommand;
import it.pagopa.interop.common.eservice.domain.EServiceMode;
import it.pagopa.interop.common.eservice.infrastructure.cucumber.EServiceJourneySteps;
import it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.EServicePreparationConfiguration;
import it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.mapper.EServicePreparationConfigurationMapper;
import it.pagopa.interop.common.infrastructure.cucumber.dto.ConfiguredBoolean;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.UserRole;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.mockito.Mockito.*;

class EServicePreparationConfigurationMapperTest {
    private final EServicePreparationConfigurationMapper mapper =
            Mappers.getMapper(EServicePreparationConfigurationMapper.class);
    private final EServiceCreationCommand command = mock(EServiceCreationCommand.class);

    @Test
    void preservesFactoryDefaultsWhenPropertiesAreOmitted() {
        mapper.apply(new EServicePreparationConfiguration(), command);

        verifyNoInteractions(command);
    }

    @Test
    void mapsExplicitPropertiesAndFlags() {
        var configuration = new EServicePreparationConfiguration();
        configuration.setAlias("ES-1");
        configuration.setName("Anagrafe");
        configuration.setDescription("");
        configuration.setMode(EServiceMode.RECEIVE);
        configuration.setSignalHub(ConfiguredBoolean.ENABLED);
        configuration.setAsyncExchange(ConfiguredBoolean.DISABLED);

        mapper.apply(configuration, command);

        verify(command).alias("ES-1");
        verify(command).name("Anagrafe");
        verify(command).description("");
        verify(command).mode(EServiceMode.RECEIVE);
        verify(command).isSignalHubEnabled(true);
        verify(command).isAsync(false);
        verifyNoMoreInteractions(command);
    }

    @Test
    void explicitlyUnsetsFlags() {
        var configuration = new EServicePreparationConfiguration();
        configuration.setSignalHub(ConfiguredBoolean.UNSET);
        configuration.setAsyncExchange(ConfiguredBoolean.UNSET);

        mapper.apply(configuration, command);

        verify(command).isSignalHubEnabled(null);
        verify(command).isAsync(null);
        verifyNoMoreInteractions(command);
    }

    @Test
    void mapsOtherFlagValues() {
        var configuration = new EServicePreparationConfiguration();
        configuration.setSignalHub(ConfiguredBoolean.DISABLED);
        configuration.setAsyncExchange(ConfiguredBoolean.ENABLED);

        mapper.apply(configuration, command);

        verify(command).isSignalHubEnabled(false);
        verify(command).isAsync(true);
        verifyNoMoreInteractions(command);
    }

    @Test
    void selectsProducerBeforeCreatingAndMappingCommand() {
        var journey = mock(InteropJourney.class);
        var factory = mock(EServiceRequestFactory.class);
        when(factory.defaultCreationEServiceCommand()).thenReturn(command);
        var steps = new EServiceJourneySteps(journey, factory, mapper);
        var configuration = new EServicePreparationConfiguration();
        configuration.setProducer(Tenant.COMUNE_DI_MILANO);
        configuration.setAlias("ES-1");

        steps.prepareEServicesForPublication(List.of(configuration));

        var order = inOrder(journey, factory, command);
        order.verify(journey).withProducer(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN);
        order.verify(factory).defaultCreationEServiceCommand();
        order.verify(command).alias("ES-1");
        order.verify(journey).prepareEServiceForPublication(command);
        order.verifyNoMoreInteractions();
    }
}
