package it.pagopa.interop.new_arch.common.eservice_template.domain;

import it.pagopa.interop.common.contract.model.TestModel;
import it.pagopa.interop.common.contract.model.agreement.AgreementApprovalPolicy;
import it.pagopa.interop.common.contract.model.attribute.Attributes;
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
public class EServiceTemplateVersion implements TestModel {
    UUID id;
    String version;
    EServiceTemplateVersionState state;
    Integer voucherLifespan;
    Integer dailyCallsPerConsumer;
    Integer dailyCallsTotal;
    AgreementApprovalPolicy agreementApprovalPolicy;
    Attributes attributes;
    DocumentRef interfaceDocument;
    Instant createdAt;
    Instant publishedAt;
    Instant suspendedAt;
    Instant archivedAt;

    @Singular("doc")
    List<DocumentRef> docs;

    @Singular("audienceItem")
    List<String> audience;

    @Singular("serverUrl")
    List<String> serverUrls;
}

