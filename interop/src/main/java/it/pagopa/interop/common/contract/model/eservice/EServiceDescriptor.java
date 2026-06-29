package it.pagopa.interop.common.contract.model.eservice;

import it.pagopa.interop.common.contract.enums.AgreementApprovalPolicy;
import it.pagopa.interop.common.contract.enums.EServiceDescriptorState;
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
public class EServiceDescriptor {
    UUID id;
    String version;
    EServiceDescriptorState state;
    String description; // nullable

    DescriptorDocumentRef interfaceDocument; // nullable

    @Singular("doc")
    List<DescriptorDocumentRef> docs; // vuota se assente

    DescriptorAttributes attributes; // nullable

    @Singular("audienceItem")
    List<String> audience; // vuota se assente

    Integer voucherLifespan;       // nullable
    Integer dailyCallsPerConsumer; // nullable
    Integer dailyCallsTotal;       // nullable

    AgreementApprovalPolicy agreementApprovalPolicy; // nullable

    @Singular("serverUrl")
    List<String> serverUrls; // vuota se assente

    Instant createdAt;    // nullable
    Instant publishedAt;  // nullable
    Instant deprecatedAt; // nullable
    Instant archivedAt;   // nullable
    Instant suspendedAt;  // nullable

    @Singular("rejectionReason")
    List<DescriptorRejectionReason> rejectionReasons; // vuota se assente

    EServiceTemplateRef templateRef; // nullable
    AsyncExchangeProperties asyncExchangeProperties; // nullable
    DescriptorDocumentRef asyncExchangeCallbackInterface; // nullable
    DelegationRef delegation; // nullable
    ArchivingSchedule archivingSchedule; // nullable
}
