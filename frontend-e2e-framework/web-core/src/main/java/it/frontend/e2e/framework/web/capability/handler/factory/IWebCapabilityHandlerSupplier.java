package it.frontend.e2e.framework.web.capability.handler.factory;

import it.frontend.e2e.framework.core.capability.handler.ICapabilityHandler;
import it.frontend.e2e.framework.web.adapter.IWebPresentationApiAdapter;

public interface IWebCapabilityHandlerSupplier {
    ICapabilityHandler create(IWebPresentationApiAdapter adapter);
}
