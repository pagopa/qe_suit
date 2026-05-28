package it.pagopa.interop.services.agreement;

import it.pagopa.interop.domain.model.Agreement;
import it.pagopa.interop.domain.model.Eservice;

import java.util.UUID;

public interface AgreementService {
    Agreement createAgreement(Eservice eservice);
    Agreement createAgreement(Eservice eservice, UUID delegationId);
    Agreement getAgreement(UUID agreementId);
    Agreement submitAgreement(Agreement agreement);
    Agreement publishAgreement(Agreement agreement);
}
