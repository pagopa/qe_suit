package it.pagopa.interop.web.controller;

import it.pagopa.interop.common.contract.controller.EserviceCommonController;
import it.pagopa.interop.common.contract.service.EServiceService;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceSeed;

public class EServiceController extends EserviceCommonController<EServiceSeed> {

    public EServiceController(EServiceService<EServiceSeed> service) {
        super(service);
    }


}
