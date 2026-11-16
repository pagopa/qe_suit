package it.pagopa.interop.m2m.v3.infrastructure.auth;

import it.pagopa.infrastructure.context.InMemoryCurrentChannel;
import it.pagopa.infrastructure.channel.CurrentChannel;
import it.pagopa.interop.common.client.domain.Client;
import it.pagopa.interop.common.infrastructure.config.CacheConfig;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.m2m.kernel.domain.M2MRole;
import org.junit.jupiter.api.Test;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class M2Mv3ApiClientProviderTest {
    @Test
    void cachesPerTenantAndRoleAndRestoresChannel() {
        var channel = new InMemoryCurrentChannel<Channel>();
        channel.setCurrentChannel(Channel.M2M_V3);
        var journey = mock(InteropJourney.class);
        var client = Client.builder().build();
        when(journey.withProducer(any(Tenant.class), any(User.class))).thenAnswer(invocation -> {
            assertThat(channel.getCurrentChannel()).isEqualTo(Channel.BFF);
            return journey;
        });
        when(journey.createClient(any())).thenReturn(journey);
        when(journey.get(Client.class)).thenReturn(client);
        try (var context = new AnnotationConfigApplicationContext()) {
            context.register(CachingConfig.class, CacheConfig.class);
            context.registerBean("m2Mv3ApiClientProvider", M2Mv3ApiClientProvider.class,
                    () -> new M2Mv3ApiClientProvider(journey, channel));
            context.refresh();
            var provider = context.getBean(M2Mv3ApiClientProvider.class);
            assertThat(provider.getOrCreate(Tenant.COMUNE_DI_MILANO, M2MRole.M2M_ADMIN)).isSameAs(client);
            assertThat(channel.getCurrentChannel()).isEqualTo(Channel.M2M_V3);
            assertThat(provider.getOrCreate(Tenant.COMUNE_DI_MILANO, M2MRole.M2M_ADMIN)).isSameAs(client);
            verify(journey, times(1)).createClient(any());
            provider.getOrCreate(Tenant.COMUNE_DI_MILANO, M2MRole.M2M);
            provider.getOrCreate(Tenant.PAGO_PA, M2MRole.M2M_ADMIN);
            verify(journey, times(3)).createClient(any());
        }
    }

    @Test
    void restoresChannelOnFailure() {
        var channel = new InMemoryCurrentChannel<Channel>();
        channel.setCurrentChannel(Channel.M2M_V3);
        var journey = mock(InteropJourney.class);
        when(journey.withProducer(any(Tenant.class), any(User.class))).thenThrow(new IllegalStateException("failed"));
        var provider = new M2Mv3ApiClientProvider(journey, channel);
        assertThatThrownBy(() -> provider.getOrCreate(Tenant.COMUNE_DI_MILANO, M2MRole.M2M_ADMIN))
                .hasMessage("failed");
        assertThat(channel.getCurrentChannel()).isEqualTo(Channel.M2M_V3);
    }

    @Test
    @SuppressWarnings("unchecked")
    void preservesProvisioningFailureWhenRestorationAlsoFails() {
        CurrentChannel<Channel> channel = mock(CurrentChannel.class);
        var journey = mock(InteropJourney.class);
        var provisioningFailure = new IllegalStateException("creation failed");
        var restorationFailure = new IllegalStateException("restoration failed");
        when(channel.getCurrentChannel()).thenReturn(Channel.M2M_V3);
        when(journey.withProducer(any(Tenant.class), any(User.class))).thenThrow(provisioningFailure);
        doThrow(restorationFailure).when(channel).setCurrentChannel(Channel.M2M_V3);
        var provider = new M2Mv3ApiClientProvider(journey, channel);
        assertThatThrownBy(() -> provider.getOrCreate(Tenant.COMUNE_DI_MILANO, M2MRole.M2M_ADMIN))
                .isSameAs(provisioningFailure)
                .satisfies(failure -> assertThat(failure.getSuppressed()).containsExactly(restorationFailure));
    }

    @Configuration(proxyBeanMethods = false)
    @EnableCaching
    static class CachingConfig {}
}
