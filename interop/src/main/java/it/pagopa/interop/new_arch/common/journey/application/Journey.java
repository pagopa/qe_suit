package it.pagopa.interop.new_arch.common.journey.application;

public interface Journey<SELF extends Journey<SELF>> extends
        AgreementJourney<SELF>, UserJourney<SELF> {
}
