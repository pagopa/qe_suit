package it.frontend.e2e.framework.core.capability.core;

import it.frontend.e2e.framework.core.capability.Capability;
import it.frontend.e2e.framework.core.model.selector.XPathSelector;

public interface Waitable extends Capability {
    void waitUntilElementDisappears(XPathSelector selector, long timeoutSeconds);
}