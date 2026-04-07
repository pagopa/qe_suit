package it.frontend.e2e.framework.web.capability.impl;

import it.frontend.e2e.framework.core.capability.context.CapabilityContext;
import it.frontend.e2e.framework.core.model.selector.XPathSelector;
import it.frontend.e2e.framework.web.adapter.IWebPresentationApiAdapter;
import it.frontend.e2e.framework.web.adapter.decorator.WebAdapterLoggingDecorator;
import it.frontend.e2e.framework.web.model.location.Url;

import java.util.function.Supplier;

public abstract class AbstractCapabilityImpl {
    protected final IWebPresentationApiAdapter adapter;
    protected final Supplier<XPathSelector> xPathSelector;
    protected final Supplier<Url> urlSupplier;

    public AbstractCapabilityImpl(IWebPresentationApiAdapter adapter) {
        this.adapter = new WebAdapterLoggingDecorator(adapter);
        this.xPathSelector = () -> XPathSelector.of(CapabilityContext.selector());
        this.urlSupplier = () -> Url.of(CapabilityContext.location());
    }

}
