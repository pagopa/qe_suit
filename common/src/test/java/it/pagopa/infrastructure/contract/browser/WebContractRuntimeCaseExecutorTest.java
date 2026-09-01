package it.pagopa.infrastructure.contract.browser;

import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.frontend.e2e.framework.web.domain.Page;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

class WebContractRuntimeCaseExecutorTest {

    @Test
    void shouldConfigureContextAndExecuteScenarioLifecycleInOrder() {
        Supplier<WebPresentationGateway> gatewayProvider = mock(Supplier.class);
        WebContractContextConfigurer contextConfigurer = mock(WebContractContextConfigurer.class);
        WebPresentationGateway gateway = mock(WebPresentationGateway.class);
        TestPage page = mock(TestPage.class);
        List<String> events = new ArrayList<>();

        doAnswer(invocation -> {
            events.add("configure");
            return null;
        }).when(contextConfigurer).configure();

        when(gatewayProvider.get()).thenReturn(gateway);
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

        new WebContractRuntimeCaseExecutor(gatewayProvider, contextConfigurer)
                .execute(TestPage.class, scenario);

        verify(contextConfigurer).configure();

        assertThat(events).containsExactly(
                "configure",
                "bind",
                "navigateTo",
                "assertLoaded",
                "action",
                "assertion",
                "close"
        );
    }

    @Test
    void shouldCloseGatewayWhenNavigateToFails() {
        Supplier<WebPresentationGateway> gatewayProvider = mock(Supplier.class);
        WebContractContextConfigurer contextConfigurer = mock(WebContractContextConfigurer.class);
        WebPresentationGateway gateway = mock(WebPresentationGateway.class);
        TestPage page = mock(TestPage.class);
        List<String> events = new ArrayList<>();

        when(gatewayProvider.get()).thenReturn(gateway);

        doAnswer(invocation -> {
            events.add("bind");
            return page;
        }).when(gateway).bind(TestPage.class);

        doThrow(new RuntimeException("navigate failure"))
                .when(page).navigateTo();

        doAnswer(invocation -> {
            events.add("close");
            return null;
        }).when(gateway).close();

        WebScenario<TestPage> scenario =
                new WebScenario<>("scenario", p -> {}, p -> {});

        assertThatThrownBy(() ->
                new WebContractRuntimeCaseExecutor(gatewayProvider, contextConfigurer)
                        .execute(TestPage.class, scenario)
        )
                .isInstanceOf(RuntimeException.class)
                .hasMessage("navigate failure");

        assertThat(events).containsExactly(
                "bind",
                "close"
        );
    }

    @Test
    void shouldCloseGatewayWhenAssertLoadedFails() {
        Supplier<WebPresentationGateway> gatewayProvider = mock(Supplier.class);
        WebContractContextConfigurer contextConfigurer = mock(WebContractContextConfigurer.class);
        WebPresentationGateway gateway = mock(WebPresentationGateway.class);
        TestPage page = mock(TestPage.class);
        List<String> events = new ArrayList<>();

        when(gatewayProvider.get()).thenReturn(gateway);

        doAnswer(invocation -> {
            events.add("bind");
            return page;
        }).when(gateway).bind(TestPage.class);

        doAnswer(invocation -> {
            events.add("navigateTo");
            return null;
        }).when(page).navigateTo();

        doThrow(new RuntimeException("assertLoaded failure"))
                .when(page).assertLoaded();

        doAnswer(invocation -> {
            events.add("close");
            return null;
        }).when(gateway).close();

        WebScenario<TestPage> scenario =
                new WebScenario<>("scenario", p -> {}, p -> {});

        assertThatThrownBy(() ->
                new WebContractRuntimeCaseExecutor(gatewayProvider, contextConfigurer)
                        .execute(TestPage.class, scenario)
        )
                .isInstanceOf(RuntimeException.class)
                .hasMessage("assertLoaded failure");

        assertThat(events).containsExactly(
                "bind",
                "navigateTo",
                "close"
        );
    }

    @Test
    void shouldCloseGatewayWhenActionFails() {
        Supplier<WebPresentationGateway> gatewayProvider = mock(Supplier.class);
        WebContractContextConfigurer contextConfigurer = mock(WebContractContextConfigurer.class);
        WebPresentationGateway gateway = mock(WebPresentationGateway.class);
        TestPage page = mock(TestPage.class);
        List<String> events = new ArrayList<>();

        when(gatewayProvider.get()).thenReturn(gateway);
        when(gateway.bind(TestPage.class)).thenReturn(page);

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
                p -> {
                    throw new RuntimeException("action failure");
                },
                p -> events.add("assertion")
        );

        assertThatThrownBy(() ->
                new WebContractRuntimeCaseExecutor(gatewayProvider, contextConfigurer)
                        .execute(TestPage.class, scenario)
        )
                .isInstanceOf(RuntimeException.class)
                .hasMessage("action failure");

        assertThat(events).containsExactly(
                "navigateTo",
                "assertLoaded",
                "close"
        );
    }

    @Test
    void shouldCloseGatewayWhenAssertionFails() {
        Supplier<WebPresentationGateway> gatewayProvider = mock(Supplier.class);
        WebContractContextConfigurer contextConfigurer = mock(WebContractContextConfigurer.class);
        WebPresentationGateway gateway = mock(WebPresentationGateway.class);
        TestPage page = mock(TestPage.class);
        List<String> events = new ArrayList<>();

        when(gatewayProvider.get()).thenReturn(gateway);
        when(gateway.bind(TestPage.class)).thenReturn(page);

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
                p -> {
                    throw new RuntimeException("assertion failure");
                }
        );

        assertThatThrownBy(() ->
                new WebContractRuntimeCaseExecutor(gatewayProvider, contextConfigurer)
                        .execute(TestPage.class, scenario)
        )
                .isInstanceOf(RuntimeException.class)
                .hasMessage("assertion failure");

        assertThat(events).containsExactly(
                "navigateTo",
                "assertLoaded",
                "action",
                "close"
        );
    }

    @Test
    void shouldAcquireFreshGatewayForEachScenario() {
        Supplier<WebPresentationGateway> gatewayProvider = mock(Supplier.class);
        WebContractContextConfigurer contextConfigurer = mock(WebContractContextConfigurer.class);

        WebPresentationGateway firstGateway = mock(WebPresentationGateway.class);
        WebPresentationGateway secondGateway = mock(WebPresentationGateway.class);

        TestPage firstPage = mock(TestPage.class);
        TestPage secondPage = mock(TestPage.class);

        when(gatewayProvider.get())
                .thenReturn(firstGateway, secondGateway);

        when(firstGateway.bind(TestPage.class))
                .thenReturn(firstPage);

        when(secondGateway.bind(TestPage.class))
                .thenReturn(secondPage);

        WebContractRuntimeCaseExecutor executor =
                new WebContractRuntimeCaseExecutor(
                        gatewayProvider,
                        contextConfigurer
                );

        executor.execute(
                TestPage.class,
                new WebScenario<>("first", p -> {}, p -> {})
        );

        executor.execute(
                TestPage.class,
                new WebScenario<>("second", p -> {}, p -> {})
        );

        verify(contextConfigurer, times(2)).configure();
        verify(gatewayProvider, times(2)).get();
        verify(firstGateway).close();
        verify(secondGateway).close();
    }

    private interface TestPage extends Page {
    }
}