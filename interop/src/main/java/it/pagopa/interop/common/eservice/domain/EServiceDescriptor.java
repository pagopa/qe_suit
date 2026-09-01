package it.pagopa.interop.common.eservice.domain;

import it.pagopa.interop.common.agreement.domain.AgreementApprovalPolicy;
import it.pagopa.interop.common.attribute.domain.Attributes;
import it.pagopa.kernel.domain.Identifiable;
import it.pagopa.interop.common.kernel.domain.Delegation;
import it.pagopa.interop.common.kernel.domain.Document;
import it.pagopa.interop.common.kernel.domain.EServiceDescriptorRef;
import it.pagopa.interop.common.kernel.domain.EServiceTemplateRef;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class EServiceDescriptor implements Identifiable {
    UUID id;
    String version;
    EServiceDescriptorState state;
    String description;
    Document interfaceDocument;
    Attributes attributes;
    Integer voucherLifespan;
    Integer dailyCallsPerConsumer;
    Integer dailyCallsTotal;
    AgreementApprovalPolicy agreementApprovalPolicy;
    Instant publishedAt;
    Instant deprecatedAt;
    Instant archivedAt;
    Instant suspendedAt;
    EServiceTemplateRef templateRef;
    AsyncExchangeProperties asyncExchangeProperties;
    Document asyncExchangeCallbackInterface;
    Delegation delegation;
    ArchivingSchedule archivingSchedule;

    @Singular("serverUrl")
    List<String> serverUrls;

    @Singular("rejectionReason")
    List<DescriptorRejectionReason> rejectionReasons;

    @Singular("audienceItem")
    List<String> audience;

    @Singular("doc")
    List<Document> docs;

    public Document findDocument(UUID documentId) {
        if (interfaceDocument != null && interfaceDocument.getId().equals(documentId)) {
            return interfaceDocument;
        }

        if (asyncExchangeCallbackInterface != null && asyncExchangeCallbackInterface.getId().equals(documentId)) {
            return asyncExchangeCallbackInterface;
        }

        for (Document doc : docs) {
            if (doc.getId().equals(documentId)) {
                return doc;
            }
        }

        return null;
    }

    public EServiceDescriptor replaceDocument(UUID documentId, Document updatedDocument) {
        if (interfaceDocument != null && interfaceDocument.getId().equals(documentId)) {
            return this.toBuilder()
                    .interfaceDocument(updatedDocument)
                    .build();
        }

        if (asyncExchangeCallbackInterface != null && asyncExchangeCallbackInterface.getId().equals(documentId)) {
            return this.toBuilder()
                    .asyncExchangeCallbackInterface(updatedDocument)
                    .build();
        }

        return this.toBuilder()
                .docs(
                        docs.stream()
                                .map(doc ->
                                        doc.getId().equals(documentId)
                                                ? updatedDocument
                                                : doc
                                )
                                .toList()
                )
                .build();
    }

    public EServiceDescriptorRef getRef() {
        return EServiceDescriptorRef.of(this.id);
    }
}
