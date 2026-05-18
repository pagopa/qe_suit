package it.pagopa.interop.domain.services.eservice;

import it.pagopa.interop.domain.model.Eservice;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceSeed;

import java.util.UUID;

public interface EserviceService {
    Eservice createEservice(EServiceSeed request);
    Eservice createEservice();
    Eservice createEservice(java.util.function.Consumer<EServiceSeed> overrides);
    Eservice publishEservice(Eservice eservice);
    Eservice getEservice(UUID eserviceId, UUID descriptorId);
}
