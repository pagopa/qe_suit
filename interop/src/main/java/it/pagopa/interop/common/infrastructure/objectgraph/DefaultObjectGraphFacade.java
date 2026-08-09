package it.pagopa.interop.common.infrastructure.objectgraph;

import java.util.Objects;

final class DefaultObjectGraphFacade implements ObjectGraphFacade {

    private final ObjectDecomposer decomposer;

    DefaultObjectGraphFacade(ObjectDecomposer decomposer) {
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
