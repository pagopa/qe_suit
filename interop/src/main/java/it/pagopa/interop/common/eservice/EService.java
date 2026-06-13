package it.pagopa.interop.common.eservice;

import it.pagopa.interop.common.template.TestModel;
import it.pagopa.interop.generated.openapi.clients.bff.model.CompactDescriptor;
import it.pagopa.interop.generated.openapi.clients.bff.model.ProducerEServiceDescriptor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RequiredArgsConstructor
public class EService implements TestModel {
    @Delegate
    private final ProducerEServiceDescriptor embeddedModel;

    public UUID getEserviceId() {
        return this.getEservice().getId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EService other) || !super.equals(o)) return false;

        return Objects.equals(descriptorIds(this), descriptorIds(other));
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), descriptorIds(this));
    }

    public UUID getLastDescriptorId() {
        List<UUID> descriptorIds = this.getEservice().getDescriptors()
                .stream()
                .map(CompactDescriptor::getId)
                .toList();
        return descriptorIds.isEmpty() ? null : descriptorIds.get(descriptorIds.size() - 1);
    }

    public UUID getLastDraftDescriptorId() {
        return this.getEservice().getDraftDescriptor().getId();
    }

    private static List<String> descriptorIds(EService e) {
        e.getEservice();
        return e.getEservice().getDescriptors()
                .stream()
                .map(d -> d.getId().toString())
                .sorted(java.util.Comparator.nullsFirst(String::compareTo))
                .toList();
    }
}
