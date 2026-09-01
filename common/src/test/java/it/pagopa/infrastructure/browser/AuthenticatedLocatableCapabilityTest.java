package it.pagopa.infrastructure.browser;

import it.frontend.e2e.framework.core.capability.context.CapabilityContext;
import it.frontend.e2e.framework.core.capability.context.CapabilityScope;
import it.frontend.e2e.framework.web.adapter.IWebPresentationApiAdapter;
import it.frontend.e2e.framework.web.model.location.Url;
import it.pagopa.application.context.BrowserContext;
import it.pagopa.infrastructure.contract.browser.WebSessionProvider;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class AuthenticatedLocatableCapabilityTest {

    @Test
    void configurerRunsBeforeNavigationAndUpdatesCurrentUrl() {
        IWebPresentationApiAdapter adapter = mock(IWebPresentationApiAdapter.class);
        BrowserContext browserContext = mock(BrowserContext.class);
        WebSessionProvider sessionConfigurer = mock(WebSessionProvider.class);
        AuthenticatedLocatableCapability capability =
                new AuthenticatedLocatableCapability(adapter, browserContext, sessionConfigurer);

        CapabilityContext.push(new CapabilityScope(null, "https://portal.example/${id}", false));

        try {
            capability.navigateTo("42");
        } finally {
            CapabilityContext.pop();
        }

        InOrder inOrder = inOrder(sessionConfigurer, adapter, browserContext);
        inOrder.verify(sessionConfigurer).provide();
        inOrder.verify(adapter).navigateTo(Url.of("https://portal.example/42"));
        inOrder.verify(browserContext).setCurrentUrl(Url.of("https://portal.example/42"));
    }

    @Test
    void resolvesUrlUsingCapabilityLocationTemplate() {
        IWebPresentationApiAdapter adapter = mock(IWebPresentationApiAdapter.class);
        BrowserContext browserContext = mock(BrowserContext.class);
        AuthenticatedLocatableCapability capability =
                new AuthenticatedLocatableCapability(adapter, browserContext, () -> {});

        CapabilityContext.push(new CapabilityScope(null, "https://portal.example/${id}/details", false));

        try {
            capability.navigateTo("abc");
        } finally {
            CapabilityContext.pop();
        }

        verify(adapter).navigateTo(Url.of("https://portal.example/abc/details"));
        verify(browserContext).setCurrentUrl(Url.of("https://portal.example/abc/details"));
        assertEquals("https://portal.example/abc/details", Url.of("https://portal.example/abc/details").getUrl());
    }
}
