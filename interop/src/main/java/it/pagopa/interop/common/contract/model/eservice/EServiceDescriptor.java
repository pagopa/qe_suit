package it.pagopa.interop.common.contract.model.eservice;

import it.pagopa.interop.common.contract.model.agreement.AgreementApprovalPolicy;
import it.pagopa.interop.common.contract.model.TestModel;
import it.pagopa.interop.common.contract.model.attribute.Attributes;
import it.pagopa.interop.common.contract.model.eservice_template.EServiceTemplateRef;
import it.pagopa.interop.common.contract.model.shared.DelegationRef;
import it.pagopa.interop.common.contract.model.shared.DocumentRef;
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
public class EServiceDescriptor implements TestModel {
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
}
