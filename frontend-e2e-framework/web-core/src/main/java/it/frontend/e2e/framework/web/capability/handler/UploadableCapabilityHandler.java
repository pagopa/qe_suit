package it.frontend.e2e.framework.web.capability.handler;

import it.frontend.e2e.framework.core.capability.core.Uploadable;
import it.frontend.e2e.framework.core.capability.handler.AbstractCapabilityHandler;
import it.frontend.e2e.framework.web.adapter.IWebPresentationApiAdapter;
import it.frontend.e2e.framework.web.capability.impl.UploadableImpl;

public class UploadableCapabilityHandler extends AbstractCapabilityHandler<Uploadable> {

    public UploadableCapabilityHandler(IWebPresentationApiAdapter adapter) {
        super(new UploadableImpl(adapter));
    }
}
