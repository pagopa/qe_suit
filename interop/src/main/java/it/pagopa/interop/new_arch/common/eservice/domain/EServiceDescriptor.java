package it.pagopa.interop.new_arch.common.eservice.domain;

import it.pagopa.interop.new_arch.common.agreement.domain.AgreementApprovalPolicy;
import it.pagopa.interop.new_arch.common.attribute.domain.Attributes;
import it.pagopa.interop.new_arch.common.eservice_template.domain.EServiceTemplateRef;
import it.pagopa.interop.new_arch.common.kernel.domain.DelegationRef;
import it.pagopa.interop.new_arch.common.kernel.domain.DocumentRef;
import it.pagopa.interop.new_arch.common.kernel.domain.Identifiable;

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
    DocumentRef interfaceDocument;
    Attributes attributes;
    Integer voucherLifespan;
    Integer dailyCallsPerConsumer;
    Integer dailyCallsTotal;
    AgreementApprovalPolicy agreementApprovalPolicy;
    Instant createdAt;
    Instant publishedAt;
    Instant deprecatedAt;
    Instant archivedAt;
    Instant suspendedAt;
    EServiceTemplateRef templateRef;
    AsyncExchangeProperties asyncExchangeProperties;
    DocumentRef asyncExchangeCallbackInterface;
    DelegationRef delegation;
    ArchivingSchedule archivingSchedule;

    @Singular("serverUrl")
    List<String> serverUrls;

    @Singular("rejectionReason")
    List<DescriptorRejectionReason> rejectionReasons;

    @Singular("audienceItem")
    List<String> audience;

    @Singular("doc")
    List<DocumentRef> docs;

    public DocumentRef findDocument(UUID documentId) {
        if (interfaceDocument != null && interfaceDocument.getId().equals(documentId)) {
            return interfaceDocument;
        }

        if (asyncExchangeCallbackInterface != null && asyncExchangeCallbackInterface.getId().equals(documentId)) {
            return asyncExchangeCallbackInterface;
        }

        for (DocumentRef doc : docs) {
            if (doc.getId().equals(documentId)) {
                return doc;
            }
        }

        return null;
    }

    public EServiceDescriptor replaceDocument(UUID documentId, DocumentRef updatedDocument) {
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
}
