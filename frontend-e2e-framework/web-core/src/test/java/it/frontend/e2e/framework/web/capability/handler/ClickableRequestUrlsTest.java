package it.frontend.e2e.framework.web.capability.handler;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.binder.DefaultBinderInvocationHandler;
import it.frontend.e2e.framework.core.binder.context.BindContext;
import it.frontend.e2e.framework.core.capability.context.CapabilityScope;
import it.frontend.e2e.framework.core.capability.dispatcher.DefaultCapabilityDispatcher;
import it.frontend.e2e.framework.core.model.selector.XPathSelector;
import it.frontend.e2e.framework.web.adapter.IWebPresentationApiAdapter;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.capability.core.RequestCapturingClickable;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClickableRequestUrlsTest {
    @XPath("//button")
    interface TestButton extends RequestCapturingClickable {}

    interface LegacyButton extends Clickable {}

    @Test
    void dispatchesRequestCollectionThroughProxyHandlerAndDecorator() {
        IWebPresentationApiAdapter adapter = mock(IWebPresentationApiAdapter.class);
        XPathSelector selector = XPathSelector.of("//button");
        List<String> urls = List.of("https://example.test/api?state=REJECTED");
        when(adapter.clickAndGetRequestUrls(any(XPathSelector.class))).thenReturn(urls);
        TestButton proxy = (TestButton) Proxy.newProxyInstance(
                TestButton.class.getClassLoader(), new Class<?>[]{TestButton.class},
                new DefaultBinderInvocationHandler(
                        new DefaultCapabilityDispatcher(List.of(new ClickableCapabilityHandler(adapter))),
                        new BindContext(new CapabilityScope("//button", "", false), TestButton.class)));

        assertEquals(urls, proxy.clickAndGetRequestUrls());
        proxy.click();

        verify(adapter).clickAndGetRequestUrls(argThat(actual -> actual.toString().equals(selector.toString())));
        verify(adapter).click(argThat(actual -> actual.toString().equals(selector.toString())));
        verifyNoMoreInteractions(adapter);
    }

    @Test
    void legacyAdaptersFailExplicitlyOnlyForTheNewOperation() {
        IWebPresentationApiAdapter adapter = mock(IWebPresentationApiAdapter.class, CALLS_REAL_METHODS);
        assertThrows(UnsupportedOperationException.class,
                () -> adapter.clickAndGetRequestUrls(XPathSelector.of("//button")));
    }

    @Test
    void legacyClickableProxyStillDispatchesOnlyTheOriginalClick() {
        IWebPresentationApiAdapter adapter = mock(IWebPresentationApiAdapter.class);
        LegacyButton proxy = (LegacyButton) Proxy.newProxyInstance(
                LegacyButton.class.getClassLoader(), new Class<?>[]{LegacyButton.class},
                new DefaultBinderInvocationHandler(
                        new DefaultCapabilityDispatcher(List.of(new ClickableCapabilityHandler(adapter))),
                        new BindContext(new CapabilityScope("//a", "", false), LegacyButton.class)));

        proxy.click();

        verify(adapter).click(argThat(actual -> actual.toString().equals("//a")));
        verifyNoMoreInteractions(adapter);
        assertTrue(Arrays.stream(LegacyButton.class.getMethods())
                .noneMatch(method -> method.getName().equals("clickAndGetRequestUrls")));
    }

    @Test
    void existingConcreteClickableImplementationsNeedNoNewMethod() {
        Runnable action = mock(Runnable.class);
        Clickable legacy = action::run;

        legacy.click();
        verify(action).run();
    }
}