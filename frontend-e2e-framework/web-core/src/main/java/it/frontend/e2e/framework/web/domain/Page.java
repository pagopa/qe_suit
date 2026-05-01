package it.frontend.e2e.framework.web.domain;

import it.frontend.e2e.framework.core.capability.core.Locatable;
import it.frontend.e2e.framework.core.capability.core.Waitable;
import it.frontend.e2e.framework.core.model.DomainElement;
import it.frontend.e2e.framework.core.model.selector.XPathSelector;

public interface Page extends DomainElement, Locatable, Waitable {
    default void assertLoaded() {
        throw new UnsupportedOperationException("Method assertLoaded() not implemented for " + this.getClass().getName());
    }

    default void waitUntilReady() {
        waitUntilElementDisappears(new XPathSelector("//*[@id='spinner-loading']"), 10);
    }
}
