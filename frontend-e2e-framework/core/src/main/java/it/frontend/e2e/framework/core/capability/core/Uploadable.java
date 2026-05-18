package it.frontend.e2e.framework.core.capability.core;

import it.frontend.e2e.framework.core.capability.Capability;

public interface Uploadable extends Capability {
    void upload(String absolutePath);
}
