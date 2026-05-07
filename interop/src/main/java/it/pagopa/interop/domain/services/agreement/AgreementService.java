package it.pagopa.interop.domain.services.agreement;

import it.pagopa.interop.domain.model.Agreement;
import it.pagopa.interop.domain.model.Eservice;
import it.pagopa.interop.domain.model.requests.AgreementCreationRequest;

public interface AgreementService {
    Agreement createEserviceAgreement(Eservice eservice, AgreementCreationRequest request);
}
