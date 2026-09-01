package it.pagopa.infrastructure.objectgraph;

import java.util.Objects;

final class DefaultObjectGraphDecomposer implements ObjectGraphDecomposer {

    private final ObjectDecomposer decomposer;

    DefaultObjectGraphDecomposer(ObjectDecomposer decomposer) {
        this.decomposer = Objects.requireNonNull(decomposer, "decomposer must not be null");
    }

    @Override
    public ObjectGraph decompose(Object source) {
        if (source == null) {
            throw new ObjectGraphException("source must not be null");
        }
        return decomposer.decompose(source);
    }
}
