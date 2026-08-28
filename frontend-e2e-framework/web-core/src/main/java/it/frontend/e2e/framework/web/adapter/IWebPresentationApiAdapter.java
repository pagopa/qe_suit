package it.frontend.e2e.framework.web.adapter;

import it.frontend.e2e.framework.core.adapter.IPresentationApiAdapter;
import it.frontend.e2e.framework.core.model.selector.XPathSelector;
import it.frontend.e2e.framework.web.model.WebPresentationElement;
import it.frontend.e2e.framework.web.model.location.Url;

import java.util.Optional;

public interface IWebPresentationApiAdapter
        extends IPresentationApiAdapter<XPathSelector, Url, WebPresentationElement> {

    Optional<String> getCookieValue(String name);
    Optional<String> getLocalStorageItem(String key);
    Optional<String> getSessionStorageItem(String key);
    void setLocalStorageItem(String key, String value);
    void setSessionStorageItem(String key, String value);
}