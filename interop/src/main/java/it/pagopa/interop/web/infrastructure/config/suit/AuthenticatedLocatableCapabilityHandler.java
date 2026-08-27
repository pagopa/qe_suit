package it.pagopa.interop.web.infrastructure.config.suit;

import it.frontend.e2e.framework.core.capability.core.Locatable;
import it.frontend.e2e.framework.core.capability.handler.AbstractCapabilityHandler;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("cucumber")
public class AuthenticatedLocatableCapabilityHandler extends AbstractCapabilityHandler<Locatable> {

    public AuthenticatedLocatableCapabilityHandler(AuthenticatedLocatableCapabilityImpl capability) {
        super(capability);
    }
}
