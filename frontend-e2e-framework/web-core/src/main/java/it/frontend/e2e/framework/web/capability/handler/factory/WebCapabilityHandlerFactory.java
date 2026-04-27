package it.frontend.e2e.framework.web.capability.handler.factory;

import it.frontend.e2e.framework.web.capability.handler.ClickableCapabilityHandler;
import it.frontend.e2e.framework.web.capability.handler.GettableCapabilityHandler;
import it.frontend.e2e.framework.web.capability.handler.LocatableCapabilityHanlder;
import it.frontend.e2e.framework.web.capability.handler.ReadableCapabilityHandler;
import it.frontend.e2e.framework.web.capability.handler.WritableCapabilityHandler;

import java.util.List;

public class WebCapabilityHandlerFactory  {

    public static List<IWebCapabilityHandlerSupplier> defaults() {
        return List.of(
                ClickableCapabilityHandler::new,
                GettableCapabilityHandler::new,
                WritableCapabilityHandler::new,
                ReadableCapabilityHandler::new,
                LocatableCapabilityHanlder::new
        );
    }
}
