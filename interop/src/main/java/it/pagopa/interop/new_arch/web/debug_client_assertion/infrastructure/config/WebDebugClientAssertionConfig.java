package it.pagopa.interop.new_arch.web.debug_client_assertion.infrastructure.config;

import io.cucumber.spring.ScenarioScope;
import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.pagopa.interop.new_arch.web.debug_client_assertion.infrastructure.page.DebugClientAssertionPage;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
@Getter
@Setter
public class WebDebugClientAssertionConfig {
    @Bean
    @ScenarioScope
    public DebugClientAssertionPage debugClientAssertionPage(WebPresentationGateway webPresentationGateway) {
        return webPresentationGateway.bind(DebugClientAssertionPage.class);
    }
}
