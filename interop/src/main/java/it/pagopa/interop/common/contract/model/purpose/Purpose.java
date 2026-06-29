package it.pagopa.interop.common.contract.model.purpose;

import it.pagopa.interop.common.contract.model.TestModel;
import it.pagopa.interop.common.contract.model.agreement.Agreement;
import it.pagopa.interop.common.contract.model.eservice.EService;
import it.pagopa.interop.common.contract.model.risk_analysis.RiskAnalysis;
import it.pagopa.interop.common.contract.model.tenant.Tenant;
import lombok.*;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Purpose implements TestModel {
    private UUID id;
    private String title;
    private String description;
    private Tenant consumer;
    private RiskAnalysis riskAnalysis;
    private EService eservice;
    private Agreement agreement;
    private List<PurposeVersion> versions;
    private Integer dailyCallsPerConsumer;
    private Integer dailyCallsTotal;
    private Tenant delegate;
    private Tenant delegator;
    private UUID delegateId;
    private PurposeTemplate template;
    private Boolean isDocumentReady;


    public PurposeVersion getCurrentVersion() {
        if (versions == null) {
            return null;
        }
        return versions.stream()
                .filter(Objects::nonNull)
                .filter(v -> v.getPurposeVersionState() == PurposeVersionState.ACTIVE)
                .findFirst()
                .orElseGet(() -> versions.stream().filter(Objects::nonNull).findFirst().orElse(null));
    }

    public PurposeVersion getWaitingForAppovalVersion(){
        if (versions == null) {
            return null;
        }
        return versions.stream()
                .filter(Objects::nonNull)
                .filter(v -> v.getPurposeVersionState() == PurposeVersionState.WAITING_FOR_APPROVAL)
                .findFirst()
                .orElse(null);
    }

    public PurposeVersion getRejectedVersion(){
        if (versions == null) {
            return null;
        }
        return versions.stream()
                .filter(Objects::nonNull)
                .filter(v -> v.getPurposeVersionState() == PurposeVersionState.REJECTED)
                .findFirst()
                .orElse(null);
    }
}
