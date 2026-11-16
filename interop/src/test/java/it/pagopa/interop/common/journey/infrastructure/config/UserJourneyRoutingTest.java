package it.pagopa.interop.common.journey.infrastructure.config;

import it.pagopa.application.context.EntityStore;
import it.pagopa.infrastructure.channel.CurrentChannel;
import it.pagopa.interop.common.client.application.ClientUseCase;
import it.pagopa.interop.common.client.domain.Client;
import it.pagopa.interop.common.infrastructure.config.AuthSessionRoutingConfig;
import it.pagopa.interop.common.infrastructure.config.CacheConfig;
import it.pagopa.interop.common.infrastructure.config.CucumberConfig;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.journey.application.UserJourney;
import it.pagopa.interop.common.journey.infrastructure.ClientJourneyImpl;
import it.pagopa.interop.common.journey.infrastructure.FinalizerJourneyImpl;
import it.pagopa.interop.common.journey.infrastructure.UserChannelJourney;
import it.pagopa.interop.common.kernel.context.AuthSession;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.m2m.kernel.domain.M2MRole;
import it.pagopa.interop.m2m.v3.infrastructure.auth.M2Mv3ApiClientProvider;
import it.pagopa.interop.m2m.v3.infrastructure.auth.M2Mv3SessionConfig;
import it.pagopa.interop.m2m.v3.infrastructure.journey.M2Mv3UserJourney;
import org.junit.jupiter.api.Test;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.SimpleThreadScope;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserJourneyRoutingTest {
    private static final Tenant TENANT = Tenant.COMUNE_DI_MILANO;
    private final Client client = Client.builder().id(UUID.randomUUID()).build();
    private final ClientUseCase clients = mock(ClientUseCase.class);

    @Test
    void routesDefaultsExplicitRolesAndActorsAndKeepsFluentChain() {
        try (var context = context("junit", null)) {
            var journey = context.getBean(InteropJourney.class);
            var channel = currentChannel(context);
            var session = context.getBean(AuthSession.class);
            when(clients.createClient(any())).thenAnswer(invocation -> {
                assertThat(channel.getCurrentChannel()).isEqualTo(Channel.BFF);
                assertThat(session.getActor()).isEqualTo(User.getTenantAdmin(TENANT));
                context.getBean(EntityStore.class).upsert(client);
                return client;
            });

            for (var userChannel : new Channel[]{Channel.BFF, Channel.WEB_BROWSER}) {
                channel.setCurrentChannel(userChannel);
                assertThat(journey.withProducer(TENANT)).isSameAs(journey);
                assertThat(session.getRole()).isEqualTo(UserRole.ADMIN);
                assertThat(journey.withConsumer(TENANT)).isSameAs(journey);
                assertThat(session.getRole()).isEqualTo(UserRole.ADMIN);
                journey.withProducer(TENANT, UserRole.API);
                assertThat(session.getRole()).isEqualTo(UserRole.API);
                journey.withConsumer(TENANT, UserRole.API);
                assertThat(session.getRole()).isEqualTo(UserRole.API);
                var user = User.getTenantAdmin(TENANT);
                journey.withProducer(TENANT, user).withConsumer(TENANT, user);
                assertThat(session.getActor()).isEqualTo(user);
                assertThatThrownBy(() -> journey.withProducer(TENANT, M2MRole.M2M))
                        .isInstanceOf(IllegalArgumentException.class);
                assertThatThrownBy(() -> journey.withConsumer(TENANT, client))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            channel.setCurrentChannel(Channel.M2M_V3);
            assertThat(journey.withProducer(TENANT).get(Client.class)).isSameAs(client);
            assertThat(channel.getCurrentChannel()).isEqualTo(Channel.M2M_V3);
            assertThat(session.getRole()).isEqualTo(M2MRole.M2M_ADMIN);
            assertThat(journey.withConsumer(TENANT)).isSameAs(journey);
            assertThat(session.getRole()).isEqualTo(M2MRole.M2M_ADMIN);
            verify(clients, times(1)).createClient(any());
            journey.withProducer(TENANT, M2MRole.M2M);
            journey.withConsumer(TENANT, M2MRole.M2M);
            assertThat(session.getRole()).isEqualTo(M2MRole.M2M);
            verify(clients, times(2)).createClient(any());
            journey.withProducer(TENANT, client).withConsumer(TENANT, client);
            assertThat(session.getActor()).isSameAs(client);
            var routed = context.getBean("routedUserJourney", UserJourney.class);
            assertThat(routed.withProducer(TENANT, client)).isSameAs(routed);
            channel.setCurrentChannel(Channel.BFF);
            assertThat(routed.withConsumer(TENANT)).isSameAs(routed);
            assertThat(session.getRole()).isEqualTo(UserRole.ADMIN);
            channel.setCurrentChannel(Channel.M2M_V3);
            assertThatThrownBy(() -> journey.withConsumer(TENANT, UserRole.ADMIN))
                    .isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> journey.withProducer(TENANT, User.getTenantAdmin(TENANT)))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Test
    void scenarioScopedSessionsAreResolvedAgainAfterScenarioEnds() {
        var scope = new SimpleThreadScope();
        try (var context = context("cucumber", scope)) {
            var journey = context.getBean(InteropJourney.class);
            var channel = currentChannel(context);
            var session = context.getBean(AuthSession.class);
            when(clients.createClient(any())).thenAnswer(invocation -> {
                context.getBean(EntityStore.class).upsert(client);
                return client;
            });
            channel.setCurrentChannel(Channel.BFF);
            journey.withConsumer(TENANT);
            channel.setCurrentChannel(Channel.M2M_V3);
            journey.withProducer(TENANT);
            assertThat(session.getActor()).isSameAs(client);
            assertThat(scope.remove("scopedTarget.userAuthSession")).isNotNull();
            assertThat(scope.remove("scopedTarget.m2mV3AuthSession")).isNotNull();
            assertThatThrownBy(session::getActor).isInstanceOf(IllegalStateException.class);
            channel.setCurrentChannel(Channel.WEB_BROWSER);
            assertThatThrownBy(session::getActor).isInstanceOf(IllegalStateException.class);
            journey.withConsumer(TENANT, UserRole.API);
            assertThat(session.getRole()).isEqualTo(UserRole.API);
        }
    }

    private AnnotationConfigApplicationContext context(String profile, SimpleThreadScope scope) {
        var context = new AnnotationConfigApplicationContext();
        if (scope != null) {
            context.getBeanFactory().registerScope("cucumber-glue", scope);
        }
        context.getEnvironment().setActiveProfiles(profile);
        context.registerBean(ClientUseCase.class, () -> clients);
        context.register(AuthSessionRoutingConfig.class, M2Mv3SessionConfig.class,
                UserJourneyRoutingConfig.class, JourneyProxyConfig.class,
                UserChannelJourney.class, M2Mv3UserJourney.class,
                ClientJourneyImpl.class, FinalizerJourneyImpl.class,
                M2Mv3ApiClientProvider.class, CacheConfig.class, CachingConfig.class);
        context.register(profile.equals("junit") ? JunitContextConfig.class : CucumberConfig.class);
        context.refresh();
        return context;
    }

    @SuppressWarnings("unchecked")
    private static CurrentChannel<Channel> currentChannel(AnnotationConfigApplicationContext context) {
        return context.getBean(CurrentChannel.class);
    }

    @Configuration(proxyBeanMethods = false)
    @EnableCaching
    static class CachingConfig {}
}
