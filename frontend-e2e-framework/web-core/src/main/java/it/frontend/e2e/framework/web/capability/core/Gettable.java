package it.frontend.e2e.framework.web.capability.core;

import it.frontend.e2e.framework.core.model.AbstractPresentationElement;
import it.frontend.e2e.framework.core.model.selector.XPathSelector;
import it.frontend.e2e.framework.web.adapter.model.FindPolicy;
import it.frontend.e2e.framework.web.model.WebPresentationElement;
import it.frontend.e2e.framework.web.model.location.Url;

import java.util.Optional;

public interface Gettable<E extends AbstractPresentationElement<XPathSelector,Url>> extends it.frontend.e2e.framework.core.capability.core.Gettable<XPathSelector, Url, E> {
    Optional<E> get(FindPolicy policy);
}
