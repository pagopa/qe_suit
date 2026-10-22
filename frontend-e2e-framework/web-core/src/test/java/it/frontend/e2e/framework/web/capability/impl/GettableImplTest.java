package it.frontend.e2e.framework.web.capability.impl;

import it.frontend.e2e.framework.core.capability.context.CapabilityContext;
import it.frontend.e2e.framework.core.capability.context.CapabilityScope;
import it.frontend.e2e.framework.core.model.selector.XPathSelector;
import it.frontend.e2e.framework.web.adapter.IWebPresentationApiAdapter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.openqa.selenium.WebDriverException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class GettableImplTest {

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void shouldDelegateImmediatePresenceUsingResolvedSelector(boolean present) {
        IWebPresentationApiAdapter adapter = mock(IWebPresentationApiAdapter.class);
        XPathSelector selector = XPathSelector.of("//div[@id='root']//button");
        when(adapter.isPresentNow(argThat(actual -> selector.getSelector().equals(actual.getSelector()))))
                .thenReturn(present);

        CapabilityContext.push(new CapabilityScope(selector.getSelector(), "https://example.test", false));
        try {
            assertEquals(present, new GettableImpl(adapter).isPresentNow());
            verify(adapter).isPresentNow(argThat(actual -> selector.getSelector().equals(actual.getSelector())));
            verifyNoMoreInteractions(adapter);
        } finally {
            CapabilityContext.pop();
        }
    }

    @Test
    void shouldPropagatePresenceLookupFailure() {
        IWebPresentationApiAdapter adapter = mock(IWebPresentationApiAdapter.class);
        XPathSelector selector = XPathSelector.of("//button");
        WebDriverException failure = new WebDriverException("browser session failed");
        when(adapter.isPresentNow(argThat(actual -> selector.getSelector().equals(actual.getSelector()))))
                .thenThrow(failure);

        CapabilityContext.push(new CapabilityScope(selector.getSelector(), "https://example.test", false));
        try {
            assertSame(failure, assertThrows(WebDriverException.class,
                    () -> new GettableImpl(adapter).isPresentNow()));
            verify(adapter).isPresentNow(argThat(actual -> selector.getSelector().equals(actual.getSelector())));
            verifyNoMoreInteractions(adapter);
        } finally {
            CapabilityContext.pop();
        }
    }
}
