package it.pagopa.interop.common.infrastructure.contract;

import it.pagopa.infrastructure.channel.CurrentChannel;
import it.pagopa.infrastructure.contract.http.HttpContractAuthentication;
import it.pagopa.infrastructure.contract.http.HttpContractStages;
import it.pagopa.infrastructure.contract.http.HttpContractValidator;
import it.pagopa.interop.common.kernel.context.AuthSession;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.InteropActor;
import it.pagopa.interop.common.kernel.domain.InteropRole;
import it.pagopa.interop.common.kernel.domain.Tenant;
import org.springframework.beans.factory.ObjectProvider;

import java.util.Objects;

public class InteropHttpContractValidator {
    private final HttpContractValidator delegate;
    private final AuthSession authSession;
    private final Channel expectedChannel;
    private final ObjectProvider<CurrentChannel<Channel>> currentChannel;

    public InteropHttpContractValidator(
            HttpContractValidator delegate,
            AuthSession authSession,
            Channel expectedChannel,
            ObjectProvider<CurrentChannel<Channel>> currentChannel
    ) {
        this.delegate = Objects.requireNonNull(delegate, "delegate must not be null");
        this.authSession = Objects.requireNonNull(authSession, "authSession must not be null");
        this.expectedChannel = Objects.requireNonNull(expectedChannel, "expectedChannel must not be null");
        this.currentChannel = Objects.requireNonNull(currentChannel, "currentChannel must not be null");
    }

    public HttpContractStages.ApiCallSelectionStage as(Tenant tenant, InteropActor actor) {
        Objects.requireNonNull(tenant, "tenant must not be null");
        Objects.requireNonNull(actor, "actor must not be null");
        return authenticated(() -> authSession.set(actor, tenant));
    }

    public HttpContractStages.ApiCallSelectionStage as(Tenant tenant, InteropRole role) {
        Objects.requireNonNull(tenant, "tenant must not be null");
        Objects.requireNonNull(role, "role must not be null");
        return authenticated(() -> authSession.set(role, tenant));
    }

    private HttpContractStages.ApiCallSelectionStage authenticated(HttpContractAuthentication authentication) {
        return operationSupplier -> delegate.apiCall(() -> {
            Channel activeChannel = currentChannel.getObject().getCurrentChannel();
            if (activeChannel != expectedChannel) {
                throw new IllegalStateException(
                        "Expected channel " + expectedChannel + ", found " + activeChannel
                );
            }
            authentication.authenticate();
        }, operationSupplier);
    }
}
