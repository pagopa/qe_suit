package it.pagopa.interop.services;

import it.pagopa.interop.services.agreement.AgreementService;
import it.pagopa.interop.services.client.ClientService;
import it.pagopa.interop.services.eservice.EserviceService;
import it.pagopa.interop.services.purpose.PurposeService;

public interface InteropService extends ClientService, PurposeService, EserviceService, AgreementService {
}
