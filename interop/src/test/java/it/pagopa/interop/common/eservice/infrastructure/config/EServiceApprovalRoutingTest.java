package it.pagopa.interop.common.eservice.infrastructure.config;

import it.pagopa.infrastructure.channel.CurrentChannel;
import it.pagopa.interop.common.eservice.application.EServiceDescriptorApprovalGateway;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.EServiceDescriptorRef;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.plugin.core.PluginRegistry;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class EServiceApprovalRoutingTest {
    @Test
    @SuppressWarnings("unchecked")
    void routesRejectionThroughTheCurrentBffChannel() {
        ObjectProvider<CurrentChannel<Channel>> provider = mock(ObjectProvider.class);
        CurrentChannel<Channel> channel = mock(CurrentChannel.class);
        when(provider.getObject()).thenReturn(channel);
        when(channel.getCurrentChannel()).thenReturn(Channel.BFF);
        var adapter = mock(EServiceDescriptorApprovalGateway.class);
        when(adapter.supports(Channel.BFF)).thenReturn(true);
        var ref = EServiceRef.of(UUID.randomUUID());
        var descriptorRef = EServiceDescriptorRef.of(UUID.randomUUID());
        var draft = EServiceDescriptor.builder().id(descriptorRef.id()).build();
        when(adapter.rejectDescriptor(ref, descriptorRef, "Cleanup")).thenReturn(draft);
        var gateway = new EServiceRoutingConfig(provider).transparentEServiceDescriptorApprovalGateway(
                PluginRegistry.of(List.of(adapter)));

        assertThat(gateway.rejectDescriptor(ref, descriptorRef, "Cleanup")).isSameAs(draft);
        verify(adapter).rejectDescriptor(ref, descriptorRef, "Cleanup");
    }
}
