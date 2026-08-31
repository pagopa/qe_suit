package it.pagopa.interop.common.infrastructure.contract.browser;

import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.ObjectProvider;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class WebContractRuntimeCaseExecutorTest {

    @Test
    void shouldExecuteScenarioLifecycleInOrder() {
        ObjectProvider<WebPresentationGateway> gatewayProvider = mock(ObjectProvider.class);
        CurrentUserSession currentUserSession = mock(CurrentUserSession.class);
        WebPresentationGateway gateway = mock(WebPresentationGateway.class);
        TestPage page = mock(TestPage.class);
        User user = User.getTenantAdmin(Tenant.COMUNE_DI_MILANO);
        Tenant tenant = Tenant.COMUNE_DI_MILANO;
        List<String> events = new ArrayList<>();

        when(gatewayProvider.getObject()).thenReturn(gateway);
        when(gateway.bind(TestPage.class)).thenReturn(page);
        doAnswer(invocation -> {
            events.add("bind");
            return page;
        }).when(gateway).bind(TestPage.class);
        doAnswer(invocation -> {
            events.add("navigateTo");
            return null;
        }).when(page).navigateTo();
        doAnswer(invocation -> {
            events.add("assertLoaded");
            return null;
        }).when(page).assertLoaded();
        doAnswer(invocation -> {
            events.add("close");
            return null;
        }).when(gateway).close();

        WebScenario<TestPage> scenario = new WebScenario<>(
                "scenario",
                p -> events.add("action"),
                p -> events.add("assertion")
        );

        new WebContractRuntimeCaseExecutor(gatewayProvider, currentUserSession)
                .execute(user, tenant, TestPage.class, scenario);

        verify(currentUserSession).set(user, tenant);
        assertThat(events).containsExactly("bind", "navigateTo", "assertLoaded", "action", "assertion", "close");
    }

    @Test
    void shouldCloseGatewayWhenNavigateToFails() {
        ObjectProvider<WebPresentationGateway> gatewayProvider = mock(ObjectProvider.class);
        CurrentUserSession currentUserSession = mock(CurrentUserSession.class);
        WebPresentationGateway gateway = mock(WebPresentationGateway.class);
        TestPage page = mock(TestPage.class);
        User user = User.getTenantAdmin(Tenant.COMUNE_DI_MILANO);
        Tenant tenant = Tenant.COMUNE_DI_MILANO;
        List<String> events = new ArrayList<>();

        when(gatewayProvider.getObject()).thenReturn(gateway);
        when(gateway.bind(TestPage.class)).thenReturn(page);
        doAnswer(invocation -> {
            events.add("bind");
            return page;
        }).when(gateway).bind(TestPage.class);
        doThrow(new RuntimeException("navigate failure")).when(page).navigateTo();
        doAnswer(invocation -> {
            events.add("close");
            return null;
        }).when(gateway).close();

        WebScenario<TestPage> scenario = new WebScenario<>("scenario", p -> events.add("action"), p -> events.add("assertion"));

        assertThatThrownBy(() -> new WebContractRuntimeCaseExecutor(gatewayProvider, currentUserSession)
                .execute(user, tenant, TestPage.class, scenario))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("navigate failure");

        assertThat(events).containsExactly("bind", "close");
    }

    @Test
    void shouldCloseGatewayWhenAssertLoadedFails() {
        ObjectProvider<WebPresentationGateway> gatewayProvider = mock(ObjectProvider.class);
        CurrentUserSession currentUserSession = mock(CurrentUserSession.class);
        WebPresentationGateway gateway = mock(WebPresentationGateway.class);
        TestPage page = mock(TestPage.class);
        User user = User.getTenantAdmin(Tenant.COMUNE_DI_MILANO);
        Tenant tenant = Tenant.COMUNE_DI_MILANO;
        List<String> events = new ArrayList<>();

        when(gatewayProvider.getObject()).thenReturn(gateway);
        when(gateway.bind(TestPage.class)).thenReturn(page);
        doAnswer(invocation -> {
            events.add("bind");
            return page;
        }).when(gateway).bind(TestPage.class);
        doAnswer(invocation -> {
            events.add("navigateTo");
            return null;
        }).when(page).navigateTo();
        doThrow(new RuntimeException("assertLoaded failure")).when(page).assertLoaded();
        doAnswer(invocation -> {
            events.add("close");
            return null;
        }).when(gateway).close();

        WebScenario<TestPage> scenario = new WebScenario<>("scenario", p -> events.add("action"), p -> events.add("assertion"));

        assertThatThrownBy(() -> new WebContractRuntimeCaseExecutor(gatewayProvider, currentUserSession)
                .execute(user, tenant, TestPage.class, scenario))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("assertLoaded failure");

        assertThat(events).containsExactly("bind", "navigateTo", "close");
    }

    @Test
    void shouldCloseGatewayWhenActionFails() {
        ObjectProvider<WebPresentationGateway> gatewayProvider = mock(ObjectProvider.class);
        CurrentUserSession currentUserSession = mock(CurrentUserSession.class);
        WebPresentationGateway gateway = mock(WebPresentationGateway.class);
        TestPage page = mock(TestPage.class);
        User user = User.getTenantAdmin(Tenant.COMUNE_DI_MILANO);
        Tenant tenant = Tenant.COMUNE_DI_MILANO;
        List<String> events = new ArrayList<>();

        when(gatewayProvider.getObject()).thenReturn(gateway);
        when(gateway.bind(TestPage.class)).thenReturn(page);
        doAnswer(invocation -> {
            events.add("bind");
            return page;
        }).when(gateway).bind(TestPage.class);
        doAnswer(invocation -> {
            events.add("navigateTo");
            return null;
        }).when(page).navigateTo();
        doAnswer(invocation -> {
            events.add("assertLoaded");
            return null;
        }).when(page).assertLoaded();
        doAnswer(invocation -> {
            events.add("close");
            return null;
        }).when(gateway).close();

        WebScenario<TestPage> scenario = new WebScenario<>("scenario", p -> {
            throw new RuntimeException("action failure");
        }, p -> events.add("assertion"));

        assertThatThrownBy(() -> new WebContractRuntimeCaseExecutor(gatewayProvider, currentUserSession)
                .execute(user, tenant, TestPage.class, scenario))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("action failure");

        assertThat(events).contains("bind", "navigateTo", "assertLoaded", "close");
    }

    @Test
    void shouldCloseGatewayWhenAssertionFails() {
        ObjectProvider<WebPresentationGateway> gatewayProvider = mock(ObjectProvider.class);
        CurrentUserSession currentUserSession = mock(CurrentUserSession.class);
        WebPresentationGateway gateway = mock(WebPresentationGateway.class);
        TestPage page = mock(TestPage.class);
        User user = User.getTenantAdmin(Tenant.COMUNE_DI_MILANO);
        Tenant tenant = Tenant.COMUNE_DI_MILANO;
        List<String> events = new ArrayList<>();

        when(gatewayProvider.getObject()).thenReturn(gateway);
        when(gateway.bind(TestPage.class)).thenReturn(page);
        doAnswer(invocation -> {
            events.add("bind");
            return page;
        }).when(gateway).bind(TestPage.class);
        doAnswer(invocation -> {
            events.add("navigateTo");
            return null;
        }).when(page).navigateTo();
        doAnswer(invocation -> {
            events.add("assertLoaded");
            return null;
        }).when(page).assertLoaded();
        doAnswer(invocation -> {
            events.add("close");
            return null;
        }).when(gateway).close();

        WebScenario<TestPage> scenario = new WebScenario<>("scenario", p -> events.add("action"), p -> {
            throw new RuntimeException("assertion failure");
        });

        assertThatThrownBy(() -> new WebContractRuntimeCaseExecutor(gatewayProvider, currentUserSession)
                .execute(user, tenant, TestPage.class, scenario))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("assertion failure");

        assertThat(events).contains("bind", "navigateTo", "assertLoaded", "action", "close");
    }

    @Test
    void shouldAcquireFreshGatewayForEachScenario() {
        ObjectProvider<WebPresentationGateway> gatewayProvider = mock(ObjectProvider.class);
        CurrentUserSession currentUserSession = mock(CurrentUserSession.class);
        User user = User.getTenantAdmin(Tenant.COMUNE_DI_MILANO);
        Tenant tenant = Tenant.COMUNE_DI_MILANO;
        WebPresentationGateway firstGateway = mock(WebPresentationGateway.class);
        WebPresentationGateway secondGateway = mock(WebPresentationGateway.class);
        TestPage firstPage = mock(TestPage.class);
        TestPage secondPage = mock(TestPage.class);

        when(gatewayProvider.getObject()).thenReturn(firstGateway, secondGateway);
        when(firstGateway.bind(TestPage.class)).thenReturn(firstPage);
        when(secondGateway.bind(TestPage.class)).thenReturn(secondPage);

        WebContractRuntimeCaseExecutor executor = new WebContractRuntimeCaseExecutor(gatewayProvider, currentUserSession);

        executor.execute(user, tenant, TestPage.class, new WebScenario<>("first", p -> {}, p -> {}));
        executor.execute(user, tenant, TestPage.class, new WebScenario<>("second", p -> {}, p -> {}));

        verify(gatewayProvider, Mockito.times(2)).getObject();
        verify(firstGateway).close();
        verify(secondGateway).close();
    }

    private interface TestPage extends Page {
    }
}
