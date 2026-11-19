package it.pagopa.interop.m2m.v3.infrastructure.auth;

import it.pagopa.infrastructure.channel.CurrentChannel;
import it.pagopa.interop.common.client.domain.Client;
import it.pagopa.interop.common.infrastructure.config.AuthSessionRoutingConfig;
import it.pagopa.interop.common.infrastructure.config.CucumberConfig;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.bff.infrastructure.cucumber.step.BffUserSteps;
import it.pagopa.interop.m2m.v3.infrastructure.cucumber.step.M2Mv3UserSteps;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.context.AuthSession;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.m2m.kernel.domain.M2MRole;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.SimpleThreadScope;

import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class M2Mv3AuthSessionTest {
    private final M2Mv3ApiClientProvider clientProvider = mock(M2Mv3ApiClientProvider.class);
    private final Client client = Client.builder().build();

    @Test
    void junitSessionsAreRoutedAndThreadLocal() throws Exception {
        try (var context = context()) {
            verifyRouting(context);
            var executor = Executors.newSingleThreadExecutor();
            try {
                executor.submit(() -> {
                    currentChannel(context).setCurrentChannel(Channel.M2M_V3);
                    assertThatThrownBy(context.getBean(AuthSession.class)::getActor)
                            .isInstanceOf(IllegalStateException.class);
                }).get();
            } finally {
                executor.shutdown();
            }
        }
    }

    @Test
    void registryRetainsScenarioProxiesNotScenarioState() {
        try (var context = new AnnotationConfigApplicationContext()) {
            var scope = new SimpleThreadScope();
            context.getBeanFactory().registerScope("cucumber-glue", scope);
            context.getEnvironment().setActiveProfiles("cucumber");
            context.registerBean(M2Mv3ApiClientProvider.class, () -> clientProvider);
            context.register(AuthSessionRoutingConfig.class, M2Mv3SessionConfig.class, CucumberConfig.class);
            context.refresh();
            verifyRouting(context);
            var session = context.getBean(AuthSession.class);
            when(clientProvider.getOrCreate(Tenant.PAGO_PA, M2MRole.M2M_ADMIN))
                    .thenThrow(new IllegalStateException("client creation failed"));
            assertThatThrownBy(() -> session.set(M2MRole.M2M_ADMIN, Tenant.PAGO_PA))
                    .hasMessage("client creation failed");
            assertThat(session.getActor()).isSameAs(client);
            assertThat(session.getTenant()).isEqualTo(Tenant.COMUNE_DI_MILANO);
            assertThat(scope.remove("scopedTarget.userAuthSession")).isNotNull();
            assertThat(scope.remove("scopedTarget.m2mV3AuthSession")).isNotNull();
            assertThatThrownBy(session::getActor).isInstanceOf(IllegalStateException.class);
            currentChannel(context).setCurrentChannel(Channel.BFF);
            assertThatThrownBy(session::getActor).isInstanceOf(IllegalStateException.class);
        }
    }

    @Test
    void givenOnBffCreatesClientAsBffAndRestoresChannel() {
        var journey = mock(InteropJourney.class);
        try (var context = new AnnotationConfigApplicationContext()) {
            context.getEnvironment().setActiveProfiles("junit");
            context.register(AuthSessionRoutingConfig.class, M2Mv3SessionConfig.class, JunitContextConfig.class);
            context.registerBean(BffUserSteps.class);
            context.registerBean(M2Mv3UserSteps.class);
            context.registerBean(M2Mv3ApiClientProvider.class,
                    () -> new M2Mv3ApiClientProvider(journey, currentChannel(context)));
            context.refresh();
            var channel = currentChannel(context);
            var session = context.getBean(AuthSession.class);
            var tenant = Tenant.COMUNE_DI_MILANO;
            when(journey.withProducer(eq(tenant), any(User.class))).thenAnswer(invocation -> {
                assertThat(channel.getCurrentChannel()).isEqualTo(Channel.BFF);
                session.set((User) invocation.getArgument(1), tenant);
                return journey;
            });
            when(journey.createClient(any())).thenReturn(journey);
            when(journey.get(Client.class)).thenReturn(client);
            channel.setCurrentChannel(Channel.BFF);
            context.getBean(M2Mv3UserSteps.class).setMachine(M2MRole.M2M_ADMIN, tenant);
            assertThat(channel.getCurrentChannel()).isEqualTo(Channel.BFF);
            assertThat(session.getActor()).isEqualTo(User.getTenantAdmin(tenant));
            channel.setCurrentChannel(Channel.M2M_V3);
            assertThat(session.getActor()).isSameAs(client);
            assertThat(session.getRole()).isEqualTo(M2MRole.M2M_ADMIN);
            context.getBean(BffUserSteps.class).setUser(UserRole.ADMIN, Tenant.PAGO_PA);
            assertThat(channel.getCurrentChannel()).isEqualTo(Channel.M2M_V3);
            assertThat(session.getActor()).isSameAs(client);
            channel.setCurrentChannel(Channel.BFF);
            assertThat(session.getTenant()).isEqualTo(Tenant.PAGO_PA);
        }
    }

    @Test
    void failureLeavesPreviousSessionIntact() {
        try (var context = context()) {
            var channel = currentChannel(context);
            channel.setCurrentChannel(Channel.M2M_V3);
            var session = context.getBean(AuthSession.class);
            when(clientProvider.getOrCreate(Tenant.COMUNE_DI_MILANO, M2MRole.M2M_ADMIN)).thenReturn(client);
            session.set(M2MRole.M2M_ADMIN, Tenant.COMUNE_DI_MILANO);
            when(clientProvider.getOrCreate(Tenant.PAGO_PA, M2MRole.M2M_ADMIN))
                    .thenThrow(new IllegalStateException("client creation failed"));
            assertThatThrownBy(() -> session.set(M2MRole.M2M_ADMIN, Tenant.PAGO_PA))
                    .hasMessage("client creation failed");
            assertThat(session.getActor()).isSameAs(client);
            assertThat(session.getTenant()).isEqualTo(Tenant.COMUNE_DI_MILANO);
            assertThat(session.getRole()).isEqualTo(M2MRole.M2M_ADMIN);
        }
    }

    private AnnotationConfigApplicationContext context() {
        var context = new AnnotationConfigApplicationContext();
        context.getEnvironment().setActiveProfiles("junit");
        context.registerBean(M2Mv3ApiClientProvider.class, () -> clientProvider);
        context.register(AuthSessionRoutingConfig.class, M2Mv3SessionConfig.class, JunitContextConfig.class);
        context.refresh();
        return context;
    }

    private void verifyRouting(AnnotationConfigApplicationContext context) {
        var channel = currentChannel(context);
        var session = context.getBean(AuthSession.class);
        var tenant = Tenant.COMUNE_DI_MILANO;
        var user = User.getTenantAdmin(tenant);
        when(clientProvider.getOrCreate(tenant, M2MRole.M2M_ADMIN)).thenReturn(client);
        channel.setCurrentChannel(Channel.BFF);
        session.set(user, tenant);
        channel.setCurrentChannel(Channel.M2M_V3);
        session.set(M2MRole.M2M_ADMIN, tenant);
        assertThat(session.getActor()).isSameAs(client);
        assertThat(session.getTenant()).isEqualTo(tenant);
        assertThat(session.getRole()).isEqualTo(M2MRole.M2M_ADMIN);
        channel.setCurrentChannel(Channel.WEB_BROWSER);
        assertThat(session.getActor()).isEqualTo(user);
        assertThat(session.getRole()).isEqualTo(UserRole.ADMIN);
        channel.setCurrentChannel(Channel.M2M_V3);
        assertThat(session.getActor()).isSameAs(client);
        verify(clientProvider).getOrCreate(tenant, M2MRole.M2M_ADMIN);
    }

    @SuppressWarnings("unchecked")
    private static CurrentChannel<Channel> currentChannel(AnnotationConfigApplicationContext context) {
        return context.getBean(CurrentChannel.class);
    }
}
