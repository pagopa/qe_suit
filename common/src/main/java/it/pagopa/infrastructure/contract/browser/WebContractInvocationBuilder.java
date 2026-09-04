package it.pagopa.infrastructure.contract.browser;

import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.frontend.e2e.framework.web.domain.Page;
import org.junit.jupiter.api.DynamicTest;

import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.junit.jupiter.api.DynamicTest.dynamicTest;

final class WebContractInvocationBuilder implements WebContractStages.UserStage {

    private final Supplier<WebPresentationGateway> webPresentationGatewayProvider;
    private final WebSessionProvider contextConfigurer;

    WebContractInvocationBuilder(
            Supplier<WebPresentationGateway> webPresentationGatewayProvider,
            WebSessionProvider contextConfigurer
    ) {
        this.webPresentationGatewayProvider = Objects.requireNonNull(
                webPresentationGatewayProvider,
                "webPresentationGatewayProvider must not be null"
        );
        this.contextConfigurer = Objects.requireNonNull(
                contextConfigurer,
                "contextConfigurer must not be null"
        );
    }

    @Override
    public <P extends Page> WebContractStages.PageStage<P> on(Class<P> pageType) {
        return on(pageType, new String[0]);
    }

    @Override
    public <P extends Page> WebContractStages.PageStage<P> on(Class<P> pageType, String... pathParams) {
        Objects.requireNonNull(pageType, "pageType must not be null");
        Objects.requireNonNull(pathParams, "pathParams must not be null");
        return new PageStageImpl<>(pageType, pathParams);
    }

    private final class PageStageImpl<P extends Page>
            implements WebContractStages.PageStage<P> {

        private final Class<P> pageType;
        private final String[] pathParams;
        private final WebContractRuntimeCaseExecutor runtimeCaseExecutor;

        private PageStageImpl(Class<P> pageType, String[] pathParams) {
            this.pageType = pageType;
            this.pathParams = pathParams;
            this.runtimeCaseExecutor = new WebContractRuntimeCaseExecutor(
                    webPresentationGatewayProvider,
                    contextConfigurer
            );
        }

        @Override
        public Stream<DynamicTest> tests(
                Stream<? extends WebScenario<P>> scenarios
        ) {
            Objects.requireNonNull(scenarios, "scenarios must not be null");

            return scenarios.map(scenario -> dynamicTest(
                    scenario.name(),
                    () -> runtimeCaseExecutor.execute(pageType, pathParams, scenario)
            ));
        }
    }
}