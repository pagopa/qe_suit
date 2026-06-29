package it.pagopa.interop.common.contract.model.eservice;

import it.pagopa.interop.common.contract.model.TestModel;
import it.pagopa.interop.generated.openapi.clients.bff.model.ProducerEServiceDescriptor;
import lombok.*;
import lombok.extern.jackson.Jacksonized;

import java.util.List;
import java.util.UUID;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class EService implements TestModel {
    UUID id;
    UUID eserviceId;
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
    ProducerEServiceDescriptor eservice;
    List<EServiceDescriptor> descriptors;
}