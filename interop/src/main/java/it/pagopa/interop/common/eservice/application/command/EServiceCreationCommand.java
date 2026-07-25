package it.pagopa.interop.new_arch.common.eservice.application.command;

import it.pagopa.interop.new_arch.common.eservice.domain.EServiceMode;
import it.pagopa.interop.new_arch.common.eservice.domain.EServiceTechnology;

public interface EServiceCreationCommand {
    EServiceCreationCommand name(String name);
    EServiceCreationCommand mode(EServiceMode mode);
    EServiceCreationCommand isAsync(Boolean isAsync);
    EServiceCreationCommand technology(EServiceTechnology technology);
    EServiceCreationCommand description(String description);
    EServiceCreationCommand handlePersonalData(Boolean handlePersonalData);
    EServiceCreationCommand isSignalHubEnabled(Boolean isSignalHubEnabled);
    EServiceCreationCommand isConsumerDelegable(Boolean isConsumerDelegable);
    EServiceCreationCommand isClientAccessDelegable(Boolean isClientAccessDelegable);
}
