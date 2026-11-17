package it.frontend.e2e.framework.web.capability.impl;

import it.frontend.e2e.framework.web.adapter.IWebPresentationApiAdapter;
import it.frontend.e2e.framework.web.capability.core.AccessibleNameReadable;

public final class AccessibleNameReadableImpl extends AbstractCapabilityImpl
        implements AccessibleNameReadable {

    public AccessibleNameReadableImpl(IWebPresentationApiAdapter adapter) {
        super(adapter);
    }

    @Override
    public String readAccessibleName() {
        return adapter.getAccessibleName(xPathSelector.get());
    }
}
