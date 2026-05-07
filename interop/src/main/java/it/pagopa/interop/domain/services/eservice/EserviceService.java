package it.pagopa.interop.domain.services.eservice;

import it.pagopa.interop.domain.dto.Eservice;
import it.pagopa.interop.domain.dto.requests.EserviceCreationRequest;

public interface EserviceService {
    Eservice createEservice(EserviceCreationRequest request);
}
