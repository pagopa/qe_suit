package it.pagopa.infrastructure.objectgraph;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

public final class ObjectGraphQuery {

    private static final ObjectGraphQuery ROOT = new ObjectGraphQuery(List.of());
    private final List<QueryStep> steps;

    private ObjectGraphQuery(List<QueryStep> steps) {
        this.steps = List.copyOf(steps);
    }

    public static ObjectGraphQuery root() {
        return ROOT;
    }

    public ObjectGraphQuery property(Method accessor) {
        return append(new PropertyStep(accessor));
    }

    public ObjectGraphQuery index(int index) {
        return append(new IndexStep(index));
    }

    public boolean isRoot() {
        return steps.isEmpty();
    }

    List<QueryStep> steps() {
        return steps;
    }

    private ObjectGraphQuery append(QueryStep step) {
        Objects.requireNonNull(step, "step must not be null");
        List<QueryStep> copy = new java.util.ArrayList<>(steps);
        copy.add(step);
        return new ObjectGraphQuery(copy);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof ObjectGraphQuery query)) return false;
        return steps.equals(query.steps);
    }

    @Override
    public int hashCode() {
        return steps.hashCode();
    }
}
