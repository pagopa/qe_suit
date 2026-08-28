package it.frontend.e2e.framework.web.adapter.decorator;

import it.frontend.e2e.framework.core.adapter.decorator.AbstractAdapterLoggingDecorator;
import it.frontend.e2e.framework.core.model.selector.XPathSelector;
import it.frontend.e2e.framework.web.adapter.IWebPresentationApiAdapter;
import it.frontend.e2e.framework.web.model.WebPresentationElement;
import it.frontend.e2e.framework.web.model.location.Url;

import java.util.Optional;

public class WebAdapterLoggingDecorator extends AbstractAdapterLoggingDecorator<XPathSelector, Url, WebPresentationElement> implements IWebPresentationApiAdapter {

    private final IWebPresentationApiAdapter webAdapter;

    public WebAdapterLoggingDecorator(IWebPresentationApiAdapter wrappedAdapter) {
        super(wrappedAdapter);
        this.webAdapter = wrappedAdapter;
    }

    @Override
    public Optional<String> getCookieValue(String name) {
        logger.logInfo("Get cookie value by name: " + name);
        return webAdapter.getCookieValue(name);
    }

    @Override
    public Optional<String> getLocalStorageItem(String key) {
        logger.logInfo("Get localStorage item by key: " + key);
        return webAdapter.getLocalStorageItem(key);
    }

    @Override
    public Optional<String> getSessionStorageItem(String key) {
        logger.logInfo("Get sessionStorage item by key: " + key);
        return webAdapter.getSessionStorageItem(key);
    }

    @Override
    public void setLocalStorageItem(String key, String value) {
        logger.logInfo("Set localStorage item: " + key + " = " + value);
        webAdapter.setLocalStorageItem(key, value);
    }

    @Override
    public void setSessionStorageItem(String key, String value) {
        logger.logInfo("Set sessionStorage item: " + key + " = " + value);
        webAdapter.setSessionStorageItem(key, value);
    }

    @Override
    public void close() {
        logger.logInfo("Closing Browser");
        webAdapter.close();
    }
}
