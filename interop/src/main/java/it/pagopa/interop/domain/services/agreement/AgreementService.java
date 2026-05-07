package it.pagopa.interop.domain.services.agreement;

import it.pagopa.interop.domain.dto.Agreement;
import it.pagopa.interop.domain.dto.Eservice;
import it.pagopa.interop.domain.dto.requests.AgreementCreationRequest;

public interface AgreementService {
    Agreement createEserviceAgreement(Eservice eservice, AgreementCreationRequest request);
}
