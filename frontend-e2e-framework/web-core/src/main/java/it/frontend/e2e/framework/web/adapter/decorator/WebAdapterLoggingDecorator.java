package it.frontend.e2e.framework.web.adapter.decorator;

import it.frontend.e2e.framework.core.adapter.decorator.AbstractAdapterLoggingDecorator;
import it.frontend.e2e.framework.core.model.selector.XPathSelector;
import it.frontend.e2e.framework.web.adapter.IWebPresentationApiAdapter;
import it.frontend.e2e.framework.web.adapter.model.FindPolicy;
import it.frontend.e2e.framework.web.model.WebPresentationElement;
import it.frontend.e2e.framework.web.model.location.Url;

import java.util.List;
import java.util.Optional;

public class WebAdapterLoggingDecorator extends AbstractAdapterLoggingDecorator<XPathSelector, Url, WebPresentationElement> implements IWebPresentationApiAdapter {

    private final IWebPresentationApiAdapter webAdapter;

    public WebAdapterLoggingDecorator(IWebPresentationApiAdapter wrappedAdapter) {
        super(wrappedAdapter);
        this.webAdapter = wrappedAdapter;
    }

    @Override
    public Optional<String> getCookieValue(String name) {
        logger.logDebug("Get cookie value by name: " + name);
        return webAdapter.getCookieValue(name);
    }

    @Override
    public Optional<WebPresentationElement> findElement(XPathSelector selector, FindPolicy findPolicy) {
        logger.logDebug("Find element by selector: " + selector + " with find policy: " + findPolicy);
        return webAdapter.findElement(selector, findPolicy);
    }

    @Override
    public Optional<List<WebPresentationElement>> findElements(XPathSelector selector, FindPolicy policy) {
        logger.logDebug("Find elements by selector: " + selector + " with find policy: " + policy);
        return webAdapter.findElements(selector, policy);
    }

    @Override
    public Optional<String> getLocalStorageItem(String key) {
        logger.logDebug("Get localStorage item by key: " + key);
        return webAdapter.getLocalStorageItem(key);
    }

    @Override
    public Optional<String> getSessionStorageItem(String key) {
        logger.logDebug("Get sessionStorage item by key: " + key);
        return webAdapter.getSessionStorageItem(key);
    }

    @Override
    public void setLocalStorageItem(String key, String value) {
        logger.logDebug("Set localStorage item: " + key + " = " + value);
        webAdapter.setLocalStorageItem(key, value);
    }

    @Override
    public void setSessionStorageItem(String key, String value) {
        logger.logDebug("Set sessionStorage item: " + key + " = " + value);
        webAdapter.setSessionStorageItem(key, value);
    }

    @Override
    public void close() {
        logger.logDebug("Closing Browser");
        webAdapter.close();
    }
}
