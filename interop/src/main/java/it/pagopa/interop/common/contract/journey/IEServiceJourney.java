package it.pagopa.interop.common.contract.journey;

import it.pagopa.interop.common.contract.model.eservice.EServiceDescriptorState;

public interface IEServiceJourney<SELF extends IEServiceJourney<SELF>> {
    SELF createEService(EServiceDescriptorState state);

    SELF publishEService();
}
