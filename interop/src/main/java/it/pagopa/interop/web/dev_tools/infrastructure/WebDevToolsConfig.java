package it.pagopa.interop.web.dev_tools.infrastructure;

import io.cucumber.spring.ScenarioScope;
import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.pagopa.interop.web.dev_tools.infrastructure.page.DevToolsPage;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
public class WebDevToolsConfig {
    @Bean
    @ScenarioScope
    public DevToolsPage devToolsPage(WebPresentationGateway webPresentationGateway) {
        return webPresentationGateway.bind(DevToolsPage.class);
    }
}
