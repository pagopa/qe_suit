package it.frontend.e2e.framework.web.capability.impl;

import it.frontend.e2e.framework.core.capability.core.Waitable;
import it.frontend.e2e.framework.core.model.selector.XPathSelector;
import it.frontend.e2e.framework.web.adapter.IWebPresentationApiAdapter;

public class WaitableImpl extends AbstractCapabilityImpl implements Waitable {
    public WaitableImpl(IWebPresentationApiAdapter adapter) { super(adapter); }

    @Override
    public void waitUntilElementDisappears(XPathSelector selector, long timeoutSeconds) {
        adapter.waitUntilElementDisappears(selector, timeoutSeconds);
    }
}
