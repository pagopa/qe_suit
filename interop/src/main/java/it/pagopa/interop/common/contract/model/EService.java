package it.pagopa.interop.common.contract.model;

import it.pagopa.interop.common.contract.enums.EServiceDescriptorState;
import it.pagopa.interop.common.contract.enums.EServiceMode;
import it.pagopa.interop.common.contract.enums.EServiceTechnology;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EService implements TestModel {

    private UUID id;
    private String name;
    private String description;
    private EServiceTechnology technology;
    private EServiceMode mode;
    private List<EServiceRiskAnalysis> riskAnalyses;
    private Boolean isSignalHubEnabled;
    private Boolean isConsumerDelegable;
    private Boolean isClientAccessDelegable;
    private Boolean personalData;
    private Boolean isAsync;

    @Builder.Default
    private List<EServiceDescriptor> descriptors = new ArrayList<>();

    /**
     * Recupera il descrittore attivo più recente basandosi sul timestamp.
     */
    public EServiceDescriptor getLatestActiveDescriptor() {
        return descriptors.stream()
                .filter(d -> d.getState().equals(EServiceDescriptorState.PUBLISHED))
                .max(Comparator.comparing(EServiceDescriptor::getPublishedAt))
                .orElse(null);
    }
}