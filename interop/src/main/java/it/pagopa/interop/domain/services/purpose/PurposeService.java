package it.pagopa.interop.domain.services.purpose;

import it.pagopa.interop.domain.dto.Client;
import it.pagopa.interop.domain.dto.Eservice;
import it.pagopa.interop.domain.dto.Purpose;
import it.pagopa.interop.domain.dto.requests.PurposeCreationRequest;


public interface PurposeService {
    Purpose createEservicePurpose(Eservice eservice, PurposeCreationRequest request);
    Purpose associatePurposeToClient(Purpose purpose, Client client);
}
