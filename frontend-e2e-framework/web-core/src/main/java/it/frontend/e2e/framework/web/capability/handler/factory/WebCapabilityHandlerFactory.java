package it.frontend.e2e.framework.web.capability.handler.factory;

import it.frontend.e2e.framework.web.capability.handler.*;

import java.util.List;

public class WebCapabilityHandlerFactory  {

   public static List<IWebCapabilityHandlerSupplier> defaults() {
    return List.of(
            ClickableCapabilityHandler::new,
            GettableCapabilityHandler::new,
            WritableCapabilityHandler::new,
            adapter -> new ReadableCapabilityHandler<>(adapter, String.class),
            LocatableCapabilityHandler::new,
            UploadableCapabilityHandler::new
    );
}
}
