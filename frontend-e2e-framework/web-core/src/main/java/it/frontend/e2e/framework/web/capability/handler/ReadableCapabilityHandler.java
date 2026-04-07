package it.frontend.e2e.framework.web.capability.handler;

import it.frontend.e2e.framework.core.capability.handler.AbstractCapabilityHandler;
import it.frontend.e2e.framework.web.adapter.IWebPresentationApiAdapter;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.capability.impl.ReadableImpl;

public class ReadableCapabilityHandler extends AbstractCapabilityHandler<Readable> {

    public ReadableCapabilityHandler(IWebPresentationApiAdapter adapter) {
        super(new ReadableImpl(adapter));
    }
}
