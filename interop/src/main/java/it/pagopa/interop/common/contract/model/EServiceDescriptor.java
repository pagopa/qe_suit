package it.pagopa.interop.common.contract.model;

import it.pagopa.interop.common.contract.enums.AgreementApprovalPolicy;
import it.pagopa.interop.common.contract.enums.EServiceDescriptorState;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EServiceDescriptor {

    // Identificativi
    private UUID descriptorId;
    private UUID eServiceId;
    private String version;

    // Stato
    private EServiceDescriptorState state;

    // Configurazione e Quote
    private Integer voucherLifespan;
    private Integer dailyCallsPerConsumer;
    private Integer dailyCallsTotal;
    private AgreementApprovalPolicy agreementApprovalPolicy;

    // Attributi necessari per la fruizione
    @Builder.Default
    private List<CertifiedAttribute> certifiedAttributes = new ArrayList<>();
    @Builder.Default
    private List<VerifiedAttribute> verifiedAttributes = new ArrayList<>();
    @Builder.Default
    private List<CertifiedDiscreteAttribute> ertifiedDiscreteAttributes = new ArrayList<>();
    @Builder.Default
    private List<DeclaredAttribute> declaredAttributes = new ArrayList<>();

    // Timestamp
    private OffsetDateTime publishedAt;
    private OffsetDateTime deprecatedAt;
    private OffsetDateTime archivedAt;
    private OffsetDateTime trendSuspendedAt;

}
