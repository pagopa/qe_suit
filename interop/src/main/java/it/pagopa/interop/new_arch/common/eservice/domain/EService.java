package it.pagopa.interop.new_arch.common.eservice.domain;

import it.pagopa.interop.new_arch.common.kernel.domain.Document;
import it.pagopa.interop.new_arch.common.kernel.domain.EServiceRef;
import it.pagopa.interop.new_arch.common.kernel.Identifiable;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class EService implements Identifiable {
    UUID id;
    String name;
    String producerId;
    EServiceTechnology technology;
    EServiceMode mode;
    String description;
    Boolean personalData;
    Boolean asyncExchange;
    Boolean isSignalHubEnabled;
    Boolean isConsumerDelegable;
    Boolean isClientAccessDelegable;
    List<EServiceDescriptor> descriptors;

    public EServiceDescriptor findDescriptor(UUID descriptorId) {
        return descriptors.stream()
                .filter(descriptor -> descriptor.getId().equals(descriptorId))
                .findFirst()
                .orElse(null);
    }

    public EService replaceDocument(UUID descriptorId, UUID documentId, Document updatedDocument) {
        return this.toBuilder()
                .descriptors(
                        descriptors.stream()
                                .map(descriptor ->
                                        descriptor.getId().equals(descriptorId)
                                                ? descriptor.replaceDocument(documentId, updatedDocument)
                                                : descriptor
                                )
                                .toList()
                )
                .build();
    }

    public EServiceDescriptor getLastDraftDescriptor() {
        return descriptors.stream().filter(descriptor -> descriptor.getState() == EServiceDescriptorState.DRAFT)
                .max(Comparator.comparing(EServiceDescriptor::getCreatedAt))
                .orElse(null);
    }

    public EServiceDescriptor getActiveDescriptor() {
        return descriptors.stream().filter(descriptor -> descriptor.getState() == EServiceDescriptorState.PUBLISHED)
                .max(Comparator.comparing(EServiceDescriptor::getPublishedAt))
                .orElse(null);
    }

    public void addDescriptor(EServiceDescriptor descriptor) {
        this.descriptors.add(descriptor);
    }

    public EServiceRef getRef() {
        return EServiceRef.of(this.id);
    }
}