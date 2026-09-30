package it.pagopa.interop.common.eservice_template.domain;

import it.pagopa.interop.common.agreement.domain.AgreementApprovalPolicy;
import it.pagopa.interop.common.attribute.domain.Attributes;
import it.pagopa.interop.common.kernel.domain.DocumentRef;
import it.pagopa.domain.Identifiable;
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
public class EServiceTemplateVersion implements Identifiable {
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

