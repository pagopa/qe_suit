package it.pagopa.interop.common.domain.model;

import java.util.Objects;

public abstract class AbstractModel {

    public abstract String getUniqueIdentifier();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractModel other)) return false;
        return Objects.equals(getUniqueIdentifier(), other.getUniqueIdentifier());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUniqueIdentifier());
    }
}