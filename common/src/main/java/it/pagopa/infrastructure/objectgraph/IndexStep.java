package it.pagopa.infrastructure.objectgraph;

record IndexStep(int index) implements QueryStep {
    IndexStep {
        if (index < 0) {
            throw new ObjectGraphException("index must be >= 0");
        }
    }
}
