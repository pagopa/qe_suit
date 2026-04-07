package it.frontend.e2e.framework.web.capability.handler;

import it.frontend.e2e.framework.core.capability.core.Locatable;
import it.frontend.e2e.framework.core.capability.handler.AbstractCapabilityHandler;
import it.frontend.e2e.framework.web.adapter.IWebPresentationApiAdapter;
import it.frontend.e2e.framework.web.capability.impl.LocatableCapabilityImpl;

public class LocatableCapabilityHanlder extends AbstractCapabilityHandler<Locatable> {

    public LocatableCapabilityHanlder(IWebPresentationApiAdapter adapter) {
        super(new LocatableCapabilityImpl(adapter));
    }
}
