package it.pagopa.interop.new_arch.web.dev_tools.infrastructure.config;

import io.cucumber.spring.ScenarioScope;
import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.pagopa.interop.new_arch.web.dev_tools.infrastructure.page.DevToolsPage;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
@Getter
@Setter
public class WebDevToolsConfig {
    @Bean
    @ScenarioScope
    public DevToolsPage devToolsPage(WebPresentationGateway webPresentationGateway) {
        return webPresentationGateway.bind(DevToolsPage.class);
    }
}
