package it.pagopa.interop.common.contract.model.eservice;

import it.pagopa.interop.common.contract.enums.EServiceMode;
import it.pagopa.interop.common.contract.enums.EServiceTechnology;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.List;
import java.util.UUID;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class EService {
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

    @Singular("descriptor")
    List<EServiceDescriptor> descriptors;
}