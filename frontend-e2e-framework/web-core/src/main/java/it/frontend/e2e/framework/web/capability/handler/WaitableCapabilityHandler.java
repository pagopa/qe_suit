package it.frontend.e2e.framework.web.capability.handler;

import it.frontend.e2e.framework.core.capability.core.Waitable;
import it.frontend.e2e.framework.core.capability.handler.AbstractCapabilityHandler;
import it.frontend.e2e.framework.web.adapter.IWebPresentationApiAdapter;
import it.frontend.e2e.framework.web.capability.impl.WaitableImpl;

public class WaitableCapabilityHandler extends AbstractCapabilityHandler<Waitable> {
    public WaitableCapabilityHandler(IWebPresentationApiAdapter adapter) {
        super(new WaitableImpl(adapter));
    }
}
