package it.frontend.e2e.framework.web.capability.impl;

import it.frontend.e2e.framework.web.adapter.IWebPresentationApiAdapter;
import it.frontend.e2e.framework.web.capability.core.Gettable;
import it.frontend.e2e.framework.web.model.WebPresentationElement;

import java.util.Optional;

public class GettableImpl extends AbstractCapabilityImpl implements Gettable {
    public GettableImpl(IWebPresentationApiAdapter adapter) {
        super(adapter);
    }

    @Override
    public Optional<WebPresentationElement> get() {
        return Optional.empty();
    }
}
