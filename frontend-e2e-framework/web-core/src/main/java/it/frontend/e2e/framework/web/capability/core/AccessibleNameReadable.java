package it.frontend.e2e.framework.web.capability.core;

import it.frontend.e2e.framework.core.capability.Capability;

/** Reads the browser-computed accessible name, independently of visible text. */
public interface AccessibleNameReadable extends Capability {
    String readAccessibleName();
}
