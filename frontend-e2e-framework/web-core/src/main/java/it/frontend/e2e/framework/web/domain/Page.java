package it.frontend.e2e.framework.web.domain;

import it.frontend.e2e.framework.core.capability.core.Locatable;
import it.frontend.e2e.framework.core.model.DomainElement;

public interface Page extends DomainElement, Locatable {
    default void assertLoaded() {
        throw new UnsupportedOperationException("Method assertLoaded() not implemented for " + this.getClass().getName());
    }
}
