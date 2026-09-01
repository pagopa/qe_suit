package it.pagopa.infrastructure.contract.browser;

import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import org.junit.jupiter.api.DynamicTest;
import org.springframework.beans.factory.ObjectProvider;

import java.util.Objects;
import java.util.stream.Stream;

import static org.junit.jupiter.api.DynamicTest.dynamicTest;

final class WebContractInvocationBuilder implements WebContractStages.UserStage {
    private final ObjectProvider<WebPresentationGateway> webPresentationGatewayProvider;
    private final CurrentUserSession currentUserSession;
    private final User user;
    private final Tenant tenant;

    WebContractInvocationBuilder(
            ObjectProvider<WebPresentationGateway> webPresentationGatewayProvider,
            CurrentUserSession currentUserSession,
            User user,
            Tenant tenant
    ) {
        this.webPresentationGatewayProvider = Objects.requireNonNull(webPresentationGatewayProvider, "webPresentationGatewayProvider must not be null");
        this.currentUserSession = Objects.requireNonNull(currentUserSession, "currentUserSession must not be null");
        this.user = Objects.requireNonNull(user, "user must not be null");
        this.tenant = Objects.requireNonNull(tenant, "tenant must not be null");
    }

    @Override
    public <P extends Page> WebContractStages.PageStage<P> on(Class<P> pageType) {
        Objects.requireNonNull(pageType, "pageType must not be null");
        return new PageStageImpl<>(pageType);
    }

    private final class PageStageImpl<P extends Page> implements WebContractStages.PageStage<P> {
        private final Class<P> pageType;
        private final WebContractRuntimeCaseExecutor runtimeCaseExecutor;

        private PageStageImpl(Class<P> pageType) {
            this.pageType = pageType;
            this.runtimeCaseExecutor = new WebContractRuntimeCaseExecutor(webPresentationGatewayProvider, currentUserSession);
        }

        @Override
        public Stream<DynamicTest> tests(Stream<? extends WebScenario<P>> scenarios) {
            Objects.requireNonNull(scenarios, "scenarios must not be null");
            return scenarios.map(scenario -> dynamicTest(
                    scenario.name(),
                    () -> runtimeCaseExecutor.execute(user, tenant, pageType, scenario)
            ));
        }
    }
}
