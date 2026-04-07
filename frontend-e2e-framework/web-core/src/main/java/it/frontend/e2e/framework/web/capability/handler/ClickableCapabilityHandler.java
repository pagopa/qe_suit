package it.frontend.e2e.framework.web.capability.handler;

import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.core.capability.handler.AbstractCapabilityHandler;
import it.frontend.e2e.framework.web.adapter.IWebPresentationApiAdapter;
import it.frontend.e2e.framework.web.capability.impl.ClickableImpl;

public class ClickableCapabilityHandler extends AbstractCapabilityHandler<Clickable> {

    public ClickableCapabilityHandler(IWebPresentationApiAdapter adapter) {
        super(new ClickableImpl(adapter));
    }
}
