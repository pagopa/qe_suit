package it.pagopa.interop.common.contract.model.eservice;

import it.pagopa.interop.common.contract.model.TestModel;
import it.pagopa.interop.common.contract.model.shared.DocumentRef;
import it.pagopa.interop.generated.openapi.clients.bff.model.ProducerEServiceDescriptor;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.List;
import java.util.UUID;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class EService implements TestModel {
    UUID id;
    String name;
    String producerId;
    EServiceTechnology technology;
    EServiceMode mode;
    String description;
    UUID riskAnalysisId;
    Boolean handlePersonalData;
    Boolean isAsync;
    Boolean isSignalHubEnabled;
    Boolean isConsumerDelegable;
    Boolean isClientAccessDelegable;
    ProducerEServiceDescriptor eservice;
    List<EServiceDescriptor> descriptors;

    public EServiceDescriptor findDescriptor(UUID descriptorId) {
        return descriptors.stream()
                .filter(descriptor -> descriptor.getId().equals(descriptorId))
                .findFirst()
                .orElse(null);
    }

    public EService replaceDocument(UUID descriptorId, UUID documentId, DocumentRef updatedDocument) {
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
}