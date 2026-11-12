package it.pagopa.interop.common.delegation.infrastructure.cucumber;

import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.delegation.application.command.ProducerDelegationSetupCommand;
import it.pagopa.interop.common.delegation.domain.ProducerDelegationState;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.kernel.domain.Tenant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProducerDelegationSetupMapper {

    private final EntityStore entityStore;

    public ProducerDelegationSetupCommand toCommand(
            String eServiceAlias,
            Tenant delegator,
            Tenant delegate,
            ProducerDelegationState state
    ) {
        EService eService = entityStore.getByAliasOrThrow(eServiceAlias, EService.class);
        return new ProducerDelegationSetupCommand(eService, delegator, delegate, state);
    }
}
