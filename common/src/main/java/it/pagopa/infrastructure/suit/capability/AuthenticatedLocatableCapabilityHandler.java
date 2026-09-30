package it.pagopa.infrastructure.suit.capability;

import it.frontend.e2e.framework.core.capability.core.Locatable;
import it.frontend.e2e.framework.core.capability.handler.AbstractCapabilityHandler;

public class AuthenticatedLocatableCapabilityHandler extends AbstractCapabilityHandler<Locatable> {
    public AuthenticatedLocatableCapabilityHandler(AuthenticatedLocatableCapability capability) {
        super(capability);
    }
}
