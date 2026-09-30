package it.pagopa.interop.common.eservice.domain;

import it.pagopa.domain.Identifiable;
import it.pagopa.interop.common.kernel.domain.Document;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.*;
import java.util.stream.Stream;

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
        return getUniqueDescriptorByState(EServiceDescriptorState.DRAFT);
    }

    public EServiceDescriptor getLastDeprecatedDescriptor() {
        return getUniqueDescriptorByState(EServiceDescriptorState.DEPRECATED);
    }

    public EServiceDescriptor getLastArchivedDescriptor() {
        return getUniqueDescriptorByState(EServiceDescriptorState.ARCHIVED);
    }

    public EServiceDescriptor getActiveDescriptor() {
        return descriptorsByState(EServiceDescriptorState.PUBLISHED)
                .max(Comparator.comparing(
                        EServiceDescriptor::getPublishedAt,
                        Comparator.nullsLast(Comparator.naturalOrder())
                ))
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Nessun descriptor PUBLISHED trovato per EService " + id
                        )
                );
    }

    public void addDescriptor(EServiceDescriptor descriptor) {
        Objects.requireNonNull(descriptor, "descriptor non può essere null");
        Objects.requireNonNull(descriptor.getId(), "descriptor.id non può essere null");

        for (int i = 0; i < descriptors.size(); i++) {
            EServiceDescriptor current = descriptors.get(i);
            if (current.getId().equals(descriptor.getId())) {
                descriptors.set(i, descriptor);
                return;
            }
        }

        descriptors.add(descriptor);
    }

    public EServiceRef getRef() {
        return EServiceRef.of(this.id);
    }

    private Stream<EServiceDescriptor> descriptorsByState(EServiceDescriptorState state) {
        return descriptors.stream()
                .filter(descriptor -> descriptor.getState() == state);
    }

    private EServiceDescriptor getUniqueDescriptorByState(EServiceDescriptorState state) {
        List<EServiceDescriptor> matchingDescriptors = descriptorsByState(state).toList();

        if (matchingDescriptors.isEmpty()) {
            throw new NoSuchElementException(
                    "Nessun descriptor " + state + " trovato per EService " + id
            );
        }

        if (matchingDescriptors.size() > 1) {
            throw new IllegalStateException(
                    "Trovati " + matchingDescriptors.size()
                            + " descriptor " + state
                            + " per EService " + id
                            + ", atteso esattamente 1"
            );
        }

        return matchingDescriptors.get(0);
    }
}