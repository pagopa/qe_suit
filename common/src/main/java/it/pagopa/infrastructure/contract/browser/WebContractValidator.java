package it.pagopa.infrastructure.contract.browser;

import it.frontend.e2e.framework.web.WebPresentationGateway;
import org.springframework.beans.factory.ObjectProvider;

import java.util.Objects;

public final class WebContractValidator {

    private final ObjectProvider<WebPresentationGateway> webPresentationGatewayProvider;

    public WebContractValidator(
            ObjectProvider<WebPresentationGateway> webPresentationGatewayProvider
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