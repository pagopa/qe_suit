package it.pagopa.interop.service;

import it.pagopa.interop.service.agreement.AgreementService;
import it.pagopa.interop.service.client.ClientService;
import it.pagopa.interop.service.eservice.EserviceService;
import it.pagopa.interop.service.purpose.PurposeService;

public interface InteropService extends ClientService, PurposeService, EserviceService, AgreementService {
}
