package it.frontend.e2e.framework.web.config;

import it.frontend.e2e.framework.core.capability.handler.ICapabilityHandler;
import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.frontend.e2e.framework.web.adapter.IWebPresentationApiAdapter;
import it.frontend.e2e.framework.web.capability.handler.ClickableCapabilityHandler;
import it.frontend.e2e.framework.web.capability.handler.GettableCapabilityHandler;
import it.frontend.e2e.framework.web.capability.handler.ReadableCapabilityHandler;
import it.frontend.e2e.framework.web.capability.handler.WritableCapabilityHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@DisplayName("WebSuiteConfigurationBuilder")
class WebSuiteBuilderTest {

    @BeforeEach
    void resetContext() {
        WebSuiteContext.reset();
    }

    @Test
    @DisplayName("build crea gateway con adapter e handlers di default")
    void shouldBuildGatewayWithDefaultHandlers() {
        IWebPresentationApiAdapter adapter = mock(IWebPresentationApiAdapter.class);

        WebPresentationGateway gateway = WebSuiteBuilder.builder()
                .withAdapter(() -> adapter)
                .build();

        assertNotNull(gateway);

        WebSuiteConfiguration configuration = WebSuiteContext.getConfiguration();

        List<ICapabilityHandler> handlers = configuration.getCapabilityHandlers();
        assertEquals(5, handlers.size());
        assertInstanceOf(ClickableCapabilityHandler.class, handlers.get(0));
        assertInstanceOf(GettableCapabilityHandler.class, handlers.get(1));
        assertInstanceOf(WritableCapabilityHandler.class, handlers.get(2));
        assertInstanceOf(ReadableCapabilityHandler.class, handlers.get(3));
    }

    @Test
    @DisplayName("build senza withAdapter lancia IllegalStateException")
    void shouldThrowWhenAdapterSupplierIsNotConfigured() {
        WebSuiteBuilder builder = WebSuiteBuilder.builder();

        assertThrows(IllegalStateException.class, builder::build);
    }

    @Test
    @DisplayName("withAdapter null lancia NullPointerException")
    void shouldThrowWhenWithAdapterReceivesNull() {
        WebSuiteBuilder builder = WebSuiteBuilder.builder();

        assertThrows(NullPointerException.class, () -> builder.withAdapter(null));
    }

    @Test
    @DisplayName("build lancia NullPointerException se supplier ritorna null")
    void shouldThrowWhenAdapterSupplierReturnsNull() {
        WebSuiteBuilder builder = WebSuiteBuilder.builder()
                .withAdapter(() -> null);

        assertThrows(NullPointerException.class, builder::build);
    }
}

