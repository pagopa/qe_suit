package it.frontend.e2e.framework.web.capability.handler;

import it.frontend.e2e.framework.core.capability.handler.AbstractCapabilityHandler;
import it.frontend.e2e.framework.web.adapter.IWebPresentationApiAdapter;
import it.frontend.e2e.framework.web.capability.core.Gettable;
import it.frontend.e2e.framework.web.capability.impl.GettableImpl;

public class GettableCapabilityHandler extends AbstractCapabilityHandler<Gettable> {

    public GettableCapabilityHandler(IWebPresentationApiAdapter adapter) {
        super(new GettableImpl(adapter));
    }
}
