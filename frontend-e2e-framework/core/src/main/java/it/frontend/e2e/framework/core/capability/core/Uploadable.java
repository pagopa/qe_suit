package it.frontend.e2e.framework.core.capability.core;

import it.frontend.e2e.framework.core.capability.Capability;
import it.frontend.e2e.framework.core.model.selector.Selector;

public interface Uploadable<S extends Selector> extends Capability {
    void upload(String absolutePath);
}
