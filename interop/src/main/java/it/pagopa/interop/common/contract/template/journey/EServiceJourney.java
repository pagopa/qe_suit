package it.pagopa.interop.common.contract.template.journey;

public interface EServiceJourney<SELF extends EServiceJourney<SELF>> {
    SELF createDraftEService();
    SELF publishEservice();
}
