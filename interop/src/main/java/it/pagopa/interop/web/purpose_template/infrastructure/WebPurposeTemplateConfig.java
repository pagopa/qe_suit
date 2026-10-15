package it.pagopa.interop.web.purpose_template.infrastructure;

import io.cucumber.spring.ScenarioScope;
import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.pagopa.interop.web.purpose_template.infrastructure.suite.page.ConsumerPurposeTemplateCatalogPage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebPurposeTemplateConfig {

    @Bean
    @ScenarioScope
    public ConsumerPurposeTemplateCatalogPage consumerPurposeTemplateCatalogPage(WebPresentationGateway webPresentationGateway) {
        return webPresentationGateway.bind(ConsumerPurposeTemplateCatalogPage.class);
    }
}
