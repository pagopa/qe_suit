package it.frontend.e2e.framework.web.capability.handler;

import it.frontend.e2e.framework.core.capability.context.CapabilityScope;
import it.frontend.e2e.framework.web.adapter.IWebPresentationApiAdapter;
import it.frontend.e2e.framework.web.capability.core.AccessibleNameReadable;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AccessibleNameReadableCapabilityHandlerTest {
    @it.frontend.e2e.framework.annotation.selector.XPath("//button[@id='document']")
    public interface AccessibleButton extends it.frontend.e2e.framework.web.domain.Component,
            AccessibleNameReadable {
    }

    @Test
    void boundComponentUsesTheRegisteredCapability() {
        var adapter = mock(IWebPresentationApiAdapter.class);
        when(adapter.getAccessibleName(any())).thenReturn("Guida, scarica l'allegato");
        var gateway = it.frontend.e2e.framework.web.config.WebSuiteBuilder.builder()
                .withAdapter(() -> adapter).build();
        try {
            assertEquals("Guida, scarica l'allegato",
                    gateway.bind(AccessibleButton.class).readAccessibleName());
        } finally {
            it.frontend.e2e.framework.web.config.WebSuiteContext.reset();
        }
    }

    @Test
    void dispatchesTheCapabilityThroughTheDecoratorAndPreservesFailures() throws Exception {
        var adapter = mock(IWebPresentationApiAdapter.class);
        var handler = new AccessibleNameReadableCapabilityHandler(adapter);
        var method = AccessibleNameReadable.class.getMethod("readAccessibleName");
        var scope = new CapabilityScope("//button[@id='document']", "https://example.test", false);
        when(adapter.getAccessibleName(any())).thenAnswer(invocation -> {
            assertEquals(scope.selector(), invocation.getArgument(0,
                    it.frontend.e2e.framework.core.model.selector.XPathSelector.class).getSelector());
            return "Guida, scarica l'allegato";
        });
        assertTrue(handler.canHandle(method));
        assertEquals("Guida, scarica l'allegato", handler.<String>handle(method, null, scope));
        var failure = new IllegalStateException("driver error");
        doThrow(failure).when(adapter).getAccessibleName(any());
        assertSame(failure, assertThrows(IllegalStateException.class,
                () -> handler.handle(method, null, scope)));
    }
}
