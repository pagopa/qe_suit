package it.pagopa.interop.common.infrastructure.contract.browser;

import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import org.springframework.beans.factory.ObjectProvider;

import java.util.Objects;

public final class WebContractValidator {
    private final ObjectProvider<WebPresentationGateway> webPresentationGatewayProvider;
    private final CurrentUserSession currentUserSession;

    public WebContractValidator(
            ObjectProvider<WebPresentationGateway> webPresentationGatewayProvider,
            CurrentUserSession currentUserSession
    ) {
        this.webPresentationGatewayProvider = Objects.requireNonNull(webPresentationGatewayProvider, "webPresentationGatewayProvider must not be null");
        this.currentUserSession = Objects.requireNonNull(currentUserSession, "currentUserSession must not be null");
    }

    public WebContractStages.UserStage as(User user, Tenant tenant) {
        Objects.requireNonNull(user, "user must not be null");
        Objects.requireNonNull(tenant, "tenant must not be null");
        return new WebContractInvocationBuilder(webPresentationGatewayProvider, currentUserSession, user, tenant);
    }
}
