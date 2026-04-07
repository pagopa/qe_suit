package it.frontend.e2e.framework.web.capability.handler;

import it.frontend.e2e.framework.core.capability.handler.AbstractCapabilityHandler;
import it.frontend.e2e.framework.web.adapter.IWebPresentationApiAdapter;
import it.frontend.e2e.framework.web.capability.core.Writable;
import it.frontend.e2e.framework.web.capability.impl.WritableImpl;

public class WritableCapabilityHandler extends AbstractCapabilityHandler<Writable> {

    public WritableCapabilityHandler(IWebPresentationApiAdapter adapter) {
        super(new WritableImpl(adapter));
    }
}
