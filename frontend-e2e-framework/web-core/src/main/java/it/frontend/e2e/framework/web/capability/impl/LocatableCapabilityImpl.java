package it.frontend.e2e.framework.web.capability.impl;

import it.frontend.e2e.framework.core.capability.core.Locatable;
import it.frontend.e2e.framework.web.adapter.IWebPresentationApiAdapter;
import it.frontend.e2e.framework.web.model.location.Url;

public class LocatableCapabilityImpl extends AbstractCapabilityImpl implements Locatable {
    public LocatableCapabilityImpl(IWebPresentationApiAdapter adapter) {
        super(adapter);
    }

    @Override
    public void navigateTo() {
        Url url = urlSupplier.get();
        adapter.navigateTo(url);
    }
}
