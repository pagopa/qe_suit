package it.pagopa.interop.domain.services.agreement;

import it.pagopa.interop.domain.model.Agreement;
import it.pagopa.interop.domain.model.Eservice;

import java.util.UUID;

public interface AgreementService {
    Agreement createEserviceAgreement(Eservice eservice);
    Agreement createEserviceAgreement(Eservice eservice, UUID delegationId);
    Agreement getAgreement(UUID agreementId);
    Agreement publishAgreement(Agreement agreement);
}
