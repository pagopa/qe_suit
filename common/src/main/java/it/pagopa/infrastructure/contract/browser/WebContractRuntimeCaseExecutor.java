package it.pagopa.infrastructure.contract.browser;

import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.frontend.e2e.framework.web.domain.Page;
import org.springframework.beans.factory.ObjectProvider;

import java.util.Objects;

final class WebContractRuntimeCaseExecutor {

    private final ObjectProvider<WebPresentationGateway> webPresentationGatewayProvider;
    private final WebContractContextConfigurer contextConfigurer;

    WebContractRuntimeCaseExecutor(
            ObjectProvider<WebPresentationGateway> webPresentationGatewayProvider,
            WebContractContextConfigurer contextConfigurer
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

        contextConfigurer.configure();

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