package it.frontend.e2e.framework.web.domain;

import it.frontend.e2e.framework.core.capability.core.Locatable;
import it.frontend.e2e.framework.core.model.DomainElement;

public interface SpidLoginPage extends DomainElement, Locatable {
    default void loginWithSpid(User user) {
        throw new UnsupportedOperationException("Method loginWithSpid() not implemented for " + this.getClass().getName());
    }
}
