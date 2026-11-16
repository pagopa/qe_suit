package it.pagopa.interop.common.journey.infrastructure.config;

import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.eservice.application.EServiceDescriptorLifecycleUseCase;
import it.pagopa.interop.common.eservice.application.EServiceDescriptorUseCase;
import it.pagopa.interop.common.eservice.application.EServiceRequestFactory;
import it.pagopa.interop.common.eservice.application.EServiceRiskAnalysisUseCase;
import it.pagopa.interop.common.eservice.application.EServiceUseCase;
import it.pagopa.interop.common.eservice.application.command.UpdateEServiceDescriptorCommand;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.journey.infrastructure.EServiceDescriptorJourneyImpl;
import it.pagopa.interop.common.journey.infrastructure.EServiceJourneyImpl;
import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static it.pagopa.interop.common.eservice.domain.EServiceDescriptorState.DRAFT;
import static it.pagopa.interop.common.eservice.domain.EServiceDescriptorState.PUBLISHED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class JourneyProxyConfigTest {
    private final EntityStore store = mock(EntityStore.class);
    private final EServiceUseCase services = mock(EServiceUseCase.class);
    private final EServiceDescriptorUseCase descriptors = mock(EServiceDescriptorUseCase.class);
    private final EServiceDescriptorLifecycleUseCase lifecycle = mock(EServiceDescriptorLifecycleUseCase.class);
    private final EServiceRequestFactory requestFactory = mock(EServiceRequestFactory.class);
    private final CurrentUserSession session = mock(CurrentUserSession.class);
    private final EServiceDescriptor draft = EServiceDescriptor.builder()
            .id(UUID.randomUUID()).version("1").state(DRAFT).build();
    private final EService eService = EService.builder().id(UUID.randomUUID())
            .descriptors(new ArrayList<>(List.of(draft))).build();

    @Test
    void createsRealAggregateProxyWithCompatibleInheritedMethodSignatures() {
        InteropJourney journey = new JourneyProxyConfig().interopJourney(List.of());

        assertThat(Proxy.isProxyClass(journey.getClass())).isTrue();
    }

    @Test
    void dispatchesDescriptorUpdateAndKeepsFluentChainAcrossModules() {
        InteropJourney journey = journey();
        var command = mock(UpdateEServiceDescriptorCommand.class);
        var published = draft.toBuilder().state(PUBLISHED).build();
        when(store.getLastOrThrow(EService.class)).thenReturn(eService);
        when(lifecycle.bringToState(eService, draft, PUBLISHED, command)).thenReturn(published);

        assertThat(journey.updateDescriptor(command, PUBLISHED).archiveEService(null)).isSameAs(journey);

        var order = inOrder(lifecycle, store, services);
        order.verify(store).getLastOrThrow(EService.class);
        order.verify(lifecycle).bringToState(eService, draft, PUBLISHED, command);
        order.verify(store).upsert(eService);
        order.verify(store).getLastOrThrow(EService.class);
        order.verify(services).archiveEService(eService, null);
        assertThat(eService.getDescriptorOrThrow(1)).isSameAs(published);
    }

    @Test
    void dispatchesConsumerUpdateThroughDescriptorRequestFactory() {
        InteropJourney journey = journey();
        var command = mock(UpdateEServiceDescriptorCommand.class);
        when(requestFactory.defaultUpdateDescriptorCommand()).thenReturn(command);
        when(store.getLastOrThrow(EService.class)).thenReturn(eService);
        when(lifecycle.bringToState(eService, draft, DRAFT, command)).thenReturn(draft);

        assertThat(journey.updateDescriptor(c -> c.description("Updated descriptor"), DRAFT)).isSameAs(journey);

        var order = inOrder(requestFactory, command, lifecycle);
        order.verify(requestFactory).defaultUpdateDescriptorCommand();
        order.verify(command).description("Updated descriptor");
        order.verify(lifecycle).bringToState(eService, draft, DRAFT, command);
        verify(store).upsert(eService);
    }

    private InteropJourney journey() {
        var serviceJourney = new EServiceJourneyImpl(services, descriptors, lifecycle, store,
                mock(EServiceRiskAnalysisUseCase.class), session, requestFactory);
        var descriptorJourney = new EServiceDescriptorJourneyImpl(
                descriptors, lifecycle, store, requestFactory, session);
        return new JourneyProxyConfig().interopJourney(List.of(serviceJourney, descriptorJourney));
    }
}
