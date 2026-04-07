package it.frontend.e2e.framework.web.capability.handler.factory;

import it.frontend.e2e.framework.core.capability.handler.ICapabilityHandler;
import it.frontend.e2e.framework.web.adapter.IWebPresentationApiAdapter;
import it.frontend.e2e.framework.web.capability.handler.ClickableCapabilityHandler;
import it.frontend.e2e.framework.web.capability.handler.GettableCapabilityHandler;
import it.frontend.e2e.framework.web.capability.handler.ReadableCapabilityHandler;
import it.frontend.e2e.framework.web.capability.handler.WritableCapabilityHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.mock;

@DisplayName("WebCapabilityHandlerFactory")
class WebCapabilityHandlerFactoryTest {

    @Test
    @DisplayName("defaults restituisce le factory attese in ordine")
    void shouldReturnDefaultFactoriesInExpectedOrder() {
        List<IWebCapabilityHandlerSupplier> defaults = WebCapabilityHandlerFactory.defaults();

        assertEquals(5, defaults.size());

        IWebPresentationApiAdapter adapter = mock(IWebPresentationApiAdapter.class);
        List<ICapabilityHandler> handlers = defaults.stream()
                .map(factory -> factory.create(adapter))
                .toList();

        assertInstanceOf(ClickableCapabilityHandler.class, handlers.get(0));
        assertInstanceOf(GettableCapabilityHandler.class, handlers.get(1));
        assertInstanceOf(WritableCapabilityHandler.class, handlers.get(2));
        assertInstanceOf(ReadableCapabilityHandler.class, handlers.get(3));
    }
}
