package it.pagopa.interop.domain.model;

import it.pagopa.interop.generated.openapi.clients.bff.model.CatalogEServiceDescriptor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class Eservice extends AbstractModel {
    @Delegate
    private final CatalogEServiceDescriptor embeddedModel;

    @Override
    public String getUniqueIdentifier() {
        return this.getId().toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Eservice other) || !super.equals(o)) return false;

        return Objects.equals(descriptorIds(this), descriptorIds(other));
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), descriptorIds(this));
    }

    private static List<String> descriptorIds(Eservice e) {
        e.getEservice();
        return e.getEservice().getDescriptors()
                .stream()
                .map(d -> d.getId().toString())
                .sorted(java.util.Comparator.nullsFirst(String::compareTo))
                .toList();
    }
}
