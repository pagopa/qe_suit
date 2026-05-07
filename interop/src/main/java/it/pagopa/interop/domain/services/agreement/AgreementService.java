package it.pagopa.interop.domain.services.agreement;

import it.pagopa.interop.domain.model.Agreement;
import it.pagopa.interop.domain.model.Eservice;

public interface AgreementService {
    Agreement createEserviceAgreement(Eservice eservice, Object request);
}
