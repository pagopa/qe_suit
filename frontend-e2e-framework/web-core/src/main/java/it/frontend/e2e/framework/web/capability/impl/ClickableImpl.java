package it.frontend.e2e.framework.web.capability.impl;

import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.adapter.IWebPresentationApiAdapter;

public class ClickableImpl extends AbstractCapabilityImpl implements Clickable {

    public ClickableImpl(IWebPresentationApiAdapter adapter) {
        super(adapter);
    }

    @Override
    public void click() {
        adapter.click(xPathSelector.get());
    }
}
