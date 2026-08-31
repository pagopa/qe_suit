package it.frontend.e2e.framework.core.binder.context;

import it.frontend.e2e.framework.core.capability.context.CapabilityScope;

import java.lang.reflect.Type;

public class BindContext {

    private final CapabilityScope scope;
    private final Type boundType;

    public BindContext(CapabilityScope scope) {
        this(scope, null);
    }

    public BindContext(CapabilityScope scope, Type boundType) {
        this.scope = scope;
        this.boundType = boundType;
    }

    public static BindContext root() {
        return new BindContext(
                new CapabilityScope("", "", false),
                null
        );
    }

    public CapabilityScope getScope() {
        return scope;
    }

    public Type getBoundType() {
        return boundType;
    }

    @Override
    public String toString() {
        String parent = (scope == null) ? "" : scope.toString().trim();

        String type = boundType == null
                ? ""
                : " | boundType=" + boundType.getTypeName();

        return parent.isEmpty()
                ? type
                : parent + type;
    }
}