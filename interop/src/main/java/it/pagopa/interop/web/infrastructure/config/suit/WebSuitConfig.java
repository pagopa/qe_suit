package it.pagopa.interop.web.infrastructure.config.suit;

import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.frontend.e2e.framework.web.adapter.IWebPresentationApiAdapter;
import it.frontend.e2e.framework.web.config.WebSuiteBuilder;
import it.frontend.e2e.framework.web.model.location.Url;
import it.pagopa.infrastructure.browser.AuthenticatedLocatableCapabilityHandler;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "channel.web")
@Getter
@Setter
public class WebSuitConfig {

    private String browser;
    private boolean headless;
    private List<String> arguments;

    public WebPresentationGateway createWebPresentationGateway(
            Environment environment,
            IWebPresentationApiAdapter adapter,
            AuthenticatedLocatableCapabilityHandler handler
    ) {
        return WebSuiteBuilder.builder()
                .withAdapter(() -> adapter)
                .addHandlers(handler)
                .withLocationResolver(location ->
                        Url.of(
                                environment.resolvePlaceholders(location)
                        )
                )
                .build();
    }
}