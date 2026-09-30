package it.frontend.e2e.framework.web.capability.impl;

import it.frontend.e2e.framework.core.model.AbstractPresentationElement;
import it.frontend.e2e.framework.core.model.selector.XPathSelector;
import it.frontend.e2e.framework.web.adapter.IWebPresentationApiAdapter;
import it.frontend.e2e.framework.web.adapter.model.FindPolicy;
import it.frontend.e2e.framework.web.capability.core.Gettable;
import it.frontend.e2e.framework.web.model.WebPresentationElement;
import it.frontend.e2e.framework.web.model.location.Url;

import java.util.List;
import java.util.Optional;

public class GettableImpl extends AbstractCapabilityImpl implements Gettable {
    public GettableImpl(IWebPresentationApiAdapter adapter) {
        super(adapter);
    }

    @Override
    public Optional<WebPresentationElement> get() {
        return adapter.findElement(xPathSelector.get());
    }

    @Override
    public Optional<List<WebPresentationElement>> getAll() {
        return adapter.findElements(xPathSelector.get());
    }

    @Override
    public Optional<WebPresentationElement> get(FindPolicy policy) {
        return adapter.findElement(xPathSelector.get(), policy);
    }
}