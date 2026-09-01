package it.pagopa.infrastructure.contract.browser;

import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.frontend.e2e.framework.web.domain.Page;

import java.util.Objects;
import java.util.function.Supplier;

final class WebContractRuntimeCaseExecutor {

    private final Supplier<WebPresentationGateway> webPresentationGatewayProvider;
    private final WebSessionProvider contextConfigurer;

    WebContractRuntimeCaseExecutor(
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

    <P extends Page> void execute(
            Class<P> pageType,
            WebScenario<P> scenario
    ) {
        Objects.requireNonNull(pageType, "pageType must not be null");
        Objects.requireNonNull(scenario, "scenario must not be null");

        contextConfigurer.provide();

        WebPresentationGateway gateway = webPresentationGatewayProvider.get();

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