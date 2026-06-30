package it.pagopa.interop.common.contract.journey;

import it.pagopa.interop.common.contract.model.eservice.EServiceDescriptorState;

import java.util.Map;

public interface EServiceJourney<SELF extends EServiceJourney<SELF>> {
    SELF createEService(EServiceDescriptorState state);

    SELF publishEService();
}
