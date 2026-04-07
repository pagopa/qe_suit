package it.frontend.e2e.framework.core.binder.context;

import it.frontend.e2e.framework.core.capability.context.CapabilityScope;

public class BindContext {
    private final CapabilityScope scope;

    public BindContext(CapabilityScope scope) {
        this.scope = scope;
    }

    public static BindContext root() {
        return new BindContext(new CapabilityScope("", ""));
    }

    public CapabilityScope getScope() {
        return scope;
    }

    @Override
    public String toString() {
        String parent = (scope == null) ? "" : scope.toString().trim();
        return parent.isEmpty() ? "" : parent;
    }
}
