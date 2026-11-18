package it.pagopa.interop.common.infrastructure.context;

import it.pagopa.infrastructure.channel.CurrentChannel;
import it.pagopa.interop.common.client.domain.Client;
import it.pagopa.interop.common.infrastructure.config.AuthSessionRoutingConfig;
import it.pagopa.interop.common.infrastructure.config.CucumberConfig;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.common.kernel.context.AuthSession;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.m2m.kernel.domain.M2MRole;
import it.pagopa.interop.m2m.v3.infrastructure.auth.CucumberM2Mv3Session;
import it.pagopa.interop.m2m.v3.infrastructure.auth.InMemoryM2Mv3Session;
import it.pagopa.interop.m2m.v3.infrastructure.auth.M2Mv3ApiClientProvider;
import it.pagopa.interop.m2m.v3.infrastructure.auth.M2Mv3SessionConfig;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.SimpleThreadScope;

import java.util.List;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthSessionSnapshotTest {
    private final M2Mv3ApiClientProvider clientProvider = mock(M2Mv3ApiClientProvider.class);
    private final Client firstClient = Client.builder().build();
    private final Client secondClient = Client.builder().build();

    @Test
    void userSessionsRestoreEmptyAndPopulatedStatesIncludingNestedSnapshots() {
        for (AuthSession session : List.of(new CucumberUserSession(), new InMemoryUserSession())) {
            try (var empty = session.snapshot()) {
                session.set(Tenant.PAGO_PA);
            }
            assertEmpty(session);
            session.set(UserRole.API, Tenant.COMUNE_DI_MILANO);
            try (var original = session.snapshot()) {
                session.set(Tenant.PAGO_PA);
                try (var intermediate = session.snapshot()) {
                    session.set(UserRole.API, Tenant.COMUNE_DI_MILANO);
                }
                assertThat(session.getTenant()).isEqualTo(Tenant.PAGO_PA);
                assertThat(session.getRole()).isEqualTo(UserRole.ADMIN);
            }
            assertThat(session.getActor()).isEqualTo(User.getTenantUser(Tenant.COMUNE_DI_MILANO, UserRole.API));
            assertThat(session.getTenant()).isEqualTo(Tenant.COMUNE_DI_MILANO);
            assertThat(session.getRole()).isEqualTo(UserRole.API);
        }
    }

    @Test
    void machineSnapshotsRestoreClientRoleTenantAndUnsetFieldsWithoutProvisioning() {
        when(clientProvider.getOrCreate(Tenant.COMUNE_DI_MILANO, M2MRole.M2M))
                .thenReturn(firstClient);
        when(clientProvider.getOrCreate(Tenant.PAGO_PA, M2MRole.M2M_ADMIN))
                .thenReturn(secondClient);
        try (var context = new AnnotationConfigApplicationContext()) {
            context.registerBean(M2Mv3ApiClientProvider.class, () -> clientProvider);
            context.refresh();
            var provider = context.getBeanProvider(M2Mv3ApiClientProvider.class);
            for (AuthSession session : List.of(new CucumberM2Mv3Session(provider), new InMemoryM2Mv3Session(provider))) {
                try (var empty = session.snapshot()) {
                    session.set(Tenant.PAGO_PA);
                }
                assertEmpty(session);

                session.set(firstClient, Tenant.COMUNE_DI_MILANO);
                try (var partial = session.snapshot()) {
                    session.set(Tenant.PAGO_PA);
                }
                assertThat(session.getActor()).isSameAs(firstClient);
                assertThat(session.getTenant()).isEqualTo(Tenant.COMUNE_DI_MILANO);
                assertThatThrownBy(session::getRole).isInstanceOf(IllegalStateException.class);

                session.set(M2MRole.M2M, Tenant.COMUNE_DI_MILANO);
                try (var original = session.snapshot()) {
                    session.set(Tenant.PAGO_PA);
                }
                assertThat(session.getActor()).isSameAs(firstClient);
                assertThat(session.getTenant()).isEqualTo(Tenant.COMUNE_DI_MILANO);
                assertThat(session.getRole()).isEqualTo(M2MRole.M2M);
            }
        }
        verify(clientProvider, times(6)).getOrCreate(Tenant.PAGO_PA, M2MRole.M2M_ADMIN);
        verify(clientProvider, times(2)).getOrCreate(Tenant.COMUNE_DI_MILANO, M2MRole.M2M);
        verifyNoMoreInteractions(clientProvider);
    }

    @Test
    void routedSnapshotRestoresCapturedSessionEvenIfChannelChanges() {
        for (String profile : List.of("junit", "cucumber")) {
            try (var context = context(profile)) {
                var channel = currentChannel(context);
                var session = context.getBean(AuthSession.class);
                channel.setCurrentChannel(Channel.BFF);
                session.set(UserRole.API, Tenant.COMUNE_DI_MILANO);
                var snapshot = session.snapshot();
                session.set(Tenant.PAGO_PA);
                channel.setCurrentChannel(Channel.M2M_V3);
                session.set(firstClient, Tenant.SOGECAP);
                snapshot.close();
                assertThat(session.getActor()).isSameAs(firstClient);
                channel.setCurrentChannel(Channel.WEB_BROWSER);
                assertThat(session.getTenant()).isEqualTo(Tenant.COMUNE_DI_MILANO);
                assertThat(session.getRole()).isEqualTo(UserRole.API);
            }
        }
    }

    @Test
    void scenarioSnapshotDoesNotRestoreIntoNextScenario() {
        var scope = new SimpleThreadScope();
        try (var context = new AnnotationConfigApplicationContext()) {
            context.getBeanFactory().registerScope("cucumber-glue", scope);
            context.getEnvironment().setActiveProfiles("cucumber");
            context.registerBean(M2Mv3ApiClientProvider.class, () -> clientProvider);
            context.register(AuthSessionRoutingConfig.class, M2Mv3SessionConfig.class, CucumberConfig.class);
            context.refresh();
            currentChannel(context).setCurrentChannel(Channel.BFF);
            var session = context.getBean(AuthSession.class);
            session.set(UserRole.API, Tenant.COMUNE_DI_MILANO);
            var oldSnapshot = session.snapshot();
            assertThat(scope.remove("scopedTarget.userAuthSession")).isNotNull();
            session.set(Tenant.PAGO_PA);
            oldSnapshot.close();
            assertThat(session.getTenant()).isEqualTo(Tenant.PAGO_PA);
            assertThat(session.getRole()).isEqualTo(UserRole.ADMIN);
        }
    }

    @Test
    void closingFromDifferentThreadIsRejectedWithoutChangingOwnerState() throws Exception {
        var session = new InMemoryUserSession();
        session.set(UserRole.API, Tenant.COMUNE_DI_MILANO);
        var snapshot = session.snapshot();
        session.set(Tenant.PAGO_PA);
        var executor = Executors.newSingleThreadExecutor();
        try {
            var result = executor.submit(() -> {
                assertThatThrownBy(snapshot::close).isInstanceOf(IllegalStateException.class)
                        .hasMessageContaining("originating thread");
                assertEmpty(session);
            });
            result.get();
        } finally {
            executor.shutdown();
        }
        assertThat(session.getTenant()).isEqualTo(Tenant.PAGO_PA);
        snapshot.close();
        assertThat(session.getRole()).isEqualTo(UserRole.API);
    }

    private AnnotationConfigApplicationContext context(String profile) {
        var context = new AnnotationConfigApplicationContext();
        if (profile.equals("cucumber")) {
            context.getBeanFactory().registerScope("cucumber-glue", new SimpleThreadScope());
        }
        context.getEnvironment().setActiveProfiles(profile);
        context.registerBean(M2Mv3ApiClientProvider.class, () -> clientProvider);
        context.register(AuthSessionRoutingConfig.class, M2Mv3SessionConfig.class,
                profile.equals("cucumber") ? CucumberConfig.class : JunitContextConfig.class);
        context.refresh();
        return context;
    }

    private static void assertEmpty(AuthSession session) {
        assertThatThrownBy(session::getActor).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(session::getRole).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(session::getTenant).isInstanceOf(IllegalStateException.class);
    }

    @SuppressWarnings("unchecked")
    private static CurrentChannel<Channel> currentChannel(AnnotationConfigApplicationContext context) {
        return context.getBean(CurrentChannel.class);
    }
}
