package it.pagopa.infrastructure.pst.model;

import java.util.List;
import java.util.Objects;

public record PstDocument(List<PstOperation> operations) {
    public PstDocument {
        operations = List.copyOf(Objects.requireNonNull(operations, "operations must not be null"));
    }
}
