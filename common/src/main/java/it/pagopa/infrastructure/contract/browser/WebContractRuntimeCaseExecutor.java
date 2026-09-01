package it.pagopa.infrastructure.contract.browser;

import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import org.springframework.beans.factory.ObjectProvider;

import java.util.Objects;

final class WebContractRuntimeCaseExecutor {
    private final ObjectProvider<WebPresentationGateway> webPresentationGatewayProvider;
    private final CurrentUserSession currentUserSession;

    WebContractRuntimeCaseExecutor(
            ObjectProvider<WebPresentationGateway> webPresentationGatewayProvider,
            CurrentUserSession currentUserSession
    ) {
        this.webPresentationGatewayProvider = Objects.requireNonNull(webPresentationGatewayProvider, "webPresentationGatewayProvider must not be null");
        this.currentUserSession = Objects.requireNonNull(currentUserSession, "currentUserSession must not be null");
    }

    <P extends Page> void execute(
            User user,
            Tenant tenant,
            Class<P> pageType,
            WebScenario<P> scenario
    ) {
        Objects.requireNonNull(user, "user must not be null");
        Objects.requireNonNull(tenant, "tenant must not be null");
        Objects.requireNonNull(pageType, "pageType must not be null");
        Objects.requireNonNull(scenario, "scenario must not be null");

        currentUserSession.set(user, tenant);

        WebPresentationGateway gateway = webPresentationGatewayProvider.getObject();
        try {
            P page = gateway.bind(pageType);

            page.navigateTo();
            page.assertLoaded();

            scenario.action().accept(page);
            scenario.assertion().accept(page);
        } finally {
            gateway.close();
        }
    }
}
