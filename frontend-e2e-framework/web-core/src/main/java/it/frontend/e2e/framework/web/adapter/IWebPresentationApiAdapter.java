package it.frontend.e2e.framework.web.adapter;

import it.frontend.e2e.framework.core.adapter.IPresentationApiAdapter;
import it.frontend.e2e.framework.core.model.selector.XPathSelector;
import it.frontend.e2e.framework.web.adapter.model.FindPolicy;
import it.frontend.e2e.framework.web.model.WebPresentationElement;
import it.frontend.e2e.framework.web.model.location.Url;

import java.util.List;
import java.util.Optional;

public interface IWebPresentationApiAdapter
        extends IPresentationApiAdapter<XPathSelector, Url, WebPresentationElement> {

    Optional<String> getCookieValue(String name);
    Optional<WebPresentationElement> findElement(XPathSelector selector, FindPolicy findPolicy);
    Optional<List<WebPresentationElement>> findElements(XPathSelector selector, FindPolicy policy);
    /**
     * Checks DOM presence without explicit waits or retries, regardless of visibility.
     * Requires a zero implicit wait for an immediate result. Lookup failures propagate.
     */
    boolean isPresentNow(XPathSelector selector);

    Optional<String> getLocalStorageItem(String key);
    Optional<String> getSessionStorageItem(String key);
    void setLocalStorageItem(String key, String value);
    void setSessionStorageItem(String key, String value);
}