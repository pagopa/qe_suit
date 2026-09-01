package it.pagopa.infrastructure.objectgraph;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Objects;

record PropertyStep(Method accessor) implements QueryStep {
    PropertyStep {
        Objects.requireNonNull(accessor, "accessor must not be null");
        if (accessor.getParameterCount() != 0) {
            throw new ObjectGraphException("Property accessor must have zero parameters: " + accessor);
        }
        if (Modifier.isStatic(accessor.getModifiers())) {
            throw new ObjectGraphException("Property accessor must not be static: " + accessor);
        }
        if (accessor.getReturnType() == Void.TYPE) {
            throw new ObjectGraphException("Property accessor must return a value: " + accessor);
        }
    }
}
