package it.pagopa.interop.domain.services;

import it.pagopa.interop.domain.services.agreement.AgreementService;
import it.pagopa.interop.domain.services.client.ClientService;
import it.pagopa.interop.domain.services.eservice.EserviceService;
import it.pagopa.interop.domain.services.purpose.PurposeService;

public interface InteropService extends ClientService, PurposeService, EserviceService, AgreementService {
}
