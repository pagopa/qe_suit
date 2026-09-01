package it.pagopa.infrastructure.contract.browser;

import it.frontend.e2e.framework.web.WebPresentationGateway;

import java.util.Objects;
import java.util.function.Supplier;

public final class WebContractValidator {

    private final Supplier<WebPresentationGateway> webPresentationGatewayProvider;

    public WebContractValidator(
            Supplier<WebPresentationGateway> webPresentationGatewayProvider
    ) {
        this.webPresentationGatewayProvider = Objects.requireNonNull(
                webPresentationGatewayProvider,
                "webPresentationGatewayProvider must not be null"
        );
    }

    public WebContractStages.UserStage withContext(
            WebContractContextConfigurer contextConfigurer
    ) {
        Objects.requireNonNull(
                contextConfigurer,
                "contextConfigurer must not be null"
        );

        return new WebContractInvocationBuilder(
                webPresentationGatewayProvider,
                contextConfigurer
        );
    }
}