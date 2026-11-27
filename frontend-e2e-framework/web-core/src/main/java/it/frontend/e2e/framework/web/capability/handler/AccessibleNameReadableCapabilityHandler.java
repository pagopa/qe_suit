package it.frontend.e2e.framework.web.capability.handler;

import it.frontend.e2e.framework.core.capability.handler.AbstractCapabilityHandler;
import it.frontend.e2e.framework.web.adapter.IWebPresentationApiAdapter;
import it.frontend.e2e.framework.web.capability.core.AccessibleNameReadable;
import it.frontend.e2e.framework.web.capability.impl.AccessibleNameReadableImpl;

public final class AccessibleNameReadableCapabilityHandler
        extends AbstractCapabilityHandler<AccessibleNameReadable> {

    public AccessibleNameReadableCapabilityHandler(IWebPresentationApiAdapter adapter) {
        super(new AccessibleNameReadableImpl(adapter));
    }
}
