package it.pagopa.interop.common.eservice.application.command;

import it.pagopa.interop.common.eservice.domain.EServiceMode;
import it.pagopa.interop.common.eservice.domain.EServiceTechnology;
import jakarta.annotation.Nullable;

public interface EServiceUpdateCommand {
    EServiceUpdateCommand name(String name);
    EServiceUpdateCommand description(String description);
    EServiceUpdateCommand technology(EServiceTechnology technology);
    EServiceUpdateCommand mode(EServiceMode mode);
    EServiceUpdateCommand isSignalHubEnabled(Boolean isSignalHubEnabled);
    EServiceUpdateCommand isConsumerDelegable(Boolean isConsumerDelegable);
    EServiceUpdateCommand isClientAccessDelegable(Boolean isClientAccessDelegable);
    EServiceUpdateCommand handlePersonalData(Boolean handlePersonalData);
    EServiceUpdateCommand asyncExchange(Boolean isAsync);

    EServiceUpdateCommand alias(@Nullable String alias);

    String getAlias();
}
