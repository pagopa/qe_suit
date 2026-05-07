package it.pagopa.interop.domain.services.purpose;

import it.pagopa.interop.domain.model.Client;
import it.pagopa.interop.domain.model.Eservice;
import it.pagopa.interop.domain.model.Purpose;
import it.pagopa.interop.domain.model.requests.PurposeCreationRequest;


public interface PurposeService {
    Purpose createEservicePurpose(Eservice eservice, PurposeCreationRequest request);
    Purpose associatePurposeToClient(Purpose purpose, Client client);
}
