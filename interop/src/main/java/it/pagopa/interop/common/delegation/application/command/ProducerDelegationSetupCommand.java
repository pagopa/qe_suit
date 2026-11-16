package it.pagopa.interop.common.delegation.application.command;

import it.pagopa.interop.common.delegation.domain.ProducerDelegationState;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.kernel.domain.Tenant;

import java.util.Objects;

public final class ProducerDelegationSetupCommand {

    private final EService eService;
    private final Tenant delegator;
    private final Tenant delegate;
    private final ProducerDelegationState state;

    public ProducerDelegationSetupCommand(
            EService eService,
            Tenant delegator,
            Tenant delegate,
            ProducerDelegationState state
    ) {
        this.eService = Objects.requireNonNull(eService, "eService must not be null");
        this.delegator = Objects.requireNonNull(delegator, "delegator must not be null");
        this.delegate = Objects.requireNonNull(delegate, "delegate must not be null");
        this.state = Objects.requireNonNull(state, "state must not be null");
    }

    public EService eService() {
        return eService;
    }

    public Tenant delegator() {
        return delegator;
    }

    public Tenant delegate() {
        return delegate;
    }

    public ProducerDelegationState state() {
        return state;
    }
}
