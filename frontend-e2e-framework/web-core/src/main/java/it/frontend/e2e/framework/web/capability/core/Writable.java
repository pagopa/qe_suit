package it.frontend.e2e.framework.web.capability.core;

import it.frontend.e2e.framework.web.model.location.Url;
import it.frontend.e2e.framework.web.model.WebPresentationElement;
import it.frontend.e2e.framework.core.model.selector.XPathSelector;

public interface Writable<T> extends it.frontend.e2e.framework.core.capability.core.Writable<T, XPathSelector, Url, WebPresentationElement> {
}
