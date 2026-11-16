package it.pagopa.interop.common.journey.application;

import it.pagopa.interop.common.eservice.application.command.RequiredAttributeCommand;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.kernel.domain.TenantRef;

import java.util.List;

public interface RequiredAttributeJourney<SELF extends RequiredAttributeJourney<SELF>> extends JourneyModule {
    SELF configureRequiredAttributes(
            EService eService, int version, TenantRef producer, List<RequiredAttributeCommand> requests
    );
}
